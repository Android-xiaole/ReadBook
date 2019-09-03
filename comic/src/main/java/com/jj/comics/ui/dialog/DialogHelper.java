package com.jj.comics.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.jj.base.imageloader.ILFactory;
import com.jj.comics.R;

import static android.view.KeyEvent.KEYCODE_BACK;

public class DialogHelper {
    private Context mContext;
    private DialogUtilForComic.OnDialogClick onDialogClick;

    public DialogHelper(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 提示用户退出阅读时提示收藏对话框
     *
     * @param onDialogClick
     */
    public void showCollection(final DialogUtilForComic.OnDialogClick onDialogClick) {
        final Dialog dialog = new Dialog(mContext, R.style.comic_Dialog_no_title);
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
        dialog.show();
    }


    /**
     * 金币大放送任务对话框
     *
     * @param onDialogClick
     */
    public void showFreeGold(final DialogUtilForComic.OnDialogClick onDialogClick) {
        final Dialog dialog = new Dialog(mContext, R.style.comic_Dialog_no_title);

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
    }

    /**
     * 登录权益框提醒
     *
     * @param onDialogClick
     */
    public void showLoginNotify(final DialogUtilForComic.OnDialogClick onDialogClick) {
        final Dialog dialog = new Dialog(mContext, R.style.comic_Dialog_no_title);
        dialog.setCancelable(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KEYCODE_BACK && dialog != null) {
                    dialog.dismiss();
                    ((Activity) mContext).finish();
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
        dialog.show();
    }

    /**
     * 常用对话框提醒
     *
     * @param title
     * @param msg
     * @param onDialogClick
     */
    public void showNormalNotify(String title, String msg, final DialogUtilForComic.OnDialogClick onDialogClick) {
        final Dialog dialog = new Dialog(mContext);
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

        dialog.show();
    }

    /**
     * 含有选择的通用提醒对话框
     *
     * @param title
     * @param msg
     * @param onDialogClick
     */
    public void showNotifyWithCheckBox(String title, String msg, final DialogUtilForComic.OnDialogClickWithSelect onDialogClick) {
        final Dialog dialog = new Dialog(mContext);
        dialog.setCancelable(false);
        dialog.setContentView(com.jj.base.R.layout.base_delete_layout_dialog_no_more);

        TextView tv_title = dialog.findViewById(R.id.tv_title);
        TextView tv_dialog_msg = dialog.findViewById(com.jj.base.R.id.delete_dialog_msg);
        TextView btn_dialog_cancel = dialog.findViewById(com.jj.base.R.id.delete_dialog_cancel);
        TextView btn_dialog_confirm = dialog.findViewById(com.jj.base.R.id.delete_dialog_confirm);
        androidx.appcompat.widget.AppCompatCheckBox checkBox = dialog.findViewById(com.jj.base.R.id.cb_no_more);
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
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (onDialogClick != null) onDialogClick.onSelected(isChecked);
            }
        });
        dialog.show();
    }

    /**
     * 失败提醒对话框
     */
    public void showPayFail() {
        final Dialog dialog = new Dialog(mContext, R.style.comic_Dialog_no_title);
        dialog.setContentView(R.layout.comic_pay_fail);
        dialog.setCancelable(false);

        dialog.findViewById(R.id.comic_pay_fail_dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 付费章节提醒对话框
     *
     * @param price
     * @param remainingGold
     * @param isAutoPay
     * @param onDialogClick
     */
    public void showRechargeNotify(int price, int remainingGold, final boolean isAutoPay, final DialogUtilForComic.OnDialogClickWithSelect onDialogClick) {
        final Dialog dialog = new Dialog(mContext, R.style.comic_Dialog_no_title);
        dialog.setContentView(R.layout.comic_bg_alert_pay);

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KEYCODE_BACK && dialog != null) {
                    dialog.dismiss();
                    ((Activity) mContext).finish();
                }
                return true;
            }
        });
        TextView tv_alert_pay_desc = dialog.findViewById(R.id.tv_alert_pay_desc);
        TextView tv_alert_pay_remaining = dialog.findViewById(R.id.tv_alert_pay_remaining);
        RelativeLayout btn_alert_pay_auto = dialog.findViewById(R.id.btn_alert_pay_auto);
        ImageView btn_alert_pay_now = dialog.findViewById(R.id.btn_alert_pay_now);
        ImageView btn_alert_pay_close = dialog.findViewById(R.id.btn_alert_pay_close);
        final ImageView iv_alert_pay_auto = dialog.findViewById(R.id.iv_alert_pay_auto);

        iv_alert_pay_auto.setSelected(isAutoPay);
        ILFactory.getLoader().loadResource(btn_alert_pay_now, price > remainingGold ? R.drawable.btn_comic_subscribe_pay_now : R.drawable.btn_comic_subscribe_yuedu, null);


        String desc = "解锁本章节仅需要 " + price + " 金币";
        SpannableString descSpan = new SpannableString(desc);
        RelativeSizeSpan sizeSpanDesc = new RelativeSizeSpan(1.7f);
        ForegroundColorSpan colorSpanDesc =
                new ForegroundColorSpan(mContext.getResources().getColor(R.color.comic_ff4723));
        descSpan.setSpan(sizeSpanDesc, 9, 10 + (price + "").length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        descSpan.setSpan(colorSpanDesc, 9, 10 + (price + "").length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        String remaining = "金币余额 " + remainingGold;
        SpannableString remainingSpan = new SpannableString(remaining);
        RelativeSizeSpan sizeSpanRemaining = new RelativeSizeSpan(0.9f);
        ForegroundColorSpan colorSpanRemaining =
                new ForegroundColorSpan(mContext.getResources().getColor(R.color.comic_ff4723));
        remainingSpan.setSpan(sizeSpanRemaining, 4, 5 + (remainingGold + "").length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        remainingSpan.setSpan(colorSpanRemaining, 4, 5 + (remainingGold + "").length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tv_alert_pay_desc.setText(descSpan);
        tv_alert_pay_remaining.setText(remainingSpan);


        btn_alert_pay_auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAutoPay) {
                    iv_alert_pay_auto.setSelected(false);
                } else {
                    iv_alert_pay_auto.setSelected(true);
                }
                if (onDialogClick != null) {
                    onDialogClick.onSelected(!isAutoPay);
                }
            }
        });

        btn_alert_pay_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (onDialogClick != null) onDialogClick.onRefused();
            }
        });

        btn_alert_pay_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDialogClick != null) onDialogClick.onConfirm();
            }
        });

        dialog.show();
    }
}
