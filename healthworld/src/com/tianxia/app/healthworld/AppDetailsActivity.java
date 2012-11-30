package com.tianxia.app.healthworld;

import android.view.View;

import android.os.Bundle;

import com.tianxia.lib.baseworld2.activity.DetailsActivity;

import com.waps.AdView;
import com.waps.AppConnect;
import com.waps.UpdatePointsNotifier;
import com.tianxia.lib.baseworld2.utils.PreferencesUtils;

public class AppDetailsActivity extends DetailsActivity implements UpdatePointsNotifier{

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void displayAd() {
        //获取积分
        AppConnect.getInstance(this).getPoints(this);
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
                        mAdView = new AdView(AppDetailsActivity.this,mAdContainer);
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
}
