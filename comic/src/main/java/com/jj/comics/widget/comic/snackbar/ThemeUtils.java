package com.jj.comics.widget.comic.snackbar;

import android.content.Context;
import android.content.res.TypedArray;

import com.jj.comics.R;

/**
 * Created by xuedakun on 2017/9/12 16:36
 *
 * @version : v1.0
 * @project : TopSnackbar
 * @Email : dakun611@Gmail.com
 */

class ThemeUtils {

    private static final int[] APPCOMPAT_CHECK_ATTRS = {
            R.attr.colorPrimary
    };

    static void checkAppCompatTheme(Context context) {
        TypedArray a = context.obtainStyledAttributes(APPCOMPAT_CHECK_ATTRS);
        final boolean failed = !a.hasValue(0);
        if (a != null) {
            a.recycle();
        }
        if (failed) {
            throw new IllegalArgumentException("You need to use a Theme.AppCompat theme "
                    + "(or descendant) with the design library.");
        }
    }

}