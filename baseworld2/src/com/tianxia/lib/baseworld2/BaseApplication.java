package com.tianxia.lib.baseworld2;

import android.app.Application;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import android.os.Environment;

import com.tianxia.lib.baseworld2.cache.ConfigCache;
import com.tianxia.lib.baseworld2.db.BaseSQLiteHelper;
import com.tianxia.lib.baseworld2.R;
import com.tianxia.lib.baseworld2.sync.http.AsyncHttpClient;
import com.tianxia.lib.baseworld2.sync.http.AsyncHttpResponseHandler;
import com.tianxia.lib.baseworld2.utils.NetworkUtils;
import com.tianxia.lib.baseworld2.utils.PreferencesUtils;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class BaseApplication extends Application {

    public static BaseSQLiteHelper mSQLiteHelper;
    public static final String DOMAIN = "domain";
    public static final String DOMAIN_URL = "url";
    public static String mDomain = "http://www.kaiyuanxiangmu.com/";
    public static String mBakeDomain = "http://1.kaiyuanxiangmu.sinaapp.com/";

    public static String mAppId;

    public static int mNetWorkState = NetworkUtils.NETWORN_NONE;

    public static String mDownloadPath;
    public static int mVersionCode;
    public static String mVersionName;
    public static boolean mShowUpdate = true;

    private static String mMarketName; // goapk etc.

    public static String mSdcardDataDir;
    public static String mApkDownloadUrl = null;

    public static String mServerLatestUrl;
    public static String mServerPageUrl;

    @Override
    public void onCreate() {
        initEnv();
        initLocalVersion();
    }

    public void initLocalVersion(){
        PackageInfo pinfo;
        ApplicationInfo ainfo;
        try {
            pinfo = this.getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS);
            mVersionCode = pinfo.versionCode;
            mVersionName = pinfo.versionName;

            ainfo = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            mMarketName = ainfo.metaData.getString("UMENG_CHANNEL");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initEnv() {
        mAppId = getString(R.string.app_id);
        mSQLiteHelper = new BaseSQLiteHelper(getApplicationContext(), mAppId+ ".db", 1);
        mDownloadPath = "/" + mAppId+ "/download";

        mServerLatestUrl = mDomain + mAppId + "/data/json/latest.json";
        mServerPageUrl = mDomain + mAppId + "/data/json/pages/";

        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            File file = new File(Environment.getExternalStorageDirectory().getPath() +  "/" + mAppId + "/config/");
            if(!file.exists()) {
                if (file.mkdirs()) {
                    mSdcardDataDir = file.getAbsolutePath();
                }
            } else {
                mSdcardDataDir = file.getAbsolutePath();
            }
        }

        mNetWorkState = NetworkUtils.getNetworkState(this);
        checkDomain(mDomain, false);
    }

    public void checkDomain(final String domain, final boolean stop){
        mDomain = PreferencesUtils.getStringPreference(getApplicationContext(), DOMAIN, DOMAIN_URL, mDomain);
        String cacheConfigString = ConfigCache.getUrlCache(domain + "host.json");
        if (cacheConfigString != null) {
            updateDomain(cacheConfigString);
        } else {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(domain + "host.json", new AsyncHttpResponseHandler(){

                @Override
                public void onStart() {
                }

                @Override
                public void onSuccess(String result) {
                    ConfigCache.setUrlCache(result, domain + "host.json");
                    updateDomain(result);
                }

                @Override
                public void onFailure(Throwable arg0) {
                    if (!stop) {
                        checkDomain(mBakeDomain, true);
                    }
                }

                @Override
                public void onFinish() {
                }
            });
        }
    }

    public void updateDomain(String result) {
        try {
            JSONObject appreciateConfig = new JSONObject(result);
            String domain = appreciateConfig.optString("domain");
            if (domain != null && !"".equals(domain)) {
                BaseApplication.mDomain = domain;
                PreferencesUtils.setStringPreferences(getApplicationContext(), DOMAIN, DOMAIN_URL, domain);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static boolean isForbiddenAdWall() {
        if ("goapk".equals(mMarketName)) {
            return true;
        }
        return false;
    }
}
