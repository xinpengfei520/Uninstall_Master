package com.xpf.android.uninstall.bean;

import android.graphics.drawable.Drawable;

import com.xpf.android.uninstall.utils.PinYinUtils;

/**
 * Created by xinpengfei on 2016/9/18.
 */
public class AppInfo {

    private Drawable icon;//应用图标
    private String appName;//应用名称
    private String packageName;//应用包名
    private String pinyin;//拼音

    public AppInfo() {

    }

    public AppInfo(Drawable icon, String appName, String packageName) {
        this.icon = icon;
        this.appName = appName;
        this.packageName = packageName;
        this.pinyin = PinYinUtils.getPinYin(appName);
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    @Override
    public String toString() {
        return "AppInfo [icon=" + icon + ", appName=" + appName
                + ", packageName=" + packageName + "]";
    }
}
