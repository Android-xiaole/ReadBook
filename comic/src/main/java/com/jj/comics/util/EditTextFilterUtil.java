package com.jj.comics.util;

import android.text.InputFilter;
import android.text.Spanned;

import com.jj.base.BaseApplication;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 输入框过滤器工具类
 */
public class EditTextFilterUtil {

    public static InputFilter if_emoji = new InputFilter() {
        Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]", Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Matcher emojiMatcher = emoji.matcher(source);
            if (emojiMatcher.find()) {
                ToastUtil.showToastShort(BaseApplication.getApplication().getString(R.string.comic_emoji_remind));
                return "";
            }
            return null;
        }
    };

    public static InputFilter if_cnennum = new InputFilter() {
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9\\u4E00-\\u9FA5_]");

        @Override
        public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
            Matcher matcher = pattern.matcher(charSequence);
            if (!matcher.find()) {
                return null;
            } else {
                ToastUtil.showToastShort(BaseApplication.getApplication().getString(R.string.comic_cnennum_remind));
                return "";
            }
        }
    };
}
