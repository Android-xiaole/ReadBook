package com.jj.comics.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jj.comics.R;

import androidx.annotation.Nullable;

public class UserItemView extends LinearLayout {
    private TextView right_title;
    private TextView mTitle;

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
        ImageView rightImg = findViewById(R.id.right_img);
        //标题
        mTitle = findViewById(R.id.title);
        right_title = findViewById(R.id.right_title);//标题
        View line = findViewById(R.id.bottom_line);//分割线
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.UserItemView);

        //view content
        String titleText = typedArray.getString(R.styleable.UserItemView_user_item_title);
        mTitle.setText(titleText);

        String rightText = typedArray.getString(R.styleable.UserItemView_right_text);
        right_title.setText(rightText);

        // view visiablity
        int headVisible = typedArray.getInt(R.styleable.UserItemView_head_img_visible, View.GONE);
        headImg.setVisibility(headVisible);

        int rightVisible = typedArray.getInt(R.styleable.UserItemView_right_img_visible,
                View.VISIBLE);
        rightImg.setVisibility(rightVisible);

        int rightTitleVisible = typedArray.getInt(R.styleable.UserItemView_right_title_visible, View.GONE);
        right_title.setVisibility(rightTitleVisible);

        int bottomLineVisible = typedArray.getInt(R.styleable.UserItemView_bottom_line_visible, View.GONE);
        line.setVisibility(bottomLineVisible);

        //view size
        float titleSize = typedArray.getDimension(R.styleable.UserItemView_title_size, 0);
        if (titleSize >0)  mTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,titleSize);

        float rightTextSize = typedArray.getDimension(R.styleable.UserItemView_right_text_size, 0);
        if (rightTextSize >0)  right_title.setTextSize(TypedValue.COMPLEX_UNIT_PX,titleSize);

        typedArray.recycle();
    }

    public void setTitle(String title) {
        if (mTitle != null) {
            mTitle.setText(title + "");
        }
    }

    /**
     * 设置右侧标题
     *
     * @param rightText
     */
    public void setRight_title(String rightText) {
        right_title.setText(rightText);
    }

}
