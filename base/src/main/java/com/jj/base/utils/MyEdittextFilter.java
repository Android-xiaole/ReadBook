package com.jj.base.utils;

import android.text.InputFilter;
import android.text.Spanned;

import com.jj.base.BaseApplication;
import com.jj.base.R;
import com.jj.base.utils.toast.ToastUtil;

public class MyEdittextFilter implements InputFilter {

    private int mMaxLength;

    public MyEdittextFilter(int max) {
        mMaxLength = max;
    }

    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart, int dend) {
        int keep = mMaxLength - (dest.length() - (dend - dstart));
        if (keep < (end - start)) {
            ToastUtil.showToastLong(String.format(BaseApplication.getApplication().getString(R.string.base_max_length_text), mMaxLength));
        }
        if (keep <= 0) {
            return "";
        } else if (keep >= end - start) {
            return null;
        } else {
            return source.subSequence(start, start + keep);
        }
    }
}
