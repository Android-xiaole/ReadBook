package com.jj.base.utils;

import android.widget.EditText;

public class CusTextUtils {

    public static String getStringFromEditText(EditText editText,boolean trim,String defaultStr) {
        String result = defaultStr;
        if (editText != null) {
            if (trim) {
                result = editText.getText().toString().trim();
            }else {
                result = editText.getText().toString();
            }
        }
       return result;
    }
}
