package com.tianxia.lib.baseworld2.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianxia.lib.baseworld2.R;

public class OptionsActivity extends BaseActivity
    implements View.OnClickListener {

    private TextView mAppTitle;
    protected LinearLayout mAdContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options);

        mAppTitle = (TextView) findViewById(R.id.app_title);
        mAppTitle.setText(R.string.options_title);
        mAppTitle.setGravity(Gravity.CENTER);

        initContent();

        mAdContainer = (LinearLayout) findViewById(R.id.ad_container);
    }

    protected void displayAd() {
    }

    private void initContent() {
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_right_in, R.anim.silde_from_right_out);
    }
}
