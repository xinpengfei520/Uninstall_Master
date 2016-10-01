package com.atguigu.l11_app_listview_pw;

import android.graphics.drawable.Drawable;

/**
 * Created by xinpengfei on 2016/9/18.
 */
public class AppInfo {

    private Drawable icon;//应用图标
    private String appName;//应用名称
    private String packageName;//应用包名

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

    public AppInfo(Drawable icon, String appName, String packageName) {
        super();
        this.icon = icon;
        this.appName = appName;
        this.packageName = packageName;
    }

    public AppInfo() {
        super();
    }

    @Override
    public String toString() {
        return "AppInfo [icon=" + icon + ", appName=" + appName
                + ", packageName=" + packageName + "]";
    }
}
