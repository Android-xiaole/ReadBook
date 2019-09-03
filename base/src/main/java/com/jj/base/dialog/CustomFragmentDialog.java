package com.jj.base.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.jj.base.R;

/**
 * 外部可以传入布局ID和styleId的DialogFragment
 */
public class CustomFragmentDialog extends BaseFragmentDialog {

    private Dialog dialog;

    public void show(Context context, FragmentManager manager, int layoutId) {
        super.show(manager);
        if (dialog == null) dialog = new Dialog(context, R.style.base_CustomDialog);
        dialog.setContentView(layoutId);
    }


    public void show(Context context, FragmentManager manager, int layoutId, int styleId) {
        super.show(manager);
        if (dialog == null) dialog = new Dialog(context, styleId);
        dialog.setContentView(layoutId);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (dialog != null) {
            return dialog;
        }
        return super.onCreateDialog(savedInstanceState);
    }

    /**
     * 外部调用此方法获取dialog对象，再通过dialog.findViewById()对控件进行操作
     *
     * @return
     */
    @Override
    public Dialog getDialog() {
        return dialog;
    }

}
