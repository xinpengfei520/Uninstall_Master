package com.xpf.android.uninstall.utils;

import android.content.Context;

/**
 * Created by x-sir on 3/21/21 :)
 * Function:
 */
public class DensityUtils {

    /**
     * dp--->px
     *
     * @param dp
     * @return
     */
    public static int dp2px(Context context, int dp) {
        // 获取手机的密度
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (density * dp + 0.5); // 实现四舍五入操作
    }

    /**
     * px--->dp
     *
     * @param px
     * @return
     */
    public static int px2dp(Context context, int px) {
        // 获取手机的密度
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (px / density + 0.5); // 实现四舍五入操作
    }
}
