package com.jj.comics.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.jj.base.utils.Utils;
import com.jj.comics.R;
import com.jj.comics.util.EditTextFilterUtil;
import com.jj.comics.widget.comic.comicview.BottomBar;

import androidx.annotation.NonNull;

public class CommentSendDialog extends Dialog implements
        DialogInterface.OnShowListener,
        DialogInterface.OnDismissListener,
        DialogInterface.OnCancelListener {

    private InputMethodManager imm;
    private EditText mDialogEditText;
    private TextView tv_send;
    private OnDismissListener mOnDismissListenerForActivity;

    private BottomBar mBottomBar;

    public CommentSendDialog(@NonNull final Context context,@NonNull BottomBar mBottomBar,OnDismissListener onDismissListener) {
        super(context, R.style.comic_dialog_style_send_comment);
        this.mBottomBar = mBottomBar;
        mOnDismissListenerForActivity = onDismissListener;
        Window window = getWindow();
        if (window != null) {
            //设置布局
            View contentView = View.inflate(context, R.layout.comic_read_sendcomment_dialog, null);
            mDialogEditText = contentView.findViewById(R.id.et_content);
            tv_send = contentView.findViewById(R.id.tv_send);
            setContentView(contentView);
            //设置Dialog的其他属性
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;//赋值宽度
            lp.height = Utils.dip2px(context, 55);//赋值高度
            lp.gravity = Gravity.BOTTOM;//赋值方位
            window.setAttributes(lp);
            //设置Dialog的动画效果
//            window.setWindowAnimations(animation);
            //初始化软键盘服务管理类
            imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            //设置点击Dialog外部会消失 ，下面两个设置效果会回调onCancel --onDismiss方法，我们在里面关闭一下软键盘
            setCanceledOnTouchOutside(true);
            //点返回键Diloag会消失
            setCancelable(true);
            //初始化Dialog的监听
            setOnShowListener(this);
            setOnDismissListener(this);
            setOnCancelListener(this);

            mDialogEditText.setFilters(new InputFilter[]{EditTextFilterUtil.if_emoji});
        }
    }

    public CommentSendDialog(@NonNull final Context context,OnDismissListener onDismissListener) {
        super(context, R.style.comic_dialog_style_send_comment);
        mOnDismissListenerForActivity = onDismissListener;
        Window window = getWindow();
        if (window != null) {
            //设置布局
            View contentView = View.inflate(context, R.layout.comic_read_sendcomment_dialog, null);
            mDialogEditText = contentView.findViewById(R.id.et_content);
            tv_send = contentView.findViewById(R.id.tv_send);
            setContentView(contentView);
            //设置Dialog的其他属性
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;//赋值宽度
            lp.height = Utils.dip2px(context, 55);//赋值高度
            lp.gravity = Gravity.BOTTOM;//赋值方位
            window.setAttributes(lp);
            //设置Dialog的动画效果
//            window.setWindowAnimations(animation);
            //初始化软键盘服务管理类
            imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            //设置点击Dialog外部会消失 ，下面两个设置效果会回调onCancel --onDismiss方法，我们在里面关闭一下软键盘
            setCanceledOnTouchOutside(true);
            //点返回键Diloag会消失
            setCancelable(true);
            //初始化Dialog的监听
            setOnShowListener(this);
            setOnDismissListener(this);
            setOnCancelListener(this);

            mDialogEditText.setFilters(new InputFilter[]{EditTextFilterUtil.if_emoji});
        }
    }

    /**
     * 设置发送评论的点击事件
     * @param onClickListener
     */
    public void setOnClickListener(View.OnClickListener onClickListener){
        tv_send.setOnClickListener(onClickListener);
    }

    /**
     * 获取输入框内容
     * @return
     */
    public String getContent(){
        if (mDialogEditText!=null){
            return mDialogEditText.getText().toString();
        }
        return "";
    }


    /**
     * 清除输入框内容
     */
    public void clearInput() {
        if (mDialogEditText != null) {
            mDialogEditText.setText("");
            if (imm != null) imm.hideSoftInputFromWindow(mDialogEditText.getWindowToken(), 0);
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (imm != null) imm.hideSoftInputFromWindow(mDialogEditText.getWindowToken(), 0);
        if (mBottomBar!=null)mBottomBar.show();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (imm != null) imm.hideSoftInputFromWindow(mDialogEditText.getWindowToken(), 0);
        if (mBottomBar!=null)mBottomBar.show();
        if (mOnDismissListenerForActivity != null) {
            mOnDismissListenerForActivity.onDismiss(dialog);
        }
    }

    @Override
    public void onShow(DialogInterface dialog) {
        if (imm != null&&mDialogEditText!=null) {
            //弹出键盘的方法需要设置一点延时，防止低性能手机界面绘制太慢无法弹出
            mDialogEditText.postDelayed(new Runnable() {
                @Override
                public void run() {
                    imm.showSoftInput(mDialogEditText, 0);
                }
            },100);
        }
        if (mBottomBar!=null)mBottomBar.hideNoAnim();
    }


    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        //点击弹窗外部区域
        if (isOutOfBounds(getContext(), event)) {
            if (imm != null) imm.hideSoftInputFromWindow(mDialogEditText.getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

    private boolean isOutOfBounds(Context context, MotionEvent event) {
        final int x = (int) event.getX();//相对弹窗左上角的x坐标
        final int y = (int) event.getY();//相对弹窗左上角的y坐标
        final int slop = ViewConfiguration.get(context).getScaledWindowTouchSlop();//最小识别距离
        final View decorView = getWindow().getDecorView();//弹窗的根View
        return (x < -slop) || (y < -slop) || (x > (decorView.getWidth() + slop))
                || (y > (decorView.getHeight() + slop));
    }
}
