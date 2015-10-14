package com.jimbo.mengwa.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.jimbo.mengwa.R;
import com.lidroid.xutils.BitmapUtils;

import java.net.ContentHandler;

/**
 *
 * Created by jimbo on 2015/10/14.
 */
public class BitmapHelper {
    private static BitmapUtils bitmapUtils;

    private BitmapHelper() {}

    public static BitmapUtils getBitmapUtils(Context context) {
        if (null == bitmapUtils) {
            bitmapUtils = new BitmapUtils(context);

            bitmapUtils.configDefaultLoadingImage(R.mipmap.loading);
            bitmapUtils.configDefaultLoadFailedImage(R.mipmap.icon2);
            bitmapUtils.configDefaultBitmapConfig(Bitmap.Config.RGB_565);

            return bitmapUtils;
        }
        return bitmapUtils;
    }
}
