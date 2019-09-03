package com.jj.comics.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.jj.base.dialog.BaseFragmentDialog;
import com.jj.comics.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import static android.view.KeyEvent.KEYCODE_BACK;

public class ComicCollectionDialog extends BaseFragmentDialog {

    private DialogUtilForComic.OnDialogClick onDialogClick;

    public void show(FragmentManager manager, DialogUtilForComic.OnDialogClick onDialogClick) {
        super.show(manager);
        this.onDialogClick = onDialogClick;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getContext(), R.style.comic_Dialog_no_title);

        dialog.setCancelable(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface d, int keyCode, KeyEvent event) {
                if (keyCode == KEYCODE_BACK && dialog != null) {
                    dialog.dismiss();
                }
                return true;
            }
        });
        dialog.setContentView(R.layout.comic_bg_alert_collection);
        TextView btn_know = dialog.findViewById(R.id.btn_ignore);
        TextView btn_go_read = dialog.findViewById(R.id.btn_ok);

        btn_know.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (onDialogClick != null) onDialogClick.onRefused();
            }
        });

        btn_go_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (onDialogClick != null) onDialogClick.onConfirm();
            }
        });
        return dialog;
    }

}
