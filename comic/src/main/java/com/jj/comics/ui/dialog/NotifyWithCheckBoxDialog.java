package com.jj.comics.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.jj.base.dialog.BaseFragmentDialog;
import com.jj.comics.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

/**
 * 带checkbox的提示弹窗
 */
public class NotifyWithCheckBoxDialog extends BaseFragmentDialog {

    private String msg;
    private String title;
    private DialogUtilForComic.OnDialogClickWithSelect onDialogClick;

    public void show(FragmentManager manager, String title,String msg,
                     DialogUtilForComic.OnDialogClickWithSelect onDialogClick) {
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
        dialog.setContentView(com.jj.base.R.layout.base_delete_layout_dialog_no_more);

        TextView tv_title = dialog.findViewById(R.id.tv_title);
        TextView tv_dialog_msg = dialog.findViewById(com.jj.base.R.id.delete_dialog_msg);
        TextView btn_dialog_cancel = dialog.findViewById(com.jj.base.R.id.delete_dialog_cancel);
        TextView btn_dialog_confirm = dialog.findViewById(com.jj.base.R.id.delete_dialog_confirm);
        androidx.appcompat.widget.AppCompatCheckBox checkBox = dialog.findViewById(com.jj.base.R.id.cb_no_more);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (onDialogClick != null) onDialogClick.onSelected(isChecked);
            }
        });

        tv_title.setText(title);
        tv_dialog_msg.setText(msg);

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
