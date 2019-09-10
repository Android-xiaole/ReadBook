package com.jj.comics.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jj.comics.R;

import androidx.annotation.Nullable;

public class UserItemView extends LinearLayout {
    public UserItemView(Context context) {
        super(context);
        init(context, null);
    }

    public UserItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public UserItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.comic_user_item_view, this, true);
        ImageView headImg = findViewById(R.id.user_info_head_img);//右侧头像
        TextView title = findViewById(R.id.title);//标题
        View line = findViewById(R.id.bottom_line);//分割线
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.UserItemView);

        String titleText = typedArray.getString(R.styleable.UserItemView_user_item_title);
        title.setText(titleText);

        int headVisible = typedArray.getInt(R.styleable.UserItemView_head_img_visible, View.GONE);
        headImg.setVisibility(headVisible);

        int bottomLineVisible = typedArray.getInt(R.styleable.UserItemView_bottom_line_visible, View.GONE);
        line.setVisibility(bottomLineVisible);

        typedArray.recycle();
    }
}
