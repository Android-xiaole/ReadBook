package com.jj.comics.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.jj.base.utils.FileUtil;
import com.jj.base.utils.Utils;
import com.jj.comics.R;

@SuppressWarnings("unused")
public class SubsamplingScaleImageView extends com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView {
    private int targetWidth;
    private int targetHeight;

    public SubsamplingScaleImageView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    public SubsamplingScaleImageView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (targetHeight > 0 && targetWidth > 0)
            setMeasuredDimension(targetWidth, targetHeight);
        else
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setTargetWidthAndHeight(int targetWidth, int targetHeight) {
        this.targetHeight = targetHeight;
        this.targetWidth = targetWidth;
    }

    /**
     * bitmap设置全屏
     *
     * @param bitmap          图片
     * @param isUseGlideCache 是否需要Glide缓存图片
     */
    public void setMatchBitmap(Bitmap bitmap, boolean isUseGlideCache) {
        int screenWidth = Utils.getScreenWidth(getContext());
        float minScale = screenWidth * 1.0f / bitmap.getWidth();
        setTargetWidthAndHeight(screenWidth, (int) (minScale * bitmap.getHeight()));
        //不使用SubsamplingScaleImageView来管理图片  使用Glide管理
        if (isUseGlideCache)
            setImage(ImageSource.cachedBitmap(bitmap));
        else
            setImage(ImageSource.bitmap(bitmap));
    }

    /**
     * 得到bitmap的大小
     */
    public int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return bitmap.getByteCount();
        }
        // 在低版本中用一行的字节x高度
        return bitmap.getRowBytes() * bitmap.getHeight();                //earlier version
    }

    /**
     * drawable
     */
    public void setMatchDrawable(Drawable drawable) {
        if (drawable == null) {
        } else if (drawable instanceof BitmapDrawable) {
            setMatchBitmap(((BitmapDrawable) drawable).getBitmap(), true);
        } else {
            // 取 drawable 的长宽
            int w = drawable.getIntrinsicWidth();
            int h = drawable.getIntrinsicHeight();
            // 取 drawable 的颜色格式
            Bitmap.Config config = Bitmap.Config.RGB_565;
            // 建立对应 bitmap
            Bitmap bitmap = Bitmap.createBitmap(w, h, config);
            // 建立对应 bitmap 的画布
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, w, h);
            // 把 drawable 内容画到画布中
            drawable.draw(canvas);
            if (getTag(R.id.tag_url) != null && getTag(R.id.tag_url) instanceof String) {
                Log.e(getClass().getName(), "转换BitmapDrawable失败--->最终生成图片大小为" + FileUtil.getFormatSize(getBitmapSize(bitmap)) + "\nurl：" + getTag(R.id.tag_url));
            }
            setMatchBitmap(bitmap, false);
        }
    }
}
