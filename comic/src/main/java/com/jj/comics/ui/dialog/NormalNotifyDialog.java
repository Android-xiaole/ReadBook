package com.jj.comics.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.jj.base.dialog.BaseFragmentDialog;

/**
 * 常规提示弹窗
 */
public class NormalNotifyDialog extends BaseFragmentDialog {

    private String msg;
    private String title;
    private DialogUtilForComic.OnDialogClick onDialogClick;

    public void show(FragmentManager manager, String title, String msg,
                     DialogUtilForComic.OnDialogClick onDialogClick) {
        super.show(manager);
        this.title = title;
        this.msg = msg;
        this.onDialogClick = onDialogClick;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setCancelable(false);
        dialog.setContentView(com.jj.base.R.layout.base_normal_layout_dialog);
        TextView tv_title = dialog.findViewById(com.jj.base.R.id.tv_title);
        TextView tv_msg = dialog.findViewById(com.jj.base.R.id.tv_msg);
        TextView btn_dialog_cancel = dialog.findViewById(com.jj.base.R.id.delete_dialog_cancel);
        TextView btn_dialog_confirm = dialog.findViewById(com.jj.base.R.id.delete_dialog_confirm);

        tv_title.setText(title);
        tv_msg.setText(msg);

        btn_dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (onDialogClick != null) onDialogClick.onRefused();
            }
        });

        btn_dialog_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (onDialogClick != null) onDialogClick.onConfirm();
            }
        });
        return dialog;
    }


}
