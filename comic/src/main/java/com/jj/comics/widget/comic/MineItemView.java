package com.jj.comics.widget.comic;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.jj.comics.R;

import androidx.annotation.Nullable;

/**
 * 我的界面的item的自定义布局
 */
public class MineItemView extends LinearLayout implements CompoundButton.OnCheckedChangeListener {

    private TextView tv_rightText;
    private Switch switch_autoBuy;
    private TextView tv_tip;//消息提示

    private OnCheckedChangeListener mOnCheckedChangeListener;

    public MineItemView(Context context) {
        super(context);
        init(context,null);
    }

    public MineItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public MineItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context,AttributeSet attrs){
        LayoutInflater.from(context).inflate(R.layout.comic_fragment_mine_itemview, this, true);
        ImageView iv_icon = findViewById(R.id.iv_icon);//左边的icon
        TextView tv_title = findViewById(R.id.tv_title);//右边的大标题
        ImageView iv_navigation = findViewById(R.id.iv_navigation);//右边的导航箭头
        tv_rightText = findViewById(R.id.tv_rightText);//右边的文字显示
        switch_autoBuy = findViewById(R.id.switch_autoBuy);//右边的自动购买开关
        tv_tip = findViewById(R.id.tv_tip);//消息提示

        switch_autoBuy.setOnCheckedChangeListener(this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MineItemView);

        int iconResourceId = typedArray.getResourceId(R.styleable.MineItemView_iconResource,
                R.drawable.ic_launcher);
        iv_icon.setImageResource(iconResourceId);

        String titleText = typedArray.getString(R.styleable.MineItemView_titleText);
        tv_title.setText(titleText);

        int rightTextVisible = typedArray.getInt(R.styleable.MineItemView_rightTextVisible, View.GONE);
        tv_rightText.setVisibility(rightTextVisible);
        String rightText = typedArray.getString(R.styleable.MineItemView_rightText);
        tv_rightText.setText(rightText);

        int switchVisible = typedArray.getInt(R.styleable.MineItemView_switchVisible, View.GONE);
        switch_autoBuy.setVisibility(switchVisible);

        int navigationVisible = typedArray.getInt(R.styleable.MineItemView_navigationVisible, View.VISIBLE);
        iv_navigation.setVisibility(navigationVisible);

        typedArray.recycle();
    }

    /**
     * 设置右边的字体显示
     * @param text
     */
    public void setRightText(String text){
        tv_rightText.setText(text);
    }

    /**
     * 设置开关状态
     * @param checked
     */
    public void setChecked(boolean checked){
        switch_autoBuy.setChecked(checked);
    }

    /**
     * 设置消息提示是否可见
     * @param isVisible
     */
    public void setTipVisible(boolean isVisible){
        tv_tip.setVisibility(isVisible?View.VISIBLE:View.GONE);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (mOnCheckedChangeListener == null)return;
        mOnCheckedChangeListener.onCheckedChanged(buttonView,isChecked);
    }

    /** 设置Switch开关的监听 */
    public void setOnCheckedChangeListener(MineItemView.OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(CompoundButton switchView, boolean isChecked);
    }

}
