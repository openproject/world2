package com.tianxia.lib.baseworld2.activity;

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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.feedback.NotificationType;
import com.feedback.UMFeedbackService;

import com.tianxia.lib.baseworld2.BaseApplication;
import com.tianxia.lib.baseworld2.cache.ConfigCache;
import com.tianxia.lib.baseworld2.R;
import com.tianxia.lib.baseworld2.sync.http.AsyncHttpClient;
import com.tianxia.lib.baseworld2.sync.http.AsyncHttpResponseHandler;
import com.tianxia.lib.baseworld2.upgrade.AppUpgradeService;
import com.tianxia.lib.baseworld2.utils.DownloadUtils;
import com.tianxia.lib.baseworld2.utils.EmptyViewUtils;
import com.tianxia.lib.baseworld2.utils.FileUtils;
import com.tianxia.lib.baseworld2.utils.PreferencesUtils;
import com.tianxia.lib.baseworld2.utils.StringUtils;
import com.tianxia.lib.baseworld2.widget.RefreshListView;
import com.tianxia.lib.baseworld2.widget.RefreshListView.RefreshListener;
import com.tianxia.widget.image.SmartImageView;

import java.io.File;

import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AdapterActivity<StatuInfo>
    implements RefreshListener,View.OnClickListener {

    private TextView mItemName;
    private TextView mItemDate;
    private TextView mItemText;
    private SmartImageView mItemThumbnail;
    private TextView mItemFrom;
    private TextView mItemMonth;
    private TextView mItemDay;

    private Button mAppHeaderMenu;
    private Button mAppHeaderMenu_1;
    private View mAppHeaderDivider;
    private View mAppHeaderDivider_1;

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

        if (BaseApplication.isForbiddenAdWall()) {
            mAppNoticeView = findViewById(R.id.app_notice);
            mAppNoticeView.setVisibility(View.GONE);
        }
    }

    private void setInfomationList() {
        String cacheConfigString = ConfigCache.getUrlCache(BaseApplication.mServerLatestUrl);
        if (cacheConfigString != null) {
            showInfomationList(cacheConfigString);
            checkNewVersion();
        } else {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(BaseApplication.mServerLatestUrl, new AsyncHttpResponseHandler(){

                @Override
                public void onStart() {
                }

                @Override
                public void onSuccess(String result){
                    try {
                        ConfigCache.setUrlCache(result, BaseApplication.mServerLatestUrl);
                        showInfomationList(result);
                        checkNewVersion();
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
        String cacheConfigString = ConfigCache.getUrlCache(BaseApplication.mServerPageUrl + pageIndex + ".json");
        if (cacheConfigString != null) {
            showInfomationList(cacheConfigString);
        } else {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(BaseApplication.mServerPageUrl + pageIndex + ".json", new AsyncHttpResponseHandler(){

                @Override
                public void onSuccess(String result){
                    showInfomationList(result);
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

    private void showInfomationList(String result) {
        try {
            JSONObject statusConfig = new JSONObject(result);

            mLatestVersionCode = statusConfig.optInt("version-code");
            mLatestVersionUpdate = statusConfig.optString("version-update");
            mLatestVersionDownload = BaseApplication.mDomain + statusConfig.optString("version-download");
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
                listData.add(statuInfo);
            }
            if (pageIndex == 0) {
                adapter = new Adapter(MainActivity.this);
                listView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }

            pageIndex = statusConfig.getInt("page");
            if (pageIndex == 1) {
                //if pageIndex == 1 means the page is the last page
                //so do not need show More FooterView any more
                ((RefreshListView)listView).removeFootView();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setLayoutView() {
        setContentView(R.layout.main);
        setListView(R.id.main_list);
        //init the menu widget
        mAppHeaderMenu = (Button) findViewById(R.id.app_header_menu);
        mAppHeaderMenu_1 = (Button) findViewById(R.id.app_header_menu_1);
        mAppHeaderDivider = findViewById(R.id.app_header_divider);
        mAppHeaderDivider_1 = findViewById(R.id.app_header_divider_1);
        //show menu items
        mAppHeaderMenu.setVisibility(View.VISIBLE);
        mAppHeaderMenu_1.setVisibility(View.VISIBLE);
        mAppHeaderDivider.setVisibility(View.VISIBLE);
        mAppHeaderDivider_1.setVisibility(View.VISIBLE);
        //set menu click listener
        mAppHeaderMenu.setOnClickListener(this);
        mAppHeaderMenu_1.setOnClickListener(this);
        ((RefreshListView) listView).setOnRefreshListener(this);

        showLoadingEmptyView();
    }

    @Override
    public void onClick(View v) {
        if (v == mAppHeaderMenu) {
            //setting
            gotoSetting();
        } else if ((v == mAppHeaderMenu_1)) {
            Toast.makeText(this, "正在努力开发中，敬请期待。", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected View getView(int position, View convertView) {
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(this).inflate(R.layout.main_list_item, null);
        }

        mItemName = (TextView) view.findViewById(R.id.item_name);
        mItemName.setText(listData.get(position).name);
        mItemName.getPaint().setFakeBoldText(true);

        mItemMonth = (TextView) view.findViewById(R.id.item_month);
        mItemDay = (TextView) view.findViewById(R.id.item_day);
        String dateString = listData.get(position).created;
        if (dateString != null && !"".equals(dateString)) {
            try {
                setMonthAndDay(dateString.split("-")[1], dateString.split("-")[2]);
            } catch (Exception e) {
                e.printStackTrace();
                setMonthAndDay(null, null);
            }
        } else {
            setMonthAndDay(null, null);
        }

        mItemText = (TextView) view.findViewById(R.id.item_text);
        mItemText.setText(listData.get(position).text);

        mItemThumbnail = (SmartImageView) view.findViewById(R.id.item_thumbnail);
        if (listData.get(position).pic_thumbnail != null && !"".equals(listData.get(position).pic_thumbnail)) {
            mItemThumbnail.setImageUrl(listData.get(position).pic_thumbnail, R.drawable.icon, 0);
            mItemThumbnail.setVisibility(View.VISIBLE);
        } else {
            mItemThumbnail.setVisibility(View.GONE);
        }

        mItemFrom = (TextView) view.findViewById(R.id.item_from);
        if (listData.get(position).from != null && !"".equals(listData.get(position).from)) {
            mItemFrom.setText("来自:" + listData.get(position).from);
        } else {
            mItemFrom.setText("来自:新浪");
        }
        return view;
    }

    private void setMonthAndDay(String mouth, String day) {
        if (mouth != null) {
            mItemMonth.setText(mouth + "月");
            mItemDay.setText(day);
        } else {
            Calendar cal = Calendar.getInstance();
            mItemMonth.setText(cal.get(Calendar.MONTH) + "月");
            mItemDay.setText(cal.get(Calendar.DAY_OF_MONTH) + "");
        }
    }

    @Override
    protected void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
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
        String result = null;
        if (BaseApplication.mSdcardDataDir == null) {
            BaseApplication.mSdcardDataDir = Environment.getExternalStorageDirectory().getPath() +  "/culturehistory/config/";
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
            showInfomationList((String)obj);
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

    public void checkNewVersion(){
        if (BaseApplication.mVersionCode < mLatestVersionCode && BaseApplication.mShowUpdate) {
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
    }

    protected void displayAd() {
    }

    protected void gotoSetting() {
    }

    protected void gotoDetails(int position) {
    }
}
