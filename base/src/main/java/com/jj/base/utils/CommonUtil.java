package com.jj.base.utils;

import android.content.Context;
import android.text.TextUtils;

import java.util.List;

public class CommonUtil {
    public static <T> T safeGetElement(List<T> list, int index) {
        if (checkEmpty(list) || !checkValid(list.size(), index)) return null;
        else return list.get(index);
    }

    public static <T> void safeAddElement(List<T> list, int index, T element) {
        if (list != null) {
            //下标有效 根据下标添加
            if (checkValid(list.size(), index)) {
                if (list.get(index) != element) list.set(index, element);
            }
            //下标无效 直接添加
            else list.add(element);
        }
    }

    public static boolean checkValid(int size, int index) {
        return index >= 0 && index < size;
    }

    public static boolean checkEmpty(List list) {
        return list == null || list.isEmpty();
    }

    public static boolean checkElementEmpty(List list) {
        if (checkEmpty(list)) {
            return true;
        }
        for (Object o : list) {
            if (o instanceof CharSequence) {
                if (!TextUtils.isEmpty((CharSequence) o)) return false;
            } else {
                if (o != null) return false;
            }
        }
        return true;
    }

    public static int dip2px(Context context, float dipValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * density + 0.5f);
    }

}
