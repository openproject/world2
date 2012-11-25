package com.tianxia.lib.baseworld2.activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Typeface;
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

public class RefSetActivity extends AdapterActivity<SetSummaryInfo>
    implements View.OnClickListener {

    private static final int REF_SET_TYPE_SIMPLE = 1;

    private TextView mAppTitle;
    private Button mAppHeaderBack;
    private View mAppHeaderBackDivider;

    private View mItemRight;
    private TextView mItemIndex;
    private TextView mItemTitle;
    private TextView mItemSummary;

    private int mIndex; // view index

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIndex = getIntent().getIntExtra("index", 0);

        setSetList();
    }

    @Override
    protected void setLayoutView() {
        setContentView(R.layout.ref_set);
        setListView(R.id.ref_set_list);

        mAppTitle = (TextView) findViewById(R.id.app_title);
        mAppHeaderBack = (Button) findViewById(R.id.app_header_back);
        mAppHeaderBackDivider = findViewById(R.id.app_header_back_divider);

        mAppTitle.setText(R.string.ref_set_title);
        mAppHeaderBack.setVisibility(View.VISIBLE);
        mAppHeaderBackDivider.setVisibility(View.VISIBLE);

        mAppHeaderBack.setOnClickListener(this); 
    }

    private void setSetList() {
        final String setUrl = BaseApplication.mServerSetUrl;
        String cacheConfigString = ConfigCache.getUrlCache(setUrl);
        if (cacheConfigString != null) {
            showSetList(cacheConfigString);
        } else {
            // if network is unavaliable, just show fail at once
            if (BaseApplication.mNetWorkState == NetworkUtils.NETWORN_NONE) {
                listView.setAdapter(null);
//                showFailEmptyView();
                return;
            }

            AsyncHttpClient client = new AsyncHttpClient();
            client.get(setUrl, new AsyncHttpResponseHandler(){

                @Override
                public void onStart() {
                }

                @Override
                public void onSuccess(String result){
                    try {
                        showSetList(result);
                        ConfigCache.setUrlCache(result, setUrl);
                    } catch (Exception e) {
                        listView.setAdapter(null);
//                        showFailEmptyView();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Throwable arg0) {
                    listView.setAdapter(null);
//                    showFailEmptyView();
                }

            });
        }
    }

    private void showSetList(String result) {
        try {
            JSONObject statusConfig = new JSONObject(result);

            JSONArray statuList = statusConfig.getJSONArray("list");
            SetSummaryInfo setSummaryInfo = null;
            for (int i = statuList.length() - 1; i >= 0; i--) {
                setSummaryInfo = new SetSummaryInfo();
                setSummaryInfo.type = statuList.getJSONObject(i).getInt("type");
                setSummaryInfo.index = statuList.getJSONObject(i).getInt("index");
                setSummaryInfo.title = statuList.getJSONObject(i).optString("title");
                setSummaryInfo.summary = statuList.getJSONObject(i).optString("summary");
                setSummaryInfo.right = statuList.getJSONObject(i).getInt("right");

                listData.add(setSummaryInfo);
            }

            adapter = new  Adapter(RefSetActivity.this);
            listView.setAdapter(adapter);

            if (mIndex > 0) {
                listView.setSelection(listData.size() - mIndex);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected View getView(int position, View convertView) {
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(this).inflate(R.layout.ref_set_list_item, null);
        }

        mItemRight = view.findViewById(R.id.item_base_right);
        if (showRightTag(position)) {
            mItemRight.setVisibility(View.VISIBLE);
        } else {
            mItemRight.setVisibility(View.GONE);
        }
        mItemIndex = (TextView) view.findViewById(R.id.item_index);
        mItemIndex.setText("第" + listData.get(position).index + "季");
        mItemIndex.getPaint().setFakeBoldText(true);
        mItemTitle = (TextView) view.findViewById(R.id.item_title);
        mItemTitle.setText(listData.get(position).title);
        mItemSummary = (TextView) view.findViewById(R.id.item_summary);
        mItemSummary.setText("    " + listData.get(position).summary);
        mItemSummary.setTypeface(Typeface.MONOSPACE,Typeface.ITALIC);
        return view;
    }

    protected boolean showRightTag(int position) {
        return false;
    }

    @Override
    protected void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        SetSummaryInfo setSummaryInfo = listData.get(position);
        if (setSummaryInfo.type == REF_SET_TYPE_SIMPLE) {
            Intent intent = new Intent(this, RefSetSimpleActivity.class);
            intent.putExtra("index", setSummaryInfo.index);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mAppHeaderBack) {
            onBackPressed();
        }
    }
}
