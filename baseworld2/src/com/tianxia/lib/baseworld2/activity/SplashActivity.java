package com.tianxia.lib.baseworld2.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.tianxia.lib.baseworld2.BaseApplication;
import com.tianxia.lib.baseworld2.R;

public class SplashActivity extends BaseActivity {

    private TextView mVersionNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        mVersionNameText = (TextView) findViewById(R.id.version_name);
        mVersionNameText.setText(BaseApplication.mVersionName);
    }
}
