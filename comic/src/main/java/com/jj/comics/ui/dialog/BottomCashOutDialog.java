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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jj.comics.R;

public class BottomCashOutDialog {

    private DialogOnClickListener mOnClickListener;
    private PopupWindow mPopWindow;

    /**
     * 从底部弹出popupwindow
     */
    public void showBottomPop(final Activity activity, View parent,boolean ali,boolean bank,
                              final DialogOnClickListener onClickListener) {
        final View popView = View.inflate(activity, R.layout.comic_dialog_cash_out, null);
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
        mOnClickListener = onClickListener;

        TextView cancel = popView.findViewById(R.id.pay_dialog_cancel);
        LinearLayout mAlipay = popView.findViewById(R.id.base_pay_dialog_alipay);
        LinearLayout mUnion = popView.findViewById(R.id.base_pay_dialog_union);

        if (!ali) mAlipay.setVisibility(View.GONE);
        if (!bank) mUnion.setVisibility(View.GONE);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.onCancelClick(v);
            }
        });

        mAlipay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.onAliClick(v);
            }
        });

        mUnion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.onUnionClick(v);
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

    public interface DialogOnClickListener {
        void onAliClick(View v);
        void onUnionClick(View v);
        void onCancelClick(View v);
    }


}
