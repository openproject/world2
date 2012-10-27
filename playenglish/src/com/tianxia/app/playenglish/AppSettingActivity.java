package com.tianxia.app.playenglish;

import android.view.View;

import com.tianxia.lib.baseworld2.activity.SettingActivity;
import com.waps.AppConnect;
import com.waps.UpdatePointsNotifier;

public class AppSettingActivity extends SettingActivity implements UpdatePointsNotifier{

    @Override
    public void showAdOffers() {
         AppConnect.getInstance(this).showOffers(this);
    }

    @Override
    public void showAdCredits() {
        //获取积分
        AppConnect.getInstance(this).getPoints(this);
    }

    //获取成功
    @Override
    public void getUpdatePoints(String currencyName, final int pointTotal) {
        runOnUiThread(new Runnable () {
            public void run() {
                try {
                    mSettingItemAd_Credits.setText(String.valueOf(pointTotal));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //获取失败
    @Override
    public void getUpdatePointsFailed(String error) {
        runOnUiThread(new Runnable () {
            public void run() {
                try {
                    mSettingItemAd_Credits.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
