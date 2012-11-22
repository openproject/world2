package com.tianxia.app.humorjoke;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tianxia.lib.baseworld2.activity.OptionsActivity;

public class AppOptionsActivity extends OptionsActivity {

    private Button mItemPoints;
    private TextView mAppStatusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mItemPoints = (Button) findViewById(R.id.item_points);
        mItemPoints.setOnClickListener(this);

        mAppStatusText = (TextView) findViewById(R.id.status_text);
        mAppStatusText.setText("你的当前积分为" + AppApplication.mAdPoints + "分");
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }
}
