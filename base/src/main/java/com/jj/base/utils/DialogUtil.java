package com.jj.base.utils;

import android.app.Activity;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.jj.base.R;
import com.umeng.analytics.MobclickAgent;

import static android.view.KeyEvent.KEYCODE_BACK;

/**
 * FBI WARNING ! MAGIC ! DO NOT TOUGH !
 * Created by WangZQ on 2018/8/31 - 14:12.
 */
public class DialogUtil {

    public static void showDeleteDialog(final Activity context, String msg, final OnDialogClick onDialogClick) {
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        Window dialogWindow = dialog.getWindow();

        WindowManager.LayoutParams attributes = dialogWindow.getAttributes();

        attributes.gravity = Gravity.CENTER;

        dialogWindow.setAttributes(attributes);
        dialog.setCancelable(false);
        dialog.show();

        dialogWindow.setContentView(R.layout.base_delete_layout_dialog);
        TextView tv_dialog_msg = (TextView) dialogWindow.findViewById(R.id.delete_dialog_msg);
        TextView btn_dialog_cancel = (TextView) dialogWindow.findViewById(R.id.delete_dialog_cancel);
        TextView btn_dialog_confirm = (TextView) dialogWindow.findViewById(R.id.delete_dialog_confirm);
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

    }

    public static void showDeleteDialogWithNoMore(final Activity context, String msg,
                                         final OnDialogClickWithNoMore onDialogClick) {
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        Window dialogWindow = dialog.getWindow();

        WindowManager.LayoutParams attributes = dialogWindow.getAttributes();

        attributes.gravity = Gravity.CENTER;

        dialogWindow.setAttributes(attributes);
        dialog.setCancelable(false);
        dialog.show();

        dialogWindow.setContentView(R.layout.base_delete_layout_dialog_no_more);
        TextView tv_dialog_msg = (TextView) dialogWindow.findViewById(R.id.delete_dialog_msg);
        TextView btn_dialog_cancel = (TextView) dialogWindow.findViewById(R.id.delete_dialog_cancel);
        TextView btn_dialog_confirm = (TextView) dialogWindow.findViewById(R.id.delete_dialog_confirm);
        androidx.appcompat.widget.AppCompatCheckBox checkBox = (androidx.appcompat.widget.AppCompatCheckBox) dialogWindow.findViewById(R.id.cb_no_more);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (onDialogClick != null) onDialogClick.onNoMore(isChecked);
            }
        });
//        CheckBox cb = dialogWindow.findViewById(R.id.cb_no_more);
//        Drawable drawable = context.getResources().getDrawable(R.drawable.base_button_checkbox);
//        drawable.setBounds(0,0,40,40);
//        cb.setCompoundDrawables(drawable,null,null,null);

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

    }

    public static void showRemindDialogWithNoMore(final Activity context,String title, String msg,
                                                  final OnDialogClickWithNoMore onDialogClick) {
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        Window dialogWindow = dialog.getWindow();

        WindowManager.LayoutParams attributes = dialogWindow.getAttributes();

        attributes.gravity = Gravity.CENTER;

        dialogWindow.setAttributes(attributes);
        dialog.setCancelable(false);
        dialog.show();

        dialogWindow.setContentView(R.layout.base_delete_layout_dialog_no_more);
        TextView tv_dialog_msg = (TextView) dialogWindow.findViewById(R.id.delete_dialog_msg);
        TextView btn_dialog_cancel = (TextView) dialogWindow.findViewById(R.id.delete_dialog_cancel);
        TextView btn_dialog_confirm = (TextView) dialogWindow.findViewById(R.id.delete_dialog_confirm);
        TextView tv_title = (TextView) dialogWindow.findViewById(R.id.tv_title);
        androidx.appcompat.widget.AppCompatCheckBox checkBox = (androidx.appcompat.widget.AppCompatCheckBox) dialogWindow.findViewById(R.id.cb_no_more);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (onDialogClick != null) onDialogClick.onNoMore(isChecked);
            }
        });
