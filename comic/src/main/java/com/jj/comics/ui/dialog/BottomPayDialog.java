package com.jj.comics.ui.dialog;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jj.base.R;
import com.jj.comics.common.constants.Constants;

public class BottomPayDialog {

    private CancelOnClickListener cancelOnClickListener;
    private AliPayOnClickListener aliPayOnClickListener;
    private WeChatOnClickListener weChatOnClickListener;
    private HuifubaoOnClickListener mHuifubaoOnClickListener;
    private PopupWindow mPopWindow;

    /**
     * 从底部弹出popupwindow
     */
    public void showBottomPop(final Activity activity, View parent,
                              final AliPayOnClickListener mAliPayOnClickListener,
                              WeChatOnClickListener mWeChatOnClickListener,
                              final CancelOnClickListener mCancelClickListener,
                              final HuifubaoOnClickListener huifubaoOnClickListener) {
        final View popView = View.inflate(activity, R.layout.base_dialog_pay, null);
        showAnimation(popView);//开启动画
        mPopWindow = new PopupWindow(popView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mPopWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopWindow.showAtLocation(parent,
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        mPopWindow.setOutsideTouchable(true);
        mPopWindow.setFocusable(true);
        mPopWindow.update();
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = 0.3f;
        activity.getWindow().setAttributes(lp);
        mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                lp.alpha = 1f;
                activity.getWindow().setAttributes(lp);
            }
        });
        cancelOnClickListener = mCancelClickListener;
        weChatOnClickListener = mWeChatOnClickListener;
        aliPayOnClickListener = mAliPayOnClickListener;
        mHuifubaoOnClickListener = huifubaoOnClickListener;

        ImageView cancel = popView.findViewById(R.id.base_pay_dialog_cancel);
        LinearLayout alipay = popView.findViewById(R.id.base_pay_dialog_alipay);
        LinearLayout wechat = popView.findViewById(R.id.base_pay_dialog_wechat);
        LinearLayout huifubao = popView.findViewById(R.id.base_pay_dialog_huifubao);
        TextView only_alipay = popView.findViewById(R.id.only_alipay);
        TextView only_wechatpay = popView.findViewById(R.id.only_wechatpay);
        //如果没有获取到wxId则不显示微信支付
        if (Constants.WX_APP_ID_PAY() == null || Constants.WX_APP_ID_PAY().length() == 0) {
            wechat.setVisibility(View.GONE);
        }

        if (wechat.getVisibility() == View.VISIBLE) {
            only_alipay.setVisibility(View.GONE);
        } else {
            only_alipay.setVisibility(View.VISIBLE);
        }

        if (alipay.getVisibility() == View.VISIBLE) {
            only_wechatpay.setVisibility(View.GONE);
        } else {
            only_wechatpay.setVisibility(View.VISIBLE);
        }
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelOnClickListener.onClick(v);
            }
        });

        alipay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aliPayOnClickListener.onClick(v);
            }
        });
        wechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weChatOnClickListener.onClick(v);
            }
        });
        huifubao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHuifubaoOnClickListener.onClick(v);
            }
        });
    }

    /**
     * 给popupwindow添加动画
     *
     * @param popView
     */
    private void showAnimation(View popView) {
        AnimationSet animationSet = new AnimationSet(false);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1.0f);
        alphaAnimation.setDuration(100);
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f
        );
        translateAnimation.setDuration(100);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(translateAnimation);
        popView.startAnimation(animationSet);
    }

    /**
     * 取消对话框
     */
    public void dismiss() {
        if (mPopWindow != null) {
            mPopWindow.dismiss();
        }
    }

    public interface AliPayOnClickListener {
        void onClick(View v);
    }

    public interface WeChatOnClickListener {
        void onClick(View v);
    }

    public interface HuifubaoOnClickListener {
        void onClick(View v);
    }

    public interface CancelOnClickListener {
        void onClick(View v);
    }

}
