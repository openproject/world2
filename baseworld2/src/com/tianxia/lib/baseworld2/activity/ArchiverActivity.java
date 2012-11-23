package com.tianxia.lib.baseworld2.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianxia.lib.baseworld2.BaseApplication;
import com.tianxia.lib.baseworld2.R;

public class ArchiverActivity extends BaseActivity
    implements View.OnClickListener {

    private TextView mAppTitle;
    private Button mAppHeaderBack;
    private View mAppHeaderBackDivider;
    private TextView mAppNotice;

    private LinearLayout mPageColumn1;
    private LinearLayout mPageColumn2;
    private LinearLayout mPageColumn3;
    private LinearLayout mPageColumn4;
    private LinearLayout mPageColumn5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContent();
    }

    private void initContent() {
        setContentView(R.layout.archiver);
        mAppTitle = (TextView) findViewById(R.id.app_title);
        mAppHeaderBack = (Button) findViewById(R.id.app_header_back);
        mAppHeaderBackDivider = findViewById(R.id.app_header_back_divider);
        mAppNotice = (TextView) findViewById(R.id.app_notice_text);

        mAppTitle.setText(R.string.archiver_title);
        mAppNotice.setText(R.string.archiver_notice_text);
        mAppHeaderBack.setVisibility(View.VISIBLE);
        mAppHeaderBackDivider.setVisibility(View.VISIBLE);
        mAppHeaderBack.setOnClickListener(this);

        mPageColumn1 = (LinearLayout) findViewById(R.id.page_column_1);
        mPageColumn2 = (LinearLayout) findViewById(R.id.page_column_2);
        mPageColumn3 = (LinearLayout) findViewById(R.id.page_column_3);
        mPageColumn4 = (LinearLayout) findViewById(R.id.page_column_4);
        mPageColumn5 = (LinearLayout) findViewById(R.id.page_column_5);
        for (int i = 0; i < BaseApplication.mMaxPage; i++) {
            Button btn = new Button(this);
            btn.setText((i+1) + "");
            if (i % 5 == 0) {
                mPageColumn1.addView(btn);
            } else if (i % 5 == 1) {
                mPageColumn2.addView(btn);
            } else if (i % 5 == 2) {
                mPageColumn3.addView(btn);
            } else if (i % 5 == 3) {
                mPageColumn4.addView(btn);
            } else if (i % 5 == 4) {
                mPageColumn5.addView(btn);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mAppHeaderBack) {
            onBackPressed();
        }
    }

}
