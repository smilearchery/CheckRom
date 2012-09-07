
package com.dianxinos.checkromdemo;

import java.util.ArrayList;

import android.content.pm.FeatureInfo;
import android.content.pm.Signature;
import android.content.pm.PackageParser.NewPermissionInfo;
import android.graphics.drawable.Drawable;

public class PackagesEntity {
    /**
     * 
     */
    private String name;
    private String appName;
    private Drawable appLogo;
    private String uid = null;
    private int targetSDK;
    private String version;
    private String reason;
    private String filePath;
    private ArrayList<String> permissionInfos = new ArrayList<String>();
    private FeatureInfo[] featureInfos;
    private ArrayList<String> usesLibraries = new ArrayList<String>();
    private Signature[] signature;

    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public String getAppName() {
        return this.appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getAppLogo() {
        return this.appLogo;
    }

    public void setAppLogo(Drawable appLogo) {
        this.appLogo = appLogo;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getTargetSDK() {
        return this.targetSDK;
    }

    public void setTargetSDK(int targetSDK) {
        this.targetSDK = targetSDK;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public ArrayList<String> getPermissionInfos() {
        return this.permissionInfos;
    }

    public void setPermissions(ArrayList<String> permissions) {
        this.permissionInfos = permissions;
    }

    public void setPermissionInfos(String[] permissionInfos) {
        if (permissionInfos != null) {
            for (String p : permissionInfos) {
                this.permissionInfos.add(p);
            }
        }
    }

    public FeatureInfo[] getFeatureInfos() {
        return this.featureInfos;
    }

    public void setFeatureInfosList(ArrayList<FeatureInfo> feArrayList) {
        if (feArrayList == null) {
            return;
        }
        FeatureInfo[] fInfos = new FeatureInfo[feArrayList.size()];
        int i = 0;
        for (FeatureInfo fInfo : fInfos) {
            fInfos[i] = fInfo;
            i++;
        }
        this.featureInfos = fInfos;
    }

    public void setFeatureInfos(FeatureInfo[] featureInfos) {
        this.featureInfos = featureInfos;
    }

    public ArrayList<String> getUsesLibraries() {
        return this.usesLibraries;
    }

    public void setUsesLibraries(ArrayList<String> usesLibraries) {
        this.usesLibraries = usesLibraries;
    }

    public Signature[] getSignature() {
        return this.signature;
    }

    public String[] getSignatureStrings() {
        String[] ssign = null;
        int i = 0;
        for (Signature s : signature) {
            ssign[i] = s.toCharsString();
        }
        return ssign;
    }

    public PackagesEntity(String appName, Drawable appLogo, String uid, Signature[] signatures) {
        this.appName = appName;
        this.appLogo = appLogo;
        this.uid = uid;
        this.signature = signatures;
    }
}
