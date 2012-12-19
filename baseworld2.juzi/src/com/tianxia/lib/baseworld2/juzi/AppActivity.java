package com.tianxia.lib.baseworld2.juzi;

import android.content.Intent;
import android.os.Bundle;

import com.juzi.main.AdView;
import com.juzi.main.AppConnect;
import com.tianxia.lib.baseworld2.activity.MainActivity;
import com.tianxia.lib.baseworld2.activity.RefSetActivity;

public class AppActivity extends MainActivity {

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppConnect.getInstance(this);
    }

    @Override
    public void displayAd() {
        mAdView = new AdView(AppActivity.this);
        mAdContainer.addView(mAdView);
    }

    @Override
    public void gotoSetting() {
        Intent intent = new Intent(this, AppSettingActivity.class);
        startActivity(intent);
    }

    @Override
    public void gotoDetails(int position) {
        Intent intent = new Intent(this, AppDetailsActivity.class);
        intent.putExtra("statu", listData.get(position));
        startActivity(intent);
    }

    @Override
    protected void gotoRefSet(int season) {
        Intent intent = new Intent(this, RefSetActivity.class);
        intent.putExtra("index", season);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        AppConnect.getInstance(this).finalize();
        super.onDestroy();
    }
}