//        CheckBox cb = dialogWindow.findViewById(R.id.cb_no_more);
//        Drawable drawable = context.getResources().getDrawable(R.drawable.base_button_checkbox);
//        drawable.setBounds(0,0,40,40);
//        cb.setCompoundDrawables(drawable,null,null,null);

        tv_dialog_msg.setText(msg);
        tv_title.setText(title);

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

    }

    public static AlertDialog showPayConfirmDialog(final Activity context, String msg, final OnDialogClick onDialogClick) {
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        Window dialogWindow = dialog.getWindow();

        WindowManager.LayoutParams attributes = dialogWindow.getAttributes();

        attributes.gravity = Gravity.CENTER;

        dialogWindow.setAttributes(attributes);
        dialog.setCancelable(false);
        dialog.show();
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KEYCODE_BACK && dialog != null) {
                    dialog.dismiss();
                    context.finish();
                }
                return true;
            }
        });
        dialogWindow.setContentView(R.layout.base_normal_layout_dialog);
        TextView tv_dialog_msg = (TextView) dialogWindow.findViewById(R.id.delete_dialog_msg);
        TextView btn_dialog_cancel = (TextView) dialogWindow.findViewById(R.id.delete_dialog_cancel);
        TextView btn_dialog_confirm = (TextView) dialogWindow.findViewById(R.id.delete_dialog_confirm);
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
                if (onDialogClick != null) onDialogClick.onConfirm();
            }
        });
        return dialog;
    }



    public static void showExitDialog(final Activity context ,String msg) {
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        Window dialogWindow = dialog.getWindow();

        WindowManager.LayoutParams attributes = dialogWindow.getAttributes();

        attributes.gravity = Gravity.CENTER;

        dialogWindow.setAttributes(attributes);
        dialog.setCancelable(false);
        dialog.show();

        dialogWindow.setContentView(R.layout.base_layout_dialog);
        TextView tv_dialog_title = (TextView) dialogWindow.findViewById(R.id.tv_dialog_title);
        TextView tv_dialog_msg = (TextView) dialogWindow.findViewById(R.id.tv_dialog_msg);
        TextView btn_dialog_cancel = (TextView) dialogWindow.findViewById(R.id.btn_dialog_cancel);
        TextView btn_dialog_confirm = (TextView) dialogWindow.findViewById(R.id.btn_dialog_confirm);
        tv_dialog_title.setText("提醒");
        tv_dialog_msg.setText("初始化失败，请稍后重试！" + msg);
        btn_dialog_cancel.setVisibility(View.GONE);
//        btn_dialog_cancel.setText("狠心离开");
        btn_dialog_confirm.setText("退出应用");

        dialogWindow.findViewById(R.id.btn_dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                MobclickAgent.onKillProcess(context);
                //参数用作状态码；根据惯例，非 0 的状态码表示异常终止。
                System.exit(0);
            }
        });

        dialogWindow.findViewById(R.id.btn_dialog_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                MobclickAgent.onKillProcess(context);
                //参数用作状态码；根据惯例，非 0 的状态码表示异常终止。
                System.exit(0);
            }
        });

    }

//    public static void showStatementDialog(final Activity context,
//                                           final String dialogTitle,
//                                           final String dialogMsg,
//                                           final OnDialogClick onDialogClick) {
//        final AlertDialog dialog = new AlertDialog.Builder(context).create();
//        Window dialogWindow = dialog.getWindow();
//
//        WindowManager.LayoutParams attributes = dialogWindow.getAttributes();
//
//        attributes.gravity = Gravity.CENTER;
//
//        dialogWindow.setAttributes(attributes);
//        dialog.setCancelable(false);
//        dialog.show();
//
//        dialogWindow.setContentView(R.layout.base_state_layout_dialog);
//        TextView tv_dialog_title = (TextView) dialogWindow.findViewById(R.id.tv_dialog_title);
//        TextView tv_dialog_msg = (TextView) dialogWindow.findViewById(R.id.tv_dialog_msg);
//        tv_dialog_msg.setMovementMethod(ScrollingMovementMethod.getInstance());
//        TextView btn_dialog_cancel = (TextView) dialogWindow.findViewById(R.id.btn_dialog_cancel);
//        TextView btn_dialog_confirm = (TextView) dialogWindow.findViewById(R.id.btn_dialog_confirm);
//        tv_dialog_title.setText(dialogTitle);
//        tv_dialog_msg.setText(dialogMsg);
//        btn_dialog_cancel.setText("取消");
//        btn_dialog_confirm.setText("确定");
//
//        dialogWindow.findViewById(R.id.btn_dialog_cancel).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                if (onDialogClick != null) onDialogClick.onRefused();
//            }
//        });
//
//        dialogWindow.findViewById(R.id.btn_dialog_confirm).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                if (onDialogClick != null) onDialogClick.onConfirm();
//            }
//        });
//
//    }

