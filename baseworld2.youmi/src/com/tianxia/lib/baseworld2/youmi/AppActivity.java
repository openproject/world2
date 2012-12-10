package com.tianxia.lib.baseworld2.youmi;

import net.youmi.android.AdManager;
import net.youmi.android.AdView;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;

import com.tianxia.lib.baseworld2.activity.MainActivity;
import com.tianxia.lib.baseworld2.activity.RefSetActivity;

public class AppActivity extends MainActivity {

    private AdView mAdView;

    @Override
    public void displayAd() {
        AdManager.init(this,"c9976bb1456bbfa8", "0d3ae1d6056a542d", 30, false);
        try {
            mAdContainer.removeAllViews();
            mAdView = new AdView(this);
            LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
            mAdContainer.addView(mAdView, params);
        } catch (Exception e) {
            mAdContainer.setVisibility(View.GONE);
            e.printStackTrace();
        }
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
}
