package com.tik.caculatordemo.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;

/**
 * @auth tik
 * @date 17/1/11 下午5:49
 */

public class Utils {
    /**
     * 根据手机的分辨率将dp的单位转成px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率将px(像素)的单位转成dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int changeAlpha(@ColorInt int color, int alpha) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        return Color.argb(alpha, red, green, blue);
    }

    /**
     * Return back to home.
     */
    public static void backToHome(Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get navigation bar height.
     *
     * @param context Context
     * @return navigation bar height
     */
    public static int getNavigationHeight(@NonNull Context context) {
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");

        int navigationBarHeight = 0;
        if (resourceId > 0) {
            navigationBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }

        return navigationBarHeight;
    }

    /**
     * Get whether navigation bar is visible.
     *
     * @param context Context
     * @return true: visible, false: invisible
     */
    public static boolean navigationBarVisible(@NonNull Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

            Display display = windowManager.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            Point realSize = new Point();
            display.getRealSize(realSize);

            return realSize.y != size.y;
        } else {
            boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
            boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

            if (hasMenuKey || hasBackKey) {
                return false;
            } else {
                return true;
            }
        }
    }
}
