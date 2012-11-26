package com.tianxia.lib.baseworld2.activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.tianxia.lib.baseworld2.BaseApplication;
import com.tianxia.lib.baseworld2.R;
import com.tianxia.lib.baseworld2.cache.ConfigCache;
import com.tianxia.lib.baseworld2.sync.http.AsyncHttpClient;
import com.tianxia.lib.baseworld2.sync.http.AsyncHttpResponseHandler;
import com.tianxia.lib.baseworld2.utils.NetworkUtils;
import com.tianxia.widget.image.SmartImageView;

public class RefSetSimpleActivity extends AdapterActivity<StatuInfo>
    implements View.OnClickListener {

    private TextView mAppTitle;
    private Button mAppHeaderBack;
    private View mAppHeaderBackDivider;

    private int mSeason;

    private TextView mSeasonTitle;
    private TextView mItemText;
    private SmartImageView mItemMiddle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSeasonList(mSeason);
    }

    @Override
    protected void setLayoutView() {
        setContentView(R.layout.ref_set_simple);
        setListView(R.id.ref_set_simple_list);

        mSeason = getIntent().getIntExtra("index", 0);

        mAppTitle = (TextView) findViewById(R.id.app_title);
        mAppHeaderBack = (Button) findViewById(R.id.app_header_back);
        mAppHeaderBackDivider = findViewById(R.id.app_header_back_divider);

        mAppTitle.setText(getString(R.string.ref_set_simple_title, mSeason));
        mAppHeaderBack.setVisibility(View.VISIBLE);
        mAppHeaderBackDivider.setVisibility(View.VISIBLE);

        mAppHeaderBack.setOnClickListener(this);

        mSeasonTitle = (TextView) findViewById(R.id.ref_set_simple_title);

        showLoadingEmptyView();
    }

    private void setSeasonList(int seasonIndex) {
        final String seasonUrl = BaseApplication.mServerSeasonUrl + seasonIndex + ".json";
        String cacheConfigString = ConfigCache.getUrlCache(seasonUrl);
        if (cacheConfigString != null) {
            try {
                showSeasonList(cacheConfigString);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // if network is unavaliable, just show fail at once
            if (BaseApplication.mNetWorkState == NetworkUtils.NETWORN_NONE) {
                listView.setAdapter(null);
                showFailEmptyView();
                mSeasonTitle.setText(R.string.app_loading_fail);
                return;
            }

            AsyncHttpClient client = new AsyncHttpClient();
            client.get(seasonUrl, new AsyncHttpResponseHandler(){

                @Override
                public void onStart() {
                }

                @Override
                public void onSuccess(String result){
                    try {
                        showSeasonList(result);
                        ConfigCache.setUrlCache(result, seasonUrl);
                    } catch (Exception e) {
                        listView.setAdapter(null);
                        showFailEmptyView();
                        mSeasonTitle.setText(R.string.app_loading_fail);
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Throwable arg0) {
                    listView.setAdapter(null);
                    showFailEmptyView();
                    mSeasonTitle.setText(R.string.app_loading_fail);
                }

            });
        }
    }

    private void showSeasonList(String result) throws JSONException {
        JSONObject statusConfig = new JSONObject(result);

        JSONArray statuList = statusConfig.getJSONArray("statuses");
        StatuInfo statuInfo = null;
        for (int i = 0; i < statuList.length(); i++) {
            statuInfo = new StatuInfo();
            statuInfo.text = statuList.getJSONObject(i).getString("text");
            statuInfo.id = statuList.getJSONObject(i).getLong("id");
            statuInfo.pic_thumbnail = statuList.getJSONObject(i).optString("pic_thumbnail");

            listData.add(statuInfo);
        }

        // update the season title
        String seasonTitle = statusConfig.optString("title");
        mSeasonTitle.setText(seasonTitle);

        adapter = new Adapter(RefSetSimpleActivity.this);
        listView.setAdapter(adapter);
    }

    @Override
    protected View getView(int position, View convertView) {
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(this).inflate(R.layout.ref_set_simle_list_item, null);
        }

        mItemText = (TextView) view.findViewById(R.id.item_text);
        mItemText.setText(listData.get(position).text);

        mItemMiddle = (SmartImageView) view.findViewById(R.id.item_thumbnail);
        if (listData.get(position).pic_thumbnail != null && !"".equals(listData.get(position).pic_thumbnail)) {
            mItemMiddle.setImageUrl(listData.get(position).pic_thumbnail, R.drawable.icon, 0);
            mItemMiddle.setVisibility(View.VISIBLE);
        } else {
            mItemMiddle.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    protected void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        
    }

    @Override
    public void onClick(View v) {
        if (v == mAppHeaderBack) {
            onBackPressed();
        }
    }
}
