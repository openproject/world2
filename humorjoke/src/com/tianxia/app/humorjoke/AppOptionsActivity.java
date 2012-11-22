package com.tianxia.app.humorjoke;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tianxia.lib.baseworld2.activity.OptionsActivity;
import com.waps.AppConnect;
import com.waps.UpdatePointsNotifier;

public class AppOptionsActivity extends OptionsActivity implements UpdatePointsNotifier{

    private Button mItemPoints;
    private TextView mAppStatusText;

    private boolean mNeedRefreshPoint = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mItemPoints = (Button) findViewById(R.id.item_points);
        mItemPoints.setOnClickListener(this);

        mAppStatusText = (TextView) findViewById(R.id.status_text);
        mAppStatusText.setText(
                getString(R.string.options_status_text, AppApplication.mAdPoints + ""));
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == mItemPoints) {
            mNeedRefreshPoint = true;
            AppConnect.getInstance(this).showOffers(this);
        }
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
                    mAppStatusText.setText(
                            getString(R.string.options_status_text, AppApplication.mAdPoints + ""));
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
    }
}
