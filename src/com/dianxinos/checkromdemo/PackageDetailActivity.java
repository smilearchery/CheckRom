
package com.dianxinos.checkromdemo;

import java.util.ArrayList;

import javax.security.auth.PrivateCredentialPermission;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

public class PackageDetailActivity extends Activity {
    private PackagesEntity pEntity;
    private ImageView appLogoImageView;
    private TextView appNameTextView;
    private TextView appVersionTextView;
    private TextView locationTextView;
    private TextView targetSDKTextView;
    private TextView shareUIDTextView;
    private TextView signatureTextView;
    private ExpandableListView packageInfoView;
    private ExpandableListAdapter ex_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        initView();

        Intent intent = getIntent();
        String type = null;
        int childID = 0;
        if (intent != null) {
            type = intent.getStringExtra(CheckRomUtils.EXTRA_TODETAIL_TYPE);
            childID = intent.getIntExtra(CheckRomUtils.EXTRA_TODETAIL_ID, -1);
        }
        if (type != null && type.equals(CheckRomUtils.EXTRA_TODETAIL_TYPE_NOSHOW)) {
            pEntity = PackageCompare.itemsNotShow.get(childID);
        }
        else {
            pEntity = PackageCompare.items.get(childID);
        }
        
        ex_adapter = new PackageInfoExAdapter(this, pEntity);
        packageInfoView.setAdapter(ex_adapter);
        
        initData();
    }

    private void initView() {
        setContentView(R.layout.packagedetail);
        appLogoImageView = (ImageView) findViewById(R.id.iv_logo);
        appNameTextView = (TextView) findViewById(R.id.tv_appName);
        appVersionTextView = (TextView) findViewById(R.id.tv_appVersion);
        locationTextView = (TextView) findViewById(R.id.tv_location);
        targetSDKTextView = (TextView) findViewById(R.id.tv_targetSDK);
        shareUIDTextView = (TextView) findViewById(R.id.tv_shareUId);
        signatureTextView = (TextView) findViewById(R.id.tv_signature);
        packageInfoView = (ExpandableListView) findViewById(R.id.lv_packageInfo);
    }

    private void initData() {
        appLogoImageView.setImageDrawable(pEntity.getAppLogo());
        appNameTextView.setText(pEntity.getAppName());
        appVersionTextView.setText(getString(R.string.text_version)+pEntity.getVersion());
        locationTextView.setText(getString(R.string.text_filePath)+pEntity.getFilePath());
        targetSDKTextView.setText(getString(R.string.text_targetSDK)
                + Integer.toString(pEntity.getTargetSDK()));
        if (pEntity.getUid() != null) {
            shareUIDTextView.setText(getString(R.string.text_uid) + pEntity.getUid());
        }
        else
        {
            shareUIDTextView.setVisibility(TextView.GONE);
        }

    }
}
