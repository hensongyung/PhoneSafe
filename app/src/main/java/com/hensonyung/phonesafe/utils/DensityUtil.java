package com.hensonyung.phonesafe.utils;

import android.content.Context;

/**
 * Created by wengjiasheng on 2015/3/30.
 */
public class DensityUtil {
    public static int dip2px(Context context,float dpValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context,float pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue/scale +0.5f);
    }
}
