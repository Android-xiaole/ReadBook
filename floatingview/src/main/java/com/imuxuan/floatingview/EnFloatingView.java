package com.imuxuan.floatingview;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.request.RequestOptions;
import com.jj.sdk.GlideApp;

/**
 * @ClassName EnFloatingView
 * @Description 悬浮窗
 * @Author Yunpeng Li
 * @Creation 2018/3/15 下午5:04
 * @Mender Yunpeng Li
 * @Modification 2018/3/15 下午5:04
 */
public class EnFloatingView extends FloatingMagnetView {

    private final ImageView mIcon;
    private final TextView mDesc;

    public EnFloatingView(@NonNull Context context,String msg,String url) {
        super(context, null);
        inflate(context, R.layout.en_floating_view, this);
        mIcon = findViewById(R.id.icon);
        mDesc = findViewById(R.id.desc);
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        stringBuilder.append("提现");
        stringBuilder.append(msg);
        stringBuilder.append("元现金");

        stringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#ff0000")
        ),2,stringBuilder.length() - 2, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        stringBuilder.setSpan(new RelativeSizeSpan(1.0f),2,
                stringBuilder.length() - 2,Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        updateFloatView(url,stringBuilder);
    }

    public void setIconImage(String imagePath){
        GlideApp.with(mIcon)
                .applyDefaultRequestOptions(new RequestOptions().centerCrop().circleCrop())
                .load(imagePath)
                .error(R.drawable.ic_launcher)
                .placeholder(R.drawable.ic_launcher)
                .into(mIcon);
    }

    public void setIconImage(int imagePath){
    }

    public void setText(SpannableStringBuilder stringBuilder) {
        mDesc.setText(stringBuilder);
    }

    public void setText(String string) {
        mDesc.setText(string);
    }

    public void updateFloatView(String imagePath,SpannableStringBuilder stringBuilder) {
        setIconImage(imagePath);
        setText(stringBuilder);
    }

}
