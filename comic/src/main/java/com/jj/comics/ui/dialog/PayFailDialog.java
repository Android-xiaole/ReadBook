package com.jj.comics.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import com.jj.base.dialog.BaseFragmentDialog;
import com.jj.comics.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PayFailDialog extends BaseFragmentDialog {


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getContext(),R.style.comic_Dialog_no_title);
        dialog.setContentView(R.layout.comic_pay_fail);
        dialog.setCancelable(false);

        dialog.findViewById(R.id.comic_pay_fail_dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return dialog;
    }
}
