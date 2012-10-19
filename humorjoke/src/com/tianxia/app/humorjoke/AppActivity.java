package com.tianxia.app.humorjoke;

import android.content.Intent;
import android.view.View;

import android.os.Bundle;

import com.tianxia.lib.baseworld2.activity.MainActivity;
import com.tianxia.lib.baseworld2.BaseApplication;

import com.waps.AdView;
import com.waps.AppConnect;
import com.waps.UpdatePointsNotifier;
import com.tianxia.lib.baseworld2.utils.PreferencesUtils;

public class AppActivity extends MainActivity implements UpdatePointsNotifier{

    private AdView mAdView;

    @Override
    public void displayAd() {
        //获取积分
        AppConnect.getInstance(this).getPoints(this);
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

    //获取成功
    @Override
    public void getUpdatePoints(String currencyName, int pointTotal) {
        if (pointTotal >= AppApplication.NO_AD_FOREVER_SPEND_MAX) {
            return;
        }

        if (pointTotal < AppApplication.NO_AD_SPEND_PER_DAY) {
            runOnUiThread(new Runnable () {
                public void run() {
                    try {
                        mAdContainer.removeAllViews();
                        mAdView = new AdView(AppActivity.this,mAdContainer);
                        mAdView.DisplayAd();
                    } catch (Exception e) {
                        mAdContainer.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }
            });
        } else {

            long last_time = PreferencesUtils.getLongPreference(this,
                   AppApplication.SHARE_CREDITS_LAST_TIME,
                   0);
            if (System.currentTimeMillis() - last_time > 1000*60*60*24) {
                //spent 25 credits will keep no ad one day
                AppConnect.getInstance(this).spendPoints(AppApplication.NO_AD_SPEND_PER_DAY, this);
                PreferencesUtils.setLongPreference(this,
                        AppApplication.SHARE_CREDITS_LAST_TIME,
                        System.currentTimeMillis());
            }
        }
    }

    //获取失败
    @Override
    public void getUpdatePointsFailed(String error) {
    }

    @Override
    public void onDestroy() {
        AppConnect.getInstance(this).finalize();
        super.onDestroy();
    }
}
