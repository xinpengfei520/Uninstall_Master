package com.xpf.android.uninstall.utils;

/**
 * Created by x-sir on 3/13/21 :)
 * Function:
 */
public class Utils {

    public static boolean isCurrentApp(String packageName) {
        return "com.xpf.android.uninstall".equals(packageName);
    }
}
