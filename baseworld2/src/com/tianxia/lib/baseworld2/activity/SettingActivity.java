package com.tianxia.lib.baseworld2.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tianxia.lib.baseworld2.BaseApplication;
import com.tianxia.lib.baseworld2.R;
import com.tianxia.lib.baseworld2.alipay.AlixPay;
import com.tianxia.lib.baseworld2.cache.ConfigCache;
import com.tianxia.lib.baseworld2.sync.http.AsyncHttpClient;
import com.tianxia.lib.baseworld2.sync.http.AsyncHttpResponseHandler;
import com.tianxia.lib.baseworld2.upgrade.AppUpgradeService;
import com.tianxia.lib.baseworld2.utils.NetworkUtils;
import com.umeng.fb.UMFeedbackService;

public class SettingActivity extends BaseActivity
    implements View.OnClickListener {

    private TextView mAppTitle;
    private Button mAppHeaderBack;
    private View mAppHeaderBackDivider;

    private View mSettingItemAd;
    private View mSettingItemSelfApp;
    private View mSettingItemDonate;
    private View mSettingItemShare;
    private View mSettingItemMark;
    private View mSettingItemClear;
    private View mSettingItemUpgrade;
    private View mSettingItemSuggest;
    private View mSettingItemAbout;

    protected TextView mSettingItemAd_Credits;

    private int mLatestVersionCode = 0;
    private String mLatestVersionUpdate = null;
    private String mLatestVersionDownload = null;

    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        mAppTitle = (TextView) findViewById(R.id.app_title);
        mAppHeaderBack = (Button) findViewById(R.id.app_header_back);
        mAppHeaderBackDivider = findViewById(R.id.app_header_back_divider);

        mSettingItemAd = findViewById(R.id.setting_item_ad);
        mSettingItemSelfApp = findViewById(R.id.setting_item_selfapp);
        mSettingItemDonate = findViewById(R.id.setting_item_donate);
        mSettingItemShare = findViewById(R.id.setting_item_share);
        mSettingItemMark = findViewById(R.id.setting_item_mark);
        mSettingItemClear = findViewById(R.id.setting_item_clear);
        mSettingItemUpgrade = findViewById(R.id.setting_item_upgrade);
        mSettingItemSuggest = findViewById(R.id.setting_item_suggest);
        mSettingItemAbout = findViewById(R.id.setting_item_about);

        mSettingItemAd_Credits =  (TextView) findViewById(R.id.setting_item_ad_credits);

        mAppTitle.setText(R.string.app_header_menu);
        mAppHeaderBack.setVisibility(View.VISIBLE);
        mAppHeaderBackDivider.setVisibility(View.VISIBLE);

        mAppHeaderBack.setOnClickListener(this);

        mSettingItemAd.setOnClickListener(this);
        mSettingItemSelfApp.setOnClickListener(this);
        mSettingItemDonate.setOnClickListener(this);
        mSettingItemShare.setOnClickListener(this);
        mSettingItemMark.setOnClickListener(this);
        mSettingItemClear.setOnClickListener(this);
        mSettingItemUpgrade.setOnClickListener(this);
        mSettingItemSuggest.setOnClickListener(this);
        mSettingItemAbout.setOnClickListener(this);

        if (BaseApplication.isForbiddenAdWall()) {
            mSettingItemAd.setVisibility(View.GONE);
        } else {
            showAdCredits();
        }
    }

    protected void showAdCredits() {
    }

    protected void showAdOffers() {
    }

    protected void showSelfApps() {
    }

    @Override
    public void onClick(View v) {
        if (v == mAppHeaderBack) {
            onBackPressed();
        } else if (v == mSettingItemAd) {
            //display the ad list
            showAdOffers();
        } else if (v == mSettingItemSelfApp){
            showSelfApps();
        } else if (v == mSettingItemDonate) {

            AlixPay alixPay = new AlixPay(this);
            alixPay.pay();

        } else if (v == mSettingItemShare) {

            Intent intent=new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.setting_share_app_subject));
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.setting_share_app_body) + BaseApplication.mApkDownloadUrl);
            startActivity(Intent.createChooser(intent, getString(R.string.setting_share_app_title)));

        } else if (v == mSettingItemMark) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + getPackageName()));
            startActivity(intent);

        } else if (v == mSettingItemClear) {
            clear();
        } else if (v == mSettingItemUpgrade) {
            upgrade();
        } else if (v == mSettingItemSuggest) {
            UMFeedbackService.openUmengFeedbackSDK(this);
        } else if (v == mSettingItemAbout) {

            Intent intent = new Intent(this, SettingAboutActivity.class);
            startActivity(intent);
        }
    }

    /***
     * clear app cache in sdcard : config, image, download files ...
     */
    private void clear() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setTitle(R.string.setting_clear_title);
        mProgressDialog.setMessage(getString(R.string.setting_clear_text));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        new Thread() {
            @Override
            public void run() {
                ConfigCache.clearCache(null);
                mProgressDialog.cancel();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SettingActivity.this, R.string.setting_clear_success, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }.start();
    }

    public void upgrade() {

        if (BaseApplication.mNetWorkState == NetworkUtils.NETWORN_NONE) {
            Toast.makeText(this, R.string.check_new_version_no_network, Toast.LENGTH_SHORT).show();
            return;
        }

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setTitle(R.string.check_new_version_title);
        mProgressDialog.setMessage(getString(R.string.check_new_version_message));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();

        String cacheConfigString = ConfigCache.getUrlCache(BaseApplication.mServerLatestUrl);
        if (cacheConfigString != null) {
            checkNewVersion(cacheConfigString);
        } else {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(BaseApplication.mServerLatestUrl, new AsyncHttpResponseHandler(){

                @Override
                public void onSuccess(String result){
                    ConfigCache.setUrlCache(result, BaseApplication.mServerLatestUrl);
                    checkNewVersion(result);
                }

            @Override
            public void onFailure(Throwable arg0) {
                mProgressDialog.cancel();
            }

            });
        }
    }

    public void checkNewVersion(String result){

        if (result == null || "".equals(result)) {
            mProgressDialog.cancel();
            Toast.makeText(this, R.string.check_new_version_null, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject appreciateConfig = new JSONObject(result);
            mLatestVersionCode = appreciateConfig.getInt("version-code");
            mLatestVersionUpdate = appreciateConfig.getString("version-update");
            mLatestVersionDownload = BaseApplication.mDomain + appreciateConfig.getString("version-download");
        } catch (JSONException e) {
            e.printStackTrace();
            mProgressDialog.cancel();
            Toast.makeText(this, R.string.check_new_version_exception, Toast.LENGTH_SHORT).show();
            return;
        }

        mProgressDialog.cancel();

        if (BaseApplication.mVersionCode < mLatestVersionCode) {
            new AlertDialog.Builder(this)
                .setTitle(R.string.check_new_version)
                .setMessage(mLatestVersionUpdate)
                .setPositiveButton(R.string.app_upgrade_confirm, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(SettingActivity.this, AppUpgradeService.class);
                        intent.putExtra("downloadUrl", mLatestVersionDownload);
                        startService(intent);
                    }
                })
            .setNegativeButton(R.string.app_upgrade_cancel, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            })
            .create().show();
        } else {
            Toast.makeText(this, R.string.check_new_version_latest, Toast.LENGTH_SHORT).show();
        }
    }
}
