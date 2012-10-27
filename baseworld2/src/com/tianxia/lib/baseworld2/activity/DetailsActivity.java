package com.tianxia.lib.baseworld2.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tianxia.lib.baseworld2.BaseApplication;
import com.tianxia.lib.baseworld2.R;
import com.tianxia.lib.baseworld2.utils.PreferencesUtils;
import com.tianxia.widget.image.SmartImageView;

public class DetailsActivity extends BaseActivity
    implements View.OnClickListener {

    private TextView mAppTitle;
    private Button mAppHeaderBack;
    private View mAppHeaderBackDivider;

    private Button mItemShare;
    private Button mItemCopy;
    private TextView mItemText;
    private SmartImageView mItemThumbnail;

    protected LinearLayout mAdContainer;
    private View mAppNoticeView;

    private StatuInfo mStatuInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        mStatuInfo = getIntent().getParcelableExtra("statu");

        mAppTitle = (TextView) findViewById(R.id.app_title);
        mAppHeaderBack = (Button) findViewById(R.id.app_header_back);
        mAppHeaderBackDivider = findViewById(R.id.app_header_back_divider);

        mAppTitle.setText(R.string.details_title);
        mAppHeaderBack.setVisibility(View.VISIBLE);
        mAppHeaderBackDivider.setVisibility(View.VISIBLE);

        mAppHeaderBack.setOnClickListener(this);

        initContent();

        mAdContainer = (LinearLayout) findViewById(R.id.ad_container);
        displayAd();

        displayNotice();
    }

    private void displayNotice() {
        mAppNoticeView = findViewById(R.id.app_notice);
        if (BaseApplication.isForbiddenAdWall()) {
            mAppNoticeView.setVisibility(View.GONE);
            return;
        }
        long last_time = PreferencesUtils.getLongPreference(this,
                    MainActivity.SHARE_NOTICE_LAST_TIME,
                    0);
        if (System.currentTimeMillis() - last_time > 1000*60*60*24) {
            mAppNoticeView.setVisibility(View.VISIBLE);
            PreferencesUtils.setLongPreference(this,
                    MainActivity.SHARE_NOTICE_LAST_TIME,
                    System.currentTimeMillis());                               
        } else {
            mAppNoticeView.setVisibility(View.GONE);
        }
    }
    protected void displayAd() {
    }

    private void initContent() {

        mItemShare = (Button) findViewById(R.id.item_share);
        mItemCopy = (Button) findViewById(R.id.item_copy);
        mItemShare.setOnClickListener(this);
        mItemCopy.setOnClickListener(this);

        mItemText = (TextView) findViewById(R.id.item_text);
        mItemText.setText(mStatuInfo.text);

        if (mStatuInfo.pic_middle != null && !"".equals(mStatuInfo.pic_middle)) {
            mItemThumbnail = (SmartImageView) findViewById(R.id.item_thumbnail);
            mItemThumbnail.setVisibility(View.VISIBLE);
            mItemThumbnail.setImageUrl(mStatuInfo.pic_middle, R.drawable.webimage_failure, R.drawable.webimage_loading);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mAppHeaderBack) {
            onBackPressed();
        } else if (v == mItemShare) {
            Intent intent=new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.main_options_share_title));
            intent.putExtra(Intent.EXTRA_TEXT, mStatuInfo.text);
            startActivity(Intent.createChooser(intent, getString(R.string.setting_share_app_title)));
        } else if (v == mItemCopy) {
            ClipboardManager cm = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setText(mStatuInfo.text);
            Toast.makeText(this, R.string.main_options_copy_toast, Toast.LENGTH_SHORT).show();
        }
    }
}
