
package com.dianxinos.checkromdemo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.Manifest.permission;
import android.R.integer;
import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageParser;
import android.content.pm.PackageParser.Permission;
import android.content.pm.PermissionInfo;
import android.content.pm.Signature;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageParser.NewPermissionInfo;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.content.res.AssetManager;
import android.content.res.Resources;

public class PackageCompare {
    public static final List<PackagesEntity> items = new ArrayList<PackagesEntity>();
    public static final List<PackagesEntity> itemsNotShow = new ArrayList<PackagesEntity>();
    public static final List<File> fileList = new ArrayList<File>();
    private Context ctx;
    private PackageManager pm;

    public PackageCompare(Context ctx) {
        this.ctx = ctx;
        // Read permissions from .../etc/permission directory.
        // getPackages();
        // compareApp("/app");
        // compareApp("vendor/app");
    }

    public void doWork() {
        getPackages();
        getFilelist("/app");
        // compareApp("vendor/app");
    }

    public void getPackages() {
        pm = ctx.getPackageManager();
        PackagesEntity pEntity;
        int flag = PackageManager.GET_SIGNATURES
                | PackageManager.GET_CONFIGURATIONS | PackageManager.GET_PERMISSIONS
                | PackageManager.GET_SHARED_LIBRARY_FILES;
        List<PackageInfo> lp = pm.getInstalledPackages(flag);
        for (PackageInfo packageInfo : lp) {
            try {
                pEntity = new PackagesEntity(packageInfo.packageName,
                        pm.getApplicationIcon(packageInfo.packageName), packageInfo.sharedUserId,
                        packageInfo.signatures);
                pEntity.setName(packageInfo.applicationInfo.loadLabel(pm).toString());
                pEntity.setTargetSDK(packageInfo.applicationInfo.targetSdkVersion);
                pEntity.setVersion(packageInfo.versionName);
                pEntity.setFilePath(packageInfo.applicationInfo.sourceDir);
                Log.d("Package Source DIR", packageInfo.applicationInfo.sourceDir);
                pEntity.setPermissionInfos(packageInfo.requestedPermissions);
                pEntity.setFeatureInfos(packageInfo.reqFeatures);
                items.add(pEntity);
            } catch (NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public List<PackagesEntity> getNotShowList() {
        return itemsNotShow;
    }

    public List<PackagesEntity> getShowList() {
        return items;
    }

    public List<File> getFilelist(String path) {
        File libraryDir = new File(Environment.getRootDirectory(), path);
        Log.d("Environment File", Environment.getRootDirectory().getPath());
        if (!libraryDir.exists() || !libraryDir.isDirectory()) {
            return null;
        }
        if (!libraryDir.canRead()) {
            return null;
        }
        // Iterate over the files in the directory and scan .xml files
        for (File f : libraryDir.listFiles()) {
            if (!f.getPath().endsWith(".apk")) {
                continue;
            }
            if (!f.canRead()) {
                continue;
            }
            fileList.add(f);
            Log.d("File Name", f.getPath());
        }
        return fileList;
    }

    public int getFileCount() {
        return fileList.size();
    }

    public void compareApp() {
        for (File file : fileList) {
            comparePackage(file);
        }
    }

    public void comparePackage(File f) {
        PackageParser packageParser = new PackageParser(f.getPath());

        DisplayMetrics metrics = new DisplayMetrics();
        metrics.setToDefaults();
        PackageParser.Package mPkgInfo = packageParser.parsePackage(f,
                f.getPath(), metrics, 0);
        packageParser.collectCertificates(mPkgInfo, 1);
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getAppName().equals(mPkgInfo.packageName)) {
                items.get(i).setUsesLibraries(mPkgInfo.usesLibraries);
                return;
            }
            if (!(items.get(i).getUid() == null)
                    && items.get(i).getUid().equals(mPkgInfo.mSharedUserId)) {
                if (compareSignature(items.get(i).getSignature(), mPkgInfo.mSignatures)) {
                    ifNotShow(f, mPkgInfo, ctx.getString(R.string.show_same_uid));
                    return;
                }
            }
        }
        if (Build.VERSION.SDK_INT < mPkgInfo.applicationInfo.targetSdkVersion) {
            ifNotShow(f, mPkgInfo, ctx.getString(R.string.show_low_sdk));
            return;
        }

        if (!(CheckPackage.mFeatures.contains(mPkgInfo.reqFeatures) || mPkgInfo.reqFeatures == null))
        {
            ifNotShow(f, mPkgInfo, ctx.getString(R.string.show_no_feature));
            return;
        }

        if (!(CheckPackage.mLibraries.contains(mPkgInfo.usesLibraries) || mPkgInfo.usesLibraries == null)) {
            ifNotShow(f, mPkgInfo, ctx.getString(R.string.show_no_library));
            return;
        }
    }

    private boolean compareSignature(Signature[] sys, Signature[] app) {
        if (sys[0].toCharsString().equals(app[0].toCharsString()))
        {
            return false;
        }
        return true;
    }

    private void ifNotShow(File f, PackageParser.Package mPkgInfo, String Reason) {
        PackagesEntity pEntity = null;
        Resources pRes = ctx.getResources();
        AssetManager assmgr = new AssetManager();
        assmgr.addAssetPath(f.getPath());
        Resources res = new Resources(assmgr, pRes.getDisplayMetrics(),
                pRes.getConfiguration());
        pEntity = new PackagesEntity(mPkgInfo.packageName,
                res.getDrawable(mPkgInfo.applicationInfo.icon), mPkgInfo.mSharedUserId,
                mPkgInfo.mSignatures);
        pEntity.setName(mPkgInfo.applicationInfo.loadLabel(pm).toString());
        pEntity.setReason(Reason);
        pEntity.setTargetSDK(mPkgInfo.applicationInfo.targetSdkVersion);
        pEntity.setVersion(mPkgInfo.mVersionName);
        pEntity.setFilePath(f.getPath());
        pEntity.setPermissions(mPkgInfo.requestedPermissions);
        pEntity.setFeatureInfosList(mPkgInfo.reqFeatures);
        pEntity.setUsesLibraries(mPkgInfo.usesLibraries);
        itemsNotShow.add(pEntity);
    }
}
