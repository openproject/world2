package com.tianxia.lib.baseworld2.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tianxia.lib.baseworld2.R;

public class RefSetSimpleActivity extends AdapterActivity<StatuInfo>
    implements View.OnClickListener {

    private TextView mAppTitle;
    private Button mAppHeaderBack;
    private View mAppHeaderBackDivider;

    private int mSeason;
    private String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setLayoutView() {
        setContentView(R.layout.ref_set_simple);
        setListView(R.id.ref_set_simple_list);

        mSeason = getIntent().getIntExtra("season", 0);
        mTitle = getIntent().getStringExtra("title");

        mAppTitle = (TextView) findViewById(R.id.app_title);
        mAppHeaderBack = (Button) findViewById(R.id.app_header_back);
        mAppHeaderBackDivider = findViewById(R.id.app_header_back_divider);

        mAppTitle.setText(mTitle);
        mAppHeaderBack.setVisibility(View.VISIBLE);
        mAppHeaderBackDivider.setVisibility(View.VISIBLE);

        mAppHeaderBack.setOnClickListener(this);
    }

    @Override
    protected View getView(int position, View convertView) {
        return null;
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
