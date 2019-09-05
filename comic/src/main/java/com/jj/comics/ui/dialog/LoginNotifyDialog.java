package com.jj.comics.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.jj.base.dialog.BaseFragmentDialog;
import com.jj.comics.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import static android.view.KeyEvent.KEYCODE_BACK;

public class LoginNotifyDialog extends BaseFragmentDialog {

    private DialogUtilForComic.OnDialogClick onDialogClick;

    public void show(FragmentManager fragmentManager,
                     DialogUtilForComic.OnDialogClick onDialogClick) {
        super.show(fragmentManager);
        this.onDialogClick = onDialogClick;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getContext(), R.style.comic_Dialog_no_title);
        dialog.setCancelable(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KEYCODE_BACK && dialog != null) {
                    dialog.dismiss();
                    getActivity().finish();
                }
                return true;
            }
        });
        dialog.setContentView(R.layout.comic_bg_alert_login);
        ImageView btn_alert_login_now = dialog.findViewById(R.id.btn_alert_login_now);
        ImageView btn_alert_login_close = dialog.findViewById(R.id.btn_alert_login_close);


        btn_alert_login_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (onDialogClick != null) onDialogClick.onRefused();
            }
        });

        btn_alert_login_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDialogClick != null) onDialogClick.onConfirm();
            }
        });
        return dialog;
    }
}
