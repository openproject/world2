package com.tianxia.lib.baseworld2.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.ClipboardManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tianxia.lib.baseworld2.BaseApplication;
import com.tianxia.lib.baseworld2.R;
import com.tianxia.lib.baseworld2.cache.ConfigCache;
import com.tianxia.lib.baseworld2.sync.http.AsyncHttpClient;
import com.tianxia.lib.baseworld2.sync.http.AsyncHttpResponseHandler;
import com.tianxia.lib.baseworld2.upgrade.AppUpgradeService;
import com.tianxia.lib.baseworld2.utils.DownloadUtils;
import com.tianxia.lib.baseworld2.utils.FileUtils;
import com.tianxia.lib.baseworld2.utils.NetworkUtils;
import com.tianxia.lib.baseworld2.utils.PreferencesUtils;
import com.tianxia.lib.baseworld2.utils.StringUtils;
import com.tianxia.lib.baseworld2.widget.RefreshListView;
import com.tianxia.lib.baseworld2.widget.RefreshListView.RefreshListener;
import com.tianxia.widget.image.SmartImageView;
import com.umeng.fb.NotificationType;
import com.umeng.fb.UMFeedbackService;

public class MainActivity extends AdapterActivity<StatuInfo>
    implements RefreshListener,View.OnClickListener {

    protected ViewGroup mNavContainerView;

    private ProgressBar mWebViewProgressBar;
    private WebView mWebView;

    private Button mAppHeaderMenu;
    private View mAppHeaderDivider;
    private View mAppHeaderOptions;
    private View mAppHeaderBackDivider1;

    private int pageIndex = 0;

    protected LinearLayout mAdContainer;

    private int mLatestVersionCode = 0;
    private String mLatestVersionUpdate = null;
    private String mLatestVersionDownload = null;

    private View mAppNoticeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setInfomationList();

        UMFeedbackService.enableNewReplyNotification(this, NotificationType.NotificationBar);

        listView .setOnCreateContextMenuListener(this);

        mAdContainer = (LinearLayout) findViewById(R.id.ad_container);
        displayAd();

        displayNotice();
    }

    public static final String SHARE_NOTICE_LAST_TIME = "notice_time";
    private void displayNotice() {
        mAppNoticeView = findViewById(R.id.app_notice);
        if (BaseApplication.isForbiddenAdWall()) {
            mAppNoticeView.setVisibility(View.GONE);
            return;
        }
        long last_time = PreferencesUtils.getLongPreference(this,
                    SHARE_NOTICE_LAST_TIME,
                    0);
        if (System.currentTimeMillis() - last_time > 1000*60*60*24) {
            mAppNoticeView.setVisibility(View.VISIBLE);
            PreferencesUtils.setLongPreference(this,
                    SHARE_NOTICE_LAST_TIME,
                    System.currentTimeMillis());
        } else {
            mAppNoticeView.setVisibility(View.GONE);
        }
    }

    private void setInfomationList() {
        String cacheConfigString = ConfigCache.getUrlCache(BaseApplication.mServerLatestUrl);
        if (cacheConfigString != null) {
            try {
                showInfomationList(cacheConfigString);
                checkNewVersion(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // if network is unavaliable, just show fail at once
            if (BaseApplication.mNetWorkState == NetworkUtils.NETWORN_NONE) {
                listView.setAdapter(null);
                showFailEmptyView();
                return;
            }

            AsyncHttpClient client = new AsyncHttpClient();
            client.get(BaseApplication.mServerLatestUrl, new AsyncHttpResponseHandler(){

                @Override
                public void onStart() {
                }

                @Override
                public void onSuccess(String result){
                    try {
                        showInfomationList(result);
                        ConfigCache.setUrlCache(result, BaseApplication.mServerLatestUrl);
                        checkNewVersion(false);
                    } catch (Exception e) {
                        listView.setAdapter(null);
                        showFailEmptyView();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Throwable arg0) {
                    listView.setAdapter(null);
                    showFailEmptyView();
                }

            });
        }
    }

    private void moreInfomationList(int pageIndex) {
        final String pageUrl = BaseApplication.mServerPageUrl + pageIndex + ".json";
        String cacheConfigString = ConfigCache.getUrlCache(pageUrl);
        if (cacheConfigString != null) {
            try {
                showInfomationList(cacheConfigString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            ((RefreshListView)listView).finishFootView();
        } else {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(pageUrl, new AsyncHttpResponseHandler(){

                @Override
                public void onSuccess(String result){
                    try {
                        showInfomationList(result);
                        ConfigCache.setUrlCache(result, pageUrl);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    ((RefreshListView)listView).finishFootView();
                }

                @Override
                public void onFailure(Throwable arg0) {
                    ((RefreshListView)listView).finishFootView();
                    Toast.makeText(MainActivity.this, R.string.app_loading_fail, Toast.LENGTH_SHORT).show();
                    arg0.printStackTrace();
                }

            });
        }
    }

    private void showInfomationList(String result) throws JSONException {
        JSONObject statusConfig = new JSONObject(result);

        mLatestVersionCode = statusConfig.optInt("version-code");
        mLatestVersionUpdate = statusConfig.optString("version-update");
        mLatestVersionDownload = BaseApplication.mDomain
                + statusConfig.optString("version-download");
        if (mLatestVersionDownload != null) {
            BaseApplication.mApkDownloadUrl = mLatestVersionDownload;
        }

        JSONArray statuList = statusConfig.getJSONArray("statuses");
        StatuInfo statuInfo = null;
        for (int i = statuList.length() - 1; i >= 0; i--) {
            statuInfo = new StatuInfo();
            statuInfo.created = statuList.getJSONObject(i).optString("created_at");
            statuInfo.avatar = statuList.getJSONObject(i).getString("avatar");
            statuInfo.name = statuList.getJSONObject(i).getString("name");
            statuInfo.author = statuList.getJSONObject(i).getString("author");
            statuInfo.text = statuList.getJSONObject(i).getString("text");
            statuInfo.id = statuList.getJSONObject(i).getLong("id");
            statuInfo.pic_thumbnail = statuList.getJSONObject(i).optString("pic_thumbnail");
            statuInfo.pic_middle = statuList.getJSONObject(i).optString("pic_middle");
            statuInfo.pic_original = statuList.getJSONObject(i).optString("pic_original");
            statuInfo.from = statuList.getJSONObject(i).optString("from");
            statuInfo.type = statuList.getJSONObject(i).optString("type");
            statuInfo.ref = statuList.getJSONObject(i).opt("ref");

            // for simpler using later, fill the status info extend attribute
            if (statuInfo.type != null && !"".equals(statuInfo.type)) {
                String[] subType = statuInfo.type.split(",");
                if (subType.length > 0 && "1".equals(subType[0].trim())) {
                    statuInfo.isGood = true;
                }
                if (subType.length > 1 && "1".equals(subType[1].trim())) {
                    statuInfo.isSetSimple = true;
                }
                if (subType.length > 2 && "1".equals(subType[2].trim())) {
                    statuInfo.isNewVersion = true;
                }
            }

            listData.add(statuInfo);
        }
        if (pageIndex == 0) {
            adapter = new Adapter(MainActivity.this);
            listView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        pageIndex = statusConfig.getInt("page");
        BaseApplication.mMaxPage = pageIndex - 1;
        if (pageIndex == 1) {
            // if pageIndex == 1 means the page is the last page
            // so do not need show More FooterView any more
            ((RefreshListView) listView).removeFootView();
        }
    }

    @Override
    protected void setLayoutView() {
        setContentView(R.layout.main);
        setListView(R.id.main_list);

        //init the menu widget
        mAppHeaderMenu = (Button) findViewById(R.id.app_header_menu);
        mAppHeaderDivider = findViewById(R.id.app_header_divider);
        mAppHeaderOptions = findViewById(R.id.app_header_options);
        mAppHeaderBackDivider1 = findViewById(R.id.app_header_back_divider1);
        //show menu items
        mAppHeaderMenu.setVisibility(View.VISIBLE);
        mAppHeaderDivider.setVisibility(View.VISIBLE);
        mAppHeaderOptions.setVisibility(View.VISIBLE);
        mAppHeaderBackDivider1.setVisibility(View.VISIBLE);

        //set menu click listener
        mAppHeaderMenu.setOnClickListener(this);
        mAppHeaderOptions.setOnClickListener(this);
        ((RefreshListView) listView).setOnRefreshListener(this);

        mWebViewProgressBar = (ProgressBar) findViewById(R.id.main_webview_progress);
        mWebView = (WebView) findViewById(R.id.main_webview);

        mNavContainerView = (ViewGroup) findViewById(R.id.nav_container);
        initNavs();

        showLoadingEmptyView();
    }

    private String[] mNavValue;
    private String[] mNavString;
    private List<Button> mNavButtons;
    private int mNavClick = 0;
    protected void initNavs() {
        mNavValue = getResources().getStringArray(R.array.nav_value);
        mNavString = getResources().getStringArray(R.array.nav_text);

        mNavButtons = new ArrayList<Button>();
        for (int i = 0; i < mNavString.length; i++) {
           final Button textView = new Button(this);
           textView.setTextColor(getResources().getColor(R.color.app_nav_text_color));
           textView.setText(mNavString[i]);
           textView.setTag(mNavValue[i]);
           textView.setGravity(Gravity.CENTER);
           textView.setPadding(12, 8, 12, 8);
           if ("index".equals(textView.getTag())) {
               textView.setBackgroundResource(R.drawable.app_nav_selected);
               textView.setTextSize(18);
           } else {
               textView.setBackgroundResource(0);
               textView.setTextSize(15);
           }
           textView.setOnClickListener(new View.OnClickListener() {
               public void onClick(View v) {
                   mNavClick = 0;
                   if (!"option".equals(textView.getTag())) {
                       for (Button btn : mNavButtons) {
                           btn.setBackgroundResource(0);
                           btn.setTextSize(15);
                       }
                   }
                   if ("index".equals(textView.getTag())) {
                       listView.setVisibility(View.VISIBLE);
                   } else if ("option".equals(textView.getTag())) {
                       gotoOptions();
                       overridePendingTransition(R.anim.slide_from_left_in, R.anim.silde_from_left_out);
                       return;
                   } else {
                       hideLoadingEmptyView();
                       listView.setVisibility(View.GONE);
                       mWebViewProgressBar.setVisibility(View.VISIBLE);
                       mWebView.setWebChromeClient(new WebChromeClient() {  
                           public void onProgressChanged(WebView view, int progress) {  
                               mWebViewProgressBar.setProgress(progress);
                               if (progress == 100) {
                                   mWebViewProgressBar.setVisibility(View.GONE);
                               }
                           }  
                       }); 
                       mWebView.setWebViewClient(new WebViewClient(){
                           public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                               mNavClick++;
                               mWebViewProgressBar.setVisibility(View.VISIBLE);
                               mWebView.loadUrl(url);
                               return true;
                           }
                       });

                       mWebView.loadUrl(String.valueOf(v.getTag()));
                   }
                   v.setBackgroundResource(R.drawable.app_nav_selected);
                   ((TextView)v).setTextSize(18);
               }
           });
           mNavButtons.add(textView);
        }

        LinearLayout.LayoutParams lp =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.FILL_PARENT);
        lp.gravity = Gravity.CENTER;
        for (Button btn : mNavButtons) {
            mNavContainerView.addView(btn,lp);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mAppHeaderMenu) {
            //setting
            gotoSetting();
        } else if (v == mAppHeaderOptions) {
            gotoOptions();
            overridePendingTransition(R.anim.slide_from_left_in, R.anim.silde_from_left_out);
        }
    }

    @Override
    protected View getView(int position, View convertView) {
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(this).inflate(R.layout.main_list_item, null);
            holder.itemName = (TextView) convertView.findViewById(R.id.item_name);
            holder.itemText = (TextView) convertView.findViewById(R.id.item_text);
            holder.itemDay = (TextView) convertView.findViewById(R.id.item_day);
            holder.itemMonth = (TextView) convertView.findViewById(R.id.item_month);
            holder.itemGood = convertView.findViewById(R.id.item_good);
            holder.itemSet = convertView.findViewById(R.id.item_set);
            holder.itemPic = convertView.findViewById(R.id.item_pic);
            holder.itemThumbnail = (SmartImageView) convertView.findViewById(R.id.item_thumbnail);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.itemGood.setVisibility(View.GONE);
        holder.itemSet.setVisibility(View.GONE);
        holder.itemName.setText(listData.get(position).name);
        holder.itemName.getPaint().setFakeBoldText(true);

        String dateString = listData.get(position).created;
        if (dateString != null && !"".equals(dateString)) {
            try {
                setMonthAndDay(holder.itemMonth, dateString.split("-")[1], holder.itemDay, dateString.split("-")[2]);
            } catch (Exception e) {
                e.printStackTrace();
                setMonthAndDay(holder.itemMonth, null, holder.itemDay, null);
            }
        } else {
            setMonthAndDay(holder.itemMonth, null, holder.itemDay, null);
        }

        if (listData.get(position).isGood) {
            holder.itemGood.setVisibility(View.VISIBLE);
        }

        if (listData.get(position).isSetSimple) {
            holder.itemMonth.setText("合集");
            holder.itemSet.setVisibility(View.VISIBLE);
        }

        holder.itemText.setText(listData.get(position).text);

        if (listData.get(position).pic_thumbnail != null && !"".equals(listData.get(position).pic_thumbnail)) {
            holder.itemThumbnail.setImageUrl(listData.get(position).pic_thumbnail, R.drawable.icon, 0);
            holder.itemThumbnail.setVisibility(View.VISIBLE);
            holder.itemPic.setVisibility(View.VISIBLE);
        } else {
            holder.itemThumbnail.setVisibility(View.GONE);
            holder.itemPic.setVisibility(View.GONE);
        }

        return convertView;
    }

    static class ViewHolder {
        public TextView itemName;
        public TextView itemText;
        public SmartImageView itemThumbnail;
        public TextView itemMonth;
        public TextView itemDay;
        public View itemGood;
        public View itemSet;
        public View itemPic;
    }

    private void setMonthAndDay(TextView mouthView, String mouth, TextView dayView, String day) {
        if (mouth != null) {
            mouthView.setText(mouth + "月");
            dayView.setText(day);
        } else {
            Calendar cal = Calendar.getInstance();
            mouthView.setText(cal.get(Calendar.MONTH) + "月");
            dayView.setText(cal.get(Calendar.DAY_OF_MONTH) + "");
        }
    }

    @Override
    protected void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        StatuInfo statuInfo = listData.get(position - 1);
        if (statuInfo.isNewVersion) {
            checkNewVersion(true);
            return;
        }
        if (statuInfo.isSetSimple &&  statuInfo.ref != null) {
            gotoRefSet(Integer.valueOf(String.valueOf(statuInfo.ref)));
            return;
        }
        gotoDetails(position - 1);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(getString(R.string.main_options));
        menu.add(0, 1, 1, getString(R.string.main_options_share));
        menu.add(0, 2, 1, getString(R.string.main_options_copy));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        //get item position
        ContextMenuInfo info = item.getMenuInfo();
        AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterContextMenuInfo) info;
        int position = contextMenuInfo.position - 1;

        switch (item.getItemId()) {
            case 1:
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.main_options_share_title));
                intent.putExtra(Intent.EXTRA_TEXT, listData.get(position).text);
                startActivity(Intent.createChooser(intent, getString(R.string.setting_share_app_title)));
                break;
            case 2:
                ClipboardManager cm = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(listData.get(position).text);
                Toast.makeText(this, R.string.main_options_copy_toast, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public Object refreshing() {
        if (BaseApplication.mNetWorkState == NetworkUtils.NETWORN_NONE) {
            return null;
        }

        String result = null;
        if (BaseApplication.mSdcardDataDir == null) {
            BaseApplication.mSdcardDataDir = Environment.getExternalStorageDirectory().getPath()
                    +  "/" + BaseApplication.mAppId + "/config/";
        }
        File file = new File(BaseApplication.mSdcardDataDir + "/" + StringUtils.replaceUrlWithPlus(BaseApplication.mServerLatestUrl));
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        try {
            DownloadUtils.download(BaseApplication.mServerLatestUrl, file, false, null);
            result = FileUtils.readTextFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void refreshed(Object obj) {
        if (obj != null) {
            listData.clear();
            pageIndex = 0;
            ((RefreshListView)listView).addFootView();
            try {
                showInfomationList((String)obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void more() {
        if (pageIndex > 1) {
            moreInfomationList(pageIndex - 1);
        } else {
            Toast.makeText(this, "加载完毕", Toast.LENGTH_SHORT).show();
            ((RefreshListView)listView).removeFootView();
        }
    }

    /**
     * check app new version
     * @param isManual: app auto-detect upgrade or user hand click to download in list
     */
    public void checkNewVersion(boolean isManual){
        if (BaseApplication.mVersionCode < mLatestVersionCode
                && (BaseApplication.mShowUpdate || isManual)) {
            new AlertDialog.Builder(this)
                .setTitle(R.string.check_new_version)
                .setMessage(mLatestVersionUpdate)
                .setPositiveButton(R.string.app_upgrade_confirm, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.this, AppUpgradeService.class);
                        intent.putExtra("downloadUrl", mLatestVersionDownload);
                        startService(intent);
                    }
                })
                .setNegativeButton(R.string.app_upgrade_cancel, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
            BaseApplication.mShowUpdate = false;
        }

        if (BaseApplication.mVersionCode >= mLatestVersionCode && isManual) {
            Toast.makeText(this, R.string.check_new_version_latest, Toast.LENGTH_SHORT).show();
        }
    }

    protected void displayAd() {
        // you can override this method to custom ad
    }

    protected void gotoSetting() {
        // you can override this method to custom setting activity
    }

    protected void gotoDetails(int position) {
        // you can override this method to custom details activity
    }

    protected void gotoOptions() {
        // you can override this method to custom options activity
        Intent intent  = new Intent(this, OptionsActivity.class);
        startActivity(intent);
    }

    protected void gotoRefSet(int season) {
        Intent intent = new Intent(this, RefSetActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (mNavClick > 0) {
            mNavClick--;
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

}