//    public static void showCommonDialog(final Activity context,
//                                        final String dialogMsg,
//                                        final OnDialogClick onDialogClick) {
//        final AlertDialog dialog = new AlertDialog.Builder(context,R.style.base_dialogTransparent).create();
//        Window dialogWindow = dialog.getWindow();
////        WindowManager.LayoutParams attributes = dialogWindow.getAttributes();
////
////        attributes.gravity = Gravity.CENTER;
////        attributes.alpha = 0.3f;   //取值为0-1之间  0是全透明，1是不透明
////        attributes.dimAmount = 0.3f;   //取值为0-1之间  1是全黑
////        dialogWindow.setAttributes(attributes);
//        dialog.setCancelable(false);
//        dialog.show();
//        dialogWindow.setContentView(R.layout.base_common_layout_dialog);
//        TextView tv_dialog_msg = (TextView) dialogWindow.findViewById(R.id.tv_dialog_msg);
//        tv_dialog_msg.setMovementMethod(ScrollingMovementMethod.getInstance());
//        TextView btn_dialog_confirm = (TextView) dialogWindow.findViewById(R.id.btn_dialog_confirm);
//        tv_dialog_msg.setText(dialogMsg);
//        btn_dialog_confirm.setText("确定");
//
//        dialogWindow.findViewById(R.id.btn_dialog_cancel).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                if (onDialogClick != null) onDialogClick.onRefused();
//            }
//        });
//
//        dialogWindow.findViewById(R.id.btn_dialog_confirm).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                if (onDialogClick != null) onDialogClick.onConfirm();
//            }
//        });
//
//    }

    public static void showDialog(final Activity context,
                                  final String dialogTitle,
                                  final String dialogMsg,
                                  final String dialogConfirgMsg,
                                  final String dialogRefusedMsg,
                                  final OnDialogClick onDialogClick) {
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        Window dialogWindow = dialog.getWindow();

        WindowManager.LayoutParams attributes = dialogWindow.getAttributes();

        attributes.gravity = Gravity.CENTER;

        dialogWindow.setAttributes(attributes);
        dialog.setCancelable(false);
        dialog.show();

        dialogWindow.setContentView(R.layout.base_layout_dialog);
        TextView tv_dialog_title = (TextView) dialogWindow.findViewById(R.id.tv_dialog_title);
        TextView tv_dialog_msg = (TextView) dialogWindow.findViewById(R.id.tv_dialog_msg);
        TextView btn_dialog_cancel = (TextView) dialogWindow.findViewById(R.id.btn_dialog_cancel);
        TextView btn_dialog_confirm = (TextView) dialogWindow.findViewById(R.id.btn_dialog_confirm);
        tv_dialog_title.setText(dialogTitle);
        tv_dialog_msg.setText(dialogMsg);
        btn_dialog_cancel.setText(dialogRefusedMsg);
        btn_dialog_confirm.setText(dialogConfirgMsg);

        dialogWindow.findViewById(R.id.btn_dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDialogClick.onRefused();
                dialog.dismiss();
            }
        });

        dialogWindow.findViewById(R.id.btn_dialog_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDialogClick.onConfirm();
                dialog.dismiss();
            }
        });

    }

    public interface OnDialogClick {
        void onConfirm();

        void onRefused();
    }

    public interface OnDialogClickWithNoMore {
        void onConfirm();

        void onRefused();

        void onNoMore(boolean noMore);
    }
}
