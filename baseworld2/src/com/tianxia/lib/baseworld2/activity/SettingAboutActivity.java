package com.tianxia.lib.baseworld2.activity;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.tianxia.lib.baseworld2.R;

public class SettingAboutActivity extends BaseActivity{

    private static final String UMENG_CHANNEL = "UMENG_CHANNEL";

    private TextView mAppTitle;
    private Button mAppHeaderBack = null;
    private View mAppHeaderBackDivider;

    private TextView mSettingAboutVersionTextView;
    private WebView mSettingAboutLinkWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_about_activity);

        mAppTitle = (TextView) findViewById(R.id.app_title);
        mAppHeaderBack = (Button) findViewById(R.id.app_header_back);
        mAppHeaderBackDivider = findViewById(R.id.app_header_back_divider);

        mAppTitle.setText(R.string.setting_about_title);
        mAppHeaderBack.setVisibility(View.VISIBLE);
        mAppHeaderBackDivider.setVisibility(View.VISIBLE);
        mAppHeaderBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mSettingAboutVersionTextView = (TextView) findViewById(R.id.setting_about_version);
        PackageInfo packageInfo;
        ApplicationInfo appInfo;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS);
            appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            String versionString = packageInfo.versionName
                    + " "
                    + getChannelName(appInfo.metaData.getString(UMENG_CHANNEL));
            mSettingAboutVersionTextView.setText(versionString);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        mSettingAboutLinkWebView = (WebView) findViewById(R.id.setting_about_link);
        mSettingAboutLinkWebView.loadUrl("file:///android_asset/setting_about_link.html");
    }

    private String getChannelName(String channelKey) {
        String result = "";
        if ("dev".equals(channelKey)) { result = "开发版";
        } else if ("official".equals(channelKey)) { result = "官方版";
        } else if ("google".equals(channelKey)) { result = "谷歌版";
        } else if ("appchina".equals(channelKey)) { result = "应用汇版";
        } else if ("waps".equals(channelKey)) { result = "万普版";
        } else if ("gfan".equals(channelKey)) { result = "机锋版";
        } else if ("91".equals(channelKey)) { result = "91版";
        } else if ("hiapk".equals(channelKey)) { result = "安卓版";
        } else if ("goapk".equals(channelKey)) { result = "安智版";
        } else if ("mumayi".equals(channelKey)) { result = "木蚂蚁版";
        } else if ("eoe".equals(channelKey)) { result = "优亿版";
        } else if ("nduo".equals(channelKey)) { result = "N多版";
        } else if ("feiliu".equals(channelKey)) { result = "飞流版";
        } else if ("crossmo".equals(channelKey)) { result = "十字猫版";
        } else if ("huawei".equals(channelKey)) { result = "智汇云版";
        } else if ("qq".equals(channelKey)) { result = "腾讯版";
        } else if ("3g".equals(channelKey)) { result = "3G版";
        } else if ("360".equals(channelKey)) { result = "360版";
        } else if ("baidu".equals(channelKey)) { result = "百度版";
        } else if ("sohu".equals(channelKey)) { result = "搜狐版";
        } else if ("samsung".equals(channelKey)) { result = "三星版";
        } else if ("coolmart".equals(channelKey)) { result = "酷派版";
        } else if ("meizu".equals(channelKey)) { result = "魅族版";
        } else if ("moto".equals(channelKey)) { result = "摩托版";
        } else if ("xiaomi".equals(channelKey)) { result = "小米版";
        } else if ("lenovo".equals(channelKey)) { result = "联想版";
        } else if ("uc".equals(channelKey)) { result = "UC版";
        } else { result = "山寨版";}
        return result;
    }
}
