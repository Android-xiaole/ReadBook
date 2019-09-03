package com.jj.comics.ui.dialog;

public class DialogUtilForComic {

    /**
     * 常规回调
     */
    public interface OnDialogClick {
        void onConfirm();

        void onRefused();
    }

    /**
     * 带勾选框的回调
     */
    public interface OnDialogClickWithSelect {
        void onConfirm();

        void onRefused();

        void onSelected(boolean selested);
    }

//    private static boolean mIsAutoPay;
//
//    public static AlertDialog showRechargeNotifyDialog(final Activity context, int price,
//                                                       int remainingGold, boolean isAutoPay,
//                                                       final OnDialogClick onDialogClick,
//                                                       DialogInterface.OnDismissListener onDismissListener) {
//        final AlertDialog dialog = new AlertDialog.Builder(context, R.style.comic_Dialog_no_title).create();
//        dialog.show();
//        dialog.setOnDismissListener(onDismissListener);
//        Window dialogWindow = dialog.getWindow();
//        WindowManager.LayoutParams attributes = dialogWindow.getAttributes();
//
//        attributes.gravity = Gravity.CENTER;
////        attributes.height = WindowManager.LayoutParams.MATCH_PARENT;
////        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
//
//        dialogWindow.setAttributes(attributes);
//        dialog.setCancelable(false);
//        dialogWindow.setContentView(R.layout.comic_bg_alert_pay);
//
//        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                if (keyCode == KEYCODE_BACK && dialog != null) {
//                    dialog.dismiss();
//                    context.finish();
//                }
//                return true;
//            }
//        });
//        TextView tv_alert_pay_desc = (TextView) dialogWindow.findViewById(R.id.tv_alert_pay_desc);
//        TextView tv_alert_pay_remaining = (TextView) dialogWindow.findViewById(R.id.tv_alert_pay_remaining);
//        RelativeLayout btn_alert_pay_auto = (RelativeLayout) dialogWindow.findViewById(R.id.btn_alert_pay_auto);
//        ImageView btn_alert_pay_now = (ImageView) dialogWindow.findViewById(R.id.btn_alert_pay_now);
//        ImageView btn_alert_pay_close = (ImageView) dialogWindow.findViewById(R.id.btn_alert_pay_close);
//        final ImageView iv_alert_pay_auto = (ImageView) dialogWindow.findViewById(R.id.iv_alert_pay_auto);
//
//        mIsAutoPay = isAutoPay;
//        iv_alert_pay_auto.setSelected(mIsAutoPay);
//
//
//        String desc = "解锁本章节仅需要 " + price + " 金币";
//        SpannableString descSpan = new SpannableString(desc);
//        RelativeSizeSpan sizeSpanDesc = new RelativeSizeSpan(1.7f);
//        ForegroundColorSpan colorSpanDesc =
//                new ForegroundColorSpan(context.getResources().getColor(R.color.comic_ff4723));
//        descSpan.setSpan(sizeSpanDesc, 9, 10 + (price + "").length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        descSpan.setSpan(colorSpanDesc, 9, 10 + (price + "").length(),
//                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        String remaining = "金币余额 " + remainingGold;
//        SpannableString remainingSpan = new SpannableString(remaining);
//        RelativeSizeSpan sizeSpanRemaining = new RelativeSizeSpan(0.9f);
//        ForegroundColorSpan colorSpanRemaining =
//                new ForegroundColorSpan(context.getResources().getColor(R.color.comic_ff4723));
//        remainingSpan.setSpan(sizeSpanRemaining, 4, 5 + (remainingGold + "").length(),
//                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        remainingSpan.setSpan(colorSpanRemaining, 4, 5 + (remainingGold + "").length(),
//                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        tv_alert_pay_desc.setText(descSpan);
//        tv_alert_pay_remaining.setText(remainingSpan);
//
//
//        btn_alert_pay_auto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mIsAutoPay) {
//                    iv_alert_pay_auto.setSelected(false);
//                } else {
//                    iv_alert_pay_auto.setSelected(true);
//                }
//                mIsAutoPay = !mIsAutoPay;
//                if (onDialogClick != null) onDialogClick.onSelected(mIsAutoPay);
//            }
//        });
//
//        btn_alert_pay_close.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                if (onDialogClick != null) onDialogClick.onRefused();
//            }
//        });
//
//        btn_alert_pay_now.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (onDialogClick != null) onDialogClick.onConfirm();
//            }
//        });
//        return dialog;
//    }
//
//    private static boolean isLogingDialogShow = false;
//
//    public static AlertDialog showLoginNotifyDialog(final Activity context,
//                                                    final OnDialogClick onDialogClick,
//                                                    DialogInterface.OnDismissListener onDismissListener) {
//        if (!isLogingDialogShow) {
//            final AlertDialog dialog = new AlertDialog.Builder(context, R.style.comic_Dialog_no_title).create();
//            dialog.setOnDismissListener(onDismissListener);
//            Window dialogWindow = dialog.getWindow();
//            WindowManager.LayoutParams attributes = dialogWindow.getAttributes();
//
//            attributes.gravity = Gravity.CENTER;
////        attributes.height = WindowManager.LayoutParams.MATCH_PARENT;
////        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
//
//            dialogWindow.setAttributes(attributes);
//            dialog.setCancelable(false);
//            dialog.show();
//            isLogingDialogShow = true;
//            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//                @Override
//                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                    if (keyCode == KEYCODE_BACK && dialog != null) {
//                        dialog.dismiss();
//                        isLogingDialogShow = false;
//                        context.finish();
//                    }
//                    return true;
//                }
//            });
//            dialogWindow.setContentView(R.layout.comic_bg_alert_login);
//            ImageView btn_alert_login_now = (ImageView) dialogWindow.findViewById(R.id.btn_alert_login_now);
//            ImageView btn_alert_login_close = (ImageView) dialogWindow.findViewById(R.id.btn_alert_login_close);
//
//
//            btn_alert_login_close.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dialog.dismiss();
//                    isLogingDialogShow = false;
//                    if (onDialogClick != null) onDialogClick.onRefused();
//                }
//            });
//
//            btn_alert_login_now.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    isLogingDialogShow = false;
//                    if (onDialogClick != null) onDialogClick.onConfirm();
//                }
//            });
//            return dialog;
//        }
//        return null;
//    }
//
//
//    private static boolean isFreeGoldDialogShow = false;
//
//    public static AlertDialog showFreeGoldDialog(final Activity context,
//                                                 final OnDialogClick onDialogClick,
//                                                 DialogInterface.OnDismissListener onDismissListener) {
//
//        if (!isFreeGoldDialogShow) {
//            final AlertDialog dialog = new AlertDialog.Builder(context, R.style.comic_Dialog_no_title).create();
//            dialog.setOnDismissListener(onDismissListener);
//            Window dialogWindow = dialog.getWindow();
//            WindowManager.LayoutParams attributes = dialogWindow.getAttributes();
//
//            attributes.gravity = Gravity.CENTER;
////        attributes.height = WindowManager.LayoutParams.MATCH_PARENT;
////        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
//
//            dialogWindow.setAttributes(attributes);
//            dialog.setCancelable(false);
//            dialog.show();
//            isFreeGoldDialogShow = true;
//            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//                @Override
//                public boolean onKey(DialogInterface d, int keyCode, KeyEvent event) {
//                    if (keyCode == KEYCODE_BACK && dialog != null) {
//                        dialog.dismiss();
//                        context.finish();
//                        isFreeGoldDialogShow = false;
//                    }
//                    return true;
//                }
//            });
//            dialogWindow.setContentView(R.layout.comic_bg_alert_free_gold);
//            TextView btn_know = (TextView) dialogWindow.findViewById(R.id.btn_know);
//            TextView btn_go_read = (TextView) dialogWindow.findViewById(R.id.btn_go_read);
//            ImageView iv = (ImageView) dialogWindow.findViewById(R.id.iv_bg);
//
//            ILFactory.getLoader().loadResource(iv, R.drawable.bg_comic_dialog_free_gold,
//                    new RequestOptions().transforms(new FitCenter(), new RoundedCorners(15)));
//
//            btn_know.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dialog.dismiss();
//                    isFreeGoldDialogShow = false;
//                    if (onDialogClick != null) onDialogClick.onRefused();
//                }
//            });
//
//            btn_go_read.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    isFreeGoldDialogShow = false;
//                    dialog.dismiss();
//                    if (onDialogClick != null) onDialogClick.onConfirm();
//                }
//            });
//            return dialog;
//        }
//
//        return null;
//    }
//
//    public static AlertDialog showCollection(final BaseActivity baseActivity, final OnDialogClick onDialogClick
//                                                ,DialogInterface.OnDismissListener onDismissListener) {
//        final AlertDialog dialog = new AlertDialog.Builder(baseActivity, R.style.comic_Dialog_no_title).create();
//        dialog.setOnDismissListener(onDismissListener);
//        Window dialogWindow = dialog.getWindow();
//        WindowManager.LayoutParams attributes = dialogWindow.getAttributes();
//
//        attributes.gravity = Gravity.CENTER;
////        attributes.height = WindowManager.LayoutParams.MATCH_PARENT;
////        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
//
//        dialogWindow.setAttributes(attributes);
//        dialog.setCancelable(false);
//        dialog.show();
//        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface d, int keyCode, KeyEvent event) {
//                if (keyCode == KEYCODE_BACK && dialog != null) {
//                    dialog.dismiss();
//                }
//                return true;
//            }
//        });
//        dialogWindow.setContentView(R.layout.comic_bg_alert_collection);
//        TextView btn_know = (TextView) dialogWindow.findViewById(R.id.btn_ignore);
//        TextView btn_go_read = (TextView) dialogWindow.findViewById(R.id.btn_ok);
//
//        btn_know.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                if (onDialogClick != null) onDialogClick.onRefused();
//            }
//        });
//
//        btn_go_read.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                if (onDialogClick != null) onDialogClick.onConfirm();
//            }
//        });
//        return dialog;
//    }

//    public interface OnDialogClickWithNoMore {
//        void onConfirm();
//
//        void onRefused();
//
//        void onNoMore(boolean noMore);
//    }
}
