package com.tianxia.lib.baseworld2.juzi;

import android.os.Bundle;
import android.view.View;

import com.juzi.main.AdView;
import com.tianxia.lib.baseworld2.activity.DetailsActivity;

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
            mAdContainer.addView(mAdView);
        } catch (Exception e) {
            mAdContainer.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }
}
