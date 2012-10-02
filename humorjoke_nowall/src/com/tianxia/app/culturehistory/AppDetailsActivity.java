package com.tianxia.app.humorjoke;

import android.content.Intent;

import android.os.Bundle;

import android.view.View;

import android.widget.LinearLayout.LayoutParams;

import com.tianxia.lib.baseworld2.activity.DetailsActivity;
import com.tianxia.lib.baseworld2.BaseApplication;
import com.tianxia.lib.baseworld2.utils.PreferencesUtils;

import net.youmi.android.AdView;

public class AppDetailsActivity extends DetailsActivity {

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void displayAd() {
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
}
