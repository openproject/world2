package com.tianxia.lib.baseworld2.activity;

import android.os.Bundle;

import android.view.View;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianxia.lib.baseworld2.BaseApplication;
import com.tianxia.lib.baseworld2.cache.ConfigCache;
import com.tianxia.lib.baseworld2.db.BaseSQLiteHelper;
import com.tianxia.lib.baseworld2.R;
import com.tianxia.widget.image.SmartImageView;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;

public class DetailsActivity extends BaseActivity
    implements View.OnClickListener {

    private TextView mAppTitle;
    private Button mAppHeaderBack;
    private View mAppHeaderBackDivider;

    private SmartImageView mItemAvatar;
    private TextView mItemName;
    private TextView mItemDate;
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

        if (BaseApplication.isForbiddenAdWall()) {
            mAppNoticeView = findViewById(R.id.app_notice);
            mAppNoticeView.setVisibility(View.GONE);
        }
    }

    protected void displayAd() {
    }

    private void initContent() {

        mItemAvatar = (SmartImageView) findViewById(R.id.item_avatar);
        mItemAvatar.setImageUrl(mStatuInfo.avatar, R.drawable.icon, 0);

        mItemName = (TextView) findViewById(R.id.item_name);
        mItemName.setText(mStatuInfo.name);
        mItemName.getPaint().setFakeBoldText(true);

        mItemDate = (TextView) findViewById(R.id.item_date);
        String dateString = mStatuInfo.created;
        //format the date string
        if (dateString != null && !"".equals(dateString)) {
            try {
                SimpleDateFormat mSinaWeiboDateFormat =
                    new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", new DateFormatSymbols(Locale.US));
                SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                Date date = mSinaWeiboDateFormat.parse(dateString);
                mItemDate.setText(mSimpleDateFormat.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            mItemDate.setText("");
        }

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
        }
    }
}
