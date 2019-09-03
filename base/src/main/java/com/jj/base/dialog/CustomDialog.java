package com.jj.base.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

public class CustomDialog extends Dialog {
    private Context context;
    private int resId;

    public CustomDialog(Context context, int resLayout) {
        this(context, 0, 0);
    }

    public CustomDialog(Context context, int themeResId, int resLayout) {
        super(context, themeResId);
        this.context = context;
        this.resId = resLayout;
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams attributes = dialogWindow.getAttributes();
        attributes.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(attributes);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(resId);
    }
}