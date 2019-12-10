package com.errorerrorerror.esplightcontroller.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class DisplayUtils {

    public static float convertDpToPixel(Context context, float dp) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static float convertPixelsToDp(Context context,float px) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
