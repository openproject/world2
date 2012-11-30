package com.tianxia.app.healthworld;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.tianxia.lib.baseworld2.activity.RefSetActivity;
import com.waps.AppConnect;
import com.waps.UpdatePointsNotifier;

public class AppRefSetActivity extends RefSetActivity implements UpdatePointsNotifier{

    private boolean mNeedRefreshPoint = false;
    private TextView mAppStatusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppStatusText = (TextView) findViewById(R.id.status_text);
        if (AppApplication.mAdPoints < 0) {
            mAppStatusText.setText(R.string.options_status_loading);
            AppConnect.getInstance(this).getPoints(this);
        } else {
            mAppStatusText.setText(
                    getString(R.string.options_status_text, AppApplication.mAdPoints + ""));
        }
    }

    @Override
    protected boolean showRightTag(int position) {
        if (listData.get(position).right > 0) {
            return true;
        }
        return false;
    }

    @Override
    protected void onItemClick(AdapterView<?> adapterView, View view,
            int position, long id) {
        if (AppApplication.mAdPoints >= listData.get(position).right) {
            super.onItemClick(adapterView, view, position, id);
        } else {
            showGetPointsDialog(listData.get(position).right);
        }
    }

    private void showGetPointsDialog(int points) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("积分不够");
        builder.setMessage("查看该项需要积分：" + points
                + ",  \n您的当前积分为：" + AppApplication.mAdPoints
                + ", \n请获取积分后再试，不扣取任何积分，积分到达可永久查看。");
        builder.setPositiveButton("获取积分", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mNeedRefreshPoint = true;
                AppConnect.getInstance(AppRefSetActivity.this).showOffers(AppRefSetActivity.this);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
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
        mAppStatusText.setText(R.string.options_status_fail);
    }
}
