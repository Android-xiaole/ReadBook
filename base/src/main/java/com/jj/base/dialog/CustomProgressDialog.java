package com.jj.base.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.WindowManager;

import com.jj.base.R;

import androidx.annotation.NonNull;


public class CustomProgressDialog extends Dialog{

    public CustomProgressDialog(@NonNull Context context) {
        super(context,R.style.base_CustomDialog);
        setContentView(R.layout.base_progress_dialog);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.dimAmount =0f;
        getWindow().setAttributes(params);
//            progressDialog.setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

}
