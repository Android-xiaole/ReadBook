package com.jj.comics.util;

import android.text.TextUtils;

import java.util.regex.Pattern;

public class RegularUtil {
    private static final String REGEX_MOBILE = "^((17[0-9])|(14[0-9])|(13[0-9])|(15[0-9])|(19[0-9])|(18[0-9])|(16[0-9]))\\d{8}$";
//    public static final String REGEX_MOBILE =
//            "(^((0\\d{2,3}-?)?|(\\(0\\d{2,3}\\))?)?\\d{7,8}$)|(^[48]00-?\\d{3}-?\\d{4}$)|(^9[56]\\d{3,4}$)|(^1[34578]\\d{9}$)";

    public static boolean isMobile(String mobile) {
        return !TextUtils.isEmpty(mobile) && mobile.length() == 11 && Pattern.matches(REGEX_MOBILE, mobile);
    }

    public static boolean isNumeric(String str) {
        if (TextUtils.isEmpty(str))
            return false;
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }
}
