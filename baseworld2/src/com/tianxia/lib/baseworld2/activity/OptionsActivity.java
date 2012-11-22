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
    private View mAppMenu;
    protected LinearLayout mAdContainer;

    private View mNavBaseExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContent();

    }

    protected void displayAd() {
    }

    private void initContent() {
        setContentView(R.layout.options);

        mAppTitle = (TextView) findViewById(R.id.app_title);
        mAppTitle.setText(R.string.options_title);
        mAppTitle.setGravity(Gravity.CENTER);
        mAppMenu = findViewById(R.id.app_header_menu);
        mAppMenu.setOnClickListener(this);

        mNavBaseExit = findViewById(R.id.item_base_exit);
        mNavBaseExit.setOnClickListener(this);

        mAdContainer = (LinearLayout) findViewById(R.id.ad_container);
    }

    @Override
    public void onClick(View v) {
        if (v == mNavBaseExit || v == mAppMenu) {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_right_in, R.anim.silde_from_right_out);
    }
}
