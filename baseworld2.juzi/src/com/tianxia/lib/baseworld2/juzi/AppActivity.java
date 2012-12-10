package com.tianxia.lib.baseworld2.juzi;

import android.content.Intent;
import android.os.Bundle;

import com.juzi.main.AdView;
import com.juzi.main.AppConnect;
import com.tianxia.lib.baseworld2.activity.MainActivity;

public class AppActivity extends MainActivity {

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppConnect.getInstance(this);
    }

    @Override
    public void displayAd() {
        //获取积分
        //AppConnect.getInstance(this).getPoints(this);

        mAdView = new AdView(AppActivity.this);
        mAdContainer.addView(mAdView);
    }

    @Override
    public void gotoSetting() {
        Intent intent = new Intent(this, AppSettingActivity.class);
        startActivity(intent);
    }
}
