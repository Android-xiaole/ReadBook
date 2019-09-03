package com.jj.comics.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.jj.base.dialog.BaseFragmentDialog;
import com.jj.base.imageloader.ILFactory;
import com.jj.comics.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.FragmentManager;

public class FreeGoldDialog extends BaseFragmentDialog {

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
        dialog.show();
        dialog.setContentView(R.layout.comic_bg_alert_free_gold);
        TextView btn_know = dialog.findViewById(R.id.btn_know);
        TextView btn_go_read = dialog.findViewById(R.id.btn_go_read);
        ImageView iv = dialog.findViewById(R.id.iv_bg);

        ILFactory.getLoader().loadResource(iv, R.drawable.bg_comic_dialog_free_gold,
                new RequestOptions().transforms(new FitCenter(), new RoundedCorners(15)));

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
