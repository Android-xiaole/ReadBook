package com.jj.comics.widget.comic.toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.jj.comics.R;

public class ComicToolBar extends LinearLayout implements IToolBarFactory, View.OnClickListener {

    private AppBarLayout rootView;//根布局
    private ImageView iv_leftIcon, iv_rightIcon;
    private TextView tv_title;

    private OnComicToolBarListener onChildClickListener;

    public ComicToolBar(Context context) {
        super(context);
    }

    public ComicToolBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ComicToolBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 初始化
     */
    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.comic_toolbar, this, true);
        rootView = findViewById(R.id.rootView);
        iv_leftIcon = findViewById(R.id.iv_leftIcon);
        tv_title = findViewById(R.id.tv_title);
        iv_rightIcon = findViewById(R.id.iv_rightIcon);

        iv_leftIcon.setOnClickListener(this);
        iv_rightIcon.setOnClickListener(this);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ComicToolBar);
        //设置背景
        int backgroundColor = typedArray.getColor(R.styleable.ComicToolBar_backgroundColor,
                Color.argb(0xff,0xff,0xff,0xff));
        setBackgroundColorRootView(backgroundColor);
        //设置阴影是否可见，暂时无论怎么设置都没阴影，原因不明
//        boolean shadowVisible = typedArray.getBoolean(R.styleable.ComicToolBar_shadowVisible, true);
//        setShadow(shadowVisible);

        //设置标题文字
        String titleText = typedArray.getString(R.styleable.ComicToolBar_title);
        setTitleText(titleText);
        //设置标题颜色
        int titleColor = typedArray.getColor(R.styleable.ComicToolBar_titleColor,
                Color.argb(0xff,0x33,0x33,0x33));
        setTitleColor(titleColor);

        //设置左边icon是否可见
        int leftIconVisible = typedArray.getInt(R.styleable.ComicToolBar_rightIconVisible, View.VISIBLE);
        setLeftIconVisible(leftIconVisible);
        //设置左边icon图片资源
        int leftIconRes = typedArray.getResourceId(R.styleable.ComicToolBar_leftIconRes,
                R.drawable.icon_back_black);
        setLeftIconRes(leftIconRes);
        //设置右边icon图片资源
        int rightIconRes = typedArray.getResourceId(R.styleable.ComicToolBar_rightIconRes,R.drawable.icon_comic_home);
        setRightIconRes(rightIconRes);
        //设置右边icon是否可见
        int rightIconVisible = typedArray.getInt(R.styleable.ComicToolBar_rightIconVisible, View.GONE);
        setRightIconVisible(rightIconVisible);
    }

    public interface OnComicToolBarListener {
        void onComicToolBarLeftIconClick(View childView);
        void onComicToolBarRightIconClick(View childView);
    }

    @Override
    public void addChildClickListener(OnComicToolBarListener onChildClickListener) {
        this.onChildClickListener = onChildClickListener;
    }

    @Override
    public void removeChildClickListener() {
        if (onChildClickListener != null) {
            onChildClickListener = null;
        }
    }

    @Override
    public IToolBarFactory setBackgroundColorRootView(int color) {
        rootView.setBackgroundColor(color);
        return this;
    }

    @Override
    public IToolBarFactory setShadow(boolean visible) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (visible) {
                rootView.setElevation(8);
            } else {
                rootView.setElevation(0);
            }
        }
        return this;
    }

    @Override
    public IToolBarFactory setTitleText(String title) {
        if (title == null){
            tv_title.setText("");
        }else{
            tv_title.setText(title);
        }
        return this;
    }

    @Override
    public String getTitleText() {
        return tv_title.getText().toString();
    }

    @Override
    public IToolBarFactory setTitleColor(int colorRes) {
        tv_title.setTextColor(colorRes);
        return this;
    }

    @Override
    public IToolBarFactory setLeftIconVisible(int visibility) {
        iv_leftIcon.setVisibility(visibility);
        return this;
    }

    @Override
    public IToolBarFactory setLeftIconRes(int resId) {
        iv_leftIcon.setImageResource(resId);
        return this;
    }

    @Override
    public IToolBarFactory setRightIconRes(int resId) {
        iv_rightIcon.setImageResource(resId);
        return this;
    }

    @Override
    public IToolBarFactory setRightIconVisible(int visibility) {
        iv_rightIcon.setVisibility(visibility);
        return this;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.iv_leftIcon) {
            if (onChildClickListener==null){
                Context context = getContext();
                if (context instanceof Activity) {
                    ((Activity) context).finish();
                }
            }else{
                onChildClickListener.onComicToolBarLeftIconClick(v);
            }
        }else if (viewId == R.id.iv_rightIcon){
            if (onChildClickListener != null) {
                onChildClickListener.onComicToolBarRightIconClick(v);
            }
        }
    }

}
