package com.tianxia.lib.baseworld2.waps;

import android.view.View;

import com.tianxia.lib.baseworld2.activity.SettingActivity;
import com.waps.AppConnect;
import com.waps.UpdatePointsNotifier;

public class AppSettingActivity extends SettingActivity implements UpdatePointsNotifier{

    private boolean mNeedRefreshPoint = false;

    @Override
    public void showAdOffers() {
         mNeedRefreshPoint = true;
         AppConnect.getInstance(this).showOffers(this);
    }

    @Override
    protected void showSelfApps() {
        AppConnect.getInstance(this).showMore(this);
    }

    @Override
    public void showAdCredits() {
        if (AppApplication.mAdPoints > -1) {
            mSettingItemAd_Credits.setText(AppApplication.mAdPoints + "");
        } else {
            mSettingItemAd_Credits.setText("...");
        }
        //获取积分
        AppConnect.getInstance(this).getPoints(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mNeedRefreshPoint) {
            //获取积分
            AppConnect.getInstance(this).getPoints(this);
        }
    }

    //获取成功
    @Override
    public void getUpdatePoints(String currencyName, final int pointTotal) {
        mNeedRefreshPoint = false;
        AppApplication.mAdPoints = pointTotal;
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
        mNeedRefreshPoint = false;
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
