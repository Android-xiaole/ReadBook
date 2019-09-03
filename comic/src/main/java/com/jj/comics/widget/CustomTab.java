package com.jj.comics.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jj.comics.R;

public class CustomTab extends RelativeLayout {
    private TextView mText;

    public CustomTab setText(String text) {
        mText.setText(text);
        return this;
    }

    @Override
    public void setSelected(boolean selected) {
        mText.setSelected(selected);
        super.setSelected(selected);
    }

    public CustomTab(Context context) {
        super(context);
        init(context);
    }

    public CustomTab(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomTab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.comic_tab_layout, this, true);
        mText = ((TextView) findViewById(R.id.comic_tab_text));
    }

}
