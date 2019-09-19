package com.jj.comics.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.jj.base.BaseApplication;
import com.jj.base.imageloader.ILFactory;
import com.jj.comics.R;
import com.jj.comics.data.model.ShareInfo;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.util.ImageUtil;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.QRCodeUtil;
import com.jj.comics.widget.bookreadview.utils.ScreenUtils;
import com.jj.sdk.GlideApp;
import com.nanchen.compresshelper.CompressHelper;

import java.io.File;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 分享图片页面
 */
public class ShareUserPicture extends LinearLayout {
    private final String TAG = "SharePicture";
    private Context mContext;
    private View rootView;
    private ShareInfo shareInfo;
    //布局
    private LinearLayout top_linear;
    private TextView nickName;//用户名
    private ImageView qrcodeImg; // 二维码
    private ImageView head_img; // 用户头像

    // 长图的宽度，默认为屏幕宽度
    private int picWidth;
    private int picHeight;
    // 最终压缩后的长图宽度
    private int finalCompressLongPictureWidth;
    // 长图两边的间距
    private int picMargin;

    // 被认定为长图的长宽比
    private int maxSingleImageRatio = 3;
    private int widthTop = 0;
    private int heightTop = 0;

    private Listener listener;

    public void removeListener() {
        this.listener = null;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public ShareUserPicture(Context context) {
        super(context);
        init(context);
    }

    public ShareUserPicture(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ShareUserPicture(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        picWidth = ScreenUtils.getDisplayMetrics().widthPixels;
        picHeight = ScreenUtils.getDisplayMetrics().heightPixels;
        picMargin = 40;
        rootView = LayoutInflater.from(context).inflate(R.layout.comic_share_user_view, this, false);
        initView();
    }

    //初始化布局
    private void initView() {
        top_linear = rootView.findViewById(R.id.top_linear);
        nickName = rootView.findViewById(R.id.nickname);
        qrcodeImg = rootView.findViewById(R.id.qrcode_img);
        head_img = rootView.findViewById(R.id.head_img);
        layoutView(top_linear);
        widthTop = top_linear.getMeasuredWidth();
        heightTop = top_linear.getMeasuredHeight();
    }

    /**
     * 手动测量view宽高
     */
    private void layoutView(View v) {
        int width = ScreenUtils.getDisplayMetrics().widthPixels;
        int height = ScreenUtils.getDisplayMetrics().heightPixels;

        v.layout(0, 0, width, height);
        int measuredWidth = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        int measuredHeight = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        v.measure(measuredWidth, measuredHeight);
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
    }

    public void setData(ShareInfo info) {
        this.shareInfo = info;
        if (shareInfo.getTitle() != null) {
            String title = shareInfo.getTitle();
            if (title.length() > 5) {
                title = title.substring(0, 5) + "...";
            }
            //设置昵称
            String nickname = "您的好友 " + title + " 邀请";
            SpannableString reminder = new SpannableString(nickname);
            reminder.setSpan(new ForegroundColorSpan(BaseApplication.getApplication().getResources().getColor(R.color.comic_ff8124)), 5, 5 + title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            nickName.setText(reminder);
        }

        ILFactory.getLoader().loadNet(head_img, info.getCover(),
                new RequestOptions().transforms(new CenterCrop(), new CircleCrop()).error(R.drawable.img_loading)
                        .placeholder(R.drawable.img_loading));

        UserInfo userInfo = LoginHelper.getOnLineUser();
        if (userInfo.getAvatar() == null) {
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon);
            Bitmap qrcode_bitmap = QRCodeUtil.createQRCodeBitmap(info.getQrcodeImg(), ScreenUtils.dpToPx(128), ScreenUtils.dpToPx(128), "UTF-8",
                    "H", "1", Color.BLACK, Color.WHITE, bitmap, 0.2F, null);
            qrcodeImg.setImageBitmap(qrcode_bitmap);
        } else {
            GlideApp.with(mContext).asBitmap().load(userInfo.getAvatar()).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    Bitmap qrcode_bitmap = QRCodeUtil.createQRCodeBitmap(info.getQrcodeImg(), ScreenUtils.dpToPx(128), ScreenUtils.dpToPx(128), "UTF-8",
                            "H", "1", Color.BLACK, Color.WHITE, resource, 0.2F, null);
                    qrcodeImg.setImageBitmap(qrcode_bitmap);
                }
            });
        }

    }


    public void startDraw() {
        // 需要先下载全部需要用到的图片（用户头像、图片等），下载完成后再进行长图的绘制操作
        downloadAllImage();
    }

    private void downloadAllImage() {
        //下载封面图生成
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                draw();
            }
        }).start();
    }

    private Bitmap getLinearLayoutBitmap(LinearLayout linearLayout, int w, int h) {
        Bitmap originBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(originBitmap);
        linearLayout.draw(canvas);
        return ImageUtil.resizeImage(originBitmap, picWidth, h);
    }

    private void draw() {

        // 创建空白画布
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        Bitmap bitmapAll;
        try {
            bitmapAll = Bitmap.createBitmap(picWidth, heightTop, config);
        } catch (Exception e) {
            e.printStackTrace();
            config = Bitmap.Config.RGB_565;
            bitmapAll = Bitmap.createBitmap(picWidth, heightTop, config);
        }
        Canvas canvas = new Canvas(bitmapAll);
        canvas.drawColor(Color.WHITE);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        // 绘制view
        canvas.drawBitmap(getLinearLayoutBitmap(top_linear, picWidth, heightTop), 0, 0, paint);
        canvas.save();

        // 绘制图片view
        canvas.restore();

        // 生成最终的文件，并压缩大小，这里使用的是：implementation 'com.github.nanchen2251:CompressHelper:1.0.5'
        try {
            String path = ImageUtil.saveBitmapBackPath(bitmapAll);
            float imageRatio = ImageUtil.getImageRatio(path);
            if (imageRatio >= 10) {
                finalCompressLongPictureWidth = 750;
            } else if (imageRatio >= 5 && imageRatio < 10) {
                finalCompressLongPictureWidth = 900;
            } else {
                finalCompressLongPictureWidth = picWidth;
            }
            String result;
            // 由于长图一般比较大，所以压缩时应注意OOM的问题，这里并不处理OOM问题，请自行解决。
            try {
                result = new CompressHelper.Builder(mContext).setMaxWidth(finalCompressLongPictureWidth)
                        .setMaxHeight(Integer.MAX_VALUE) // 默认最大高度为960
                        .setQuality(80)    // 默认压缩质量为80
                        .setFileName("长图_" + System.currentTimeMillis()) // 设置你需要修改的文件名
                        .setCompressFormat(Bitmap.CompressFormat.JPEG) // 设置默认压缩为jpg格式
                        .setDestinationDirectoryPath(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                                        + "/长图分享/")
                        .build()
                        .compressToFile(new File(path))
                        .getAbsolutePath();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();

                finalCompressLongPictureWidth = finalCompressLongPictureWidth / 2;
                result = new CompressHelper.Builder(mContext).setMaxWidth(finalCompressLongPictureWidth)
                        .setMaxHeight(Integer.MAX_VALUE) // 默认最大高度为960
                        .setQuality(50)    // 默认压缩质量为80
                        .setFileName("长图_" + System.currentTimeMillis()) // 设置你需要修改的文件名
                        .setCompressFormat(Bitmap.CompressFormat.JPEG) // 设置默认压缩为jpg格式
                        .setDestinationDirectoryPath(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                                        + "/长图分享/")
                        .build()
                        .compressToFile(new File(path))
                        .getAbsolutePath();
            }
            Log.d(TAG, "最终生成的长图路径为：" + result);
            if (listener != null) {
                listener.onSuccess(result);
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onFail();
            }
        }
    }

    public interface Listener {

        /**
         * 生成长图成功的回调
         *
         * @param path 长图路径
         */
        void onSuccess(String path);

        /**
         * 生成长图失败的回调
         */
        void onFail();
    }
}
