package com.jj.base.ui;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jj.base.R;
import com.jj.base.dialog.CustomDialog;
import com.jj.base.utils.toast.ToastUtil;


public class NetStatusReceiver extends BroadcastReceiver {

    public static final int NETSTATUS_INAVAILABLE = 0;
    public static final int NETSTATUS_WIFI = 1;
    public static final int NETSTATUS_MOBILE = 2;
    public static int netStatus = 0;
    public static boolean updateSuccess = false;
    private INetStatusListener mINetStatusListener;
    private CustomDialog dialog;
    public void onReceive(final Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileNetInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo allNetInfo = cm.getActiveNetworkInfo();

        if (allNetInfo == null) {
            if (mobileNetInfo != null && (mobileNetInfo.isConnected() || mobileNetInfo.isConnectedOrConnecting())) {
                netStatus = NETSTATUS_MOBILE;
            } else if (wifiNetInfo != null && wifiNetInfo.isConnected() || wifiNetInfo.isConnectedOrConnecting()) {
                netStatus = NETSTATUS_WIFI;
            } else {
                netStatus = NETSTATUS_INAVAILABLE;
            }
        } else {
            if (allNetInfo.isConnected() || allNetInfo.isConnectedOrConnecting()) {
                if (mobileNetInfo != null && (mobileNetInfo.isConnected() || mobileNetInfo.isConnectedOrConnecting())) {
                    netStatus = NETSTATUS_MOBILE;
                } else {
                    netStatus = NETSTATUS_WIFI;
                }
            } else {
                netStatus = NETSTATUS_INAVAILABLE;
            }
        }
        if (mINetStatusListener != null) {
            mINetStatusListener.getNetState(netStatus);
        }
        if (netStatus == NETSTATUS_INAVAILABLE) {
//            Toast.makeText(context, "网络未连接", Toast.LENGTH_SHORT).show();
            dialog = new CustomDialog(context, R.style.base_common_CustomDialog, R.layout.base_normal_layout_dialog);
            dialog.show();
            TextView tv_title = dialog.findViewById(com.jj.base.R.id.tv_title);
            TextView tv_msg = dialog.findViewById(com.jj.base.R.id.tv_msg);
            TextView btn_dialog_cancel = dialog.findViewById(com.jj.base.R.id.delete_dialog_cancel);
            TextView btn_dialog_confirm = dialog.findViewById(com.jj.base.R.id.delete_dialog_confirm);

            tv_title.setText("温馨提示");
            tv_msg.setText("请检查网络连接!");
            btn_dialog_confirm.setText("去设置");

            btn_dialog_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            btn_dialog_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                    if (context.getPackageManager().resolveActivity(intent,0) != null){
                        context.startActivity(intent);
                    }else {
                        try {
                            intent = new Intent(Settings.ACTION_SETTINGS);
                            context.startActivity(intent);
                        }catch (ActivityNotFoundException e){
                            ToastUtil.showToastShort("未找到目标，请手动设置");
                        }
                    }
                }
            });
        } else {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    public void setNetStateListener(INetStatusListener listener) {
        mINetStatusListener = listener;
    }

    public interface INetStatusListener {
        public void getNetState(int state);
    }
}