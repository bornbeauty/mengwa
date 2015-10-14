package com.jimbo.mengwa.utils;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 *
 * Created by jimbo on 2015/10/14.
 */
public class SnackbarUtils {
    public static void showSnackbar(View view, String content) {
        Snackbar.make(view,
                content,
                Snackbar.LENGTH_LONG)
                .setAction("好的", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .show();
    }
}
