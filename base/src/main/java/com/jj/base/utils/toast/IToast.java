package com.jj.base.utils.toast;

import android.view.View;

import androidx.annotation.StringRes;

public interface IToast {

    void show();

    void cancel();

    IToast setView(View view);

    View getView();

    IToast setDuration(int duration);

    IToast setGravity(int gravity, int xOffset, int yOffset);

    IToast setText(@StringRes int resId);

    IToast setText(CharSequence s);

    IToast setMargin(float horizontalMargin, float verticalMargin);

}
