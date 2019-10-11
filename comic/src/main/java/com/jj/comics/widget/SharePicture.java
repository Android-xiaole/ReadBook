package com.jj.comics.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
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
public class SharePicture extends LinearLayout {
    private final String TAG = "SharePicture";
    private Context mContext;
    private View rootView;
    private ShareInfo shareInfo;
    //布局
    private LinearLayout top_linear;
    private LinearLayout ll_bottom;
    private LinearLayout ll_author_type;
    private LinearLayout ll_right;
    private LinearLayout llContent;
    private ImageView iv_bookIcon;//封面
    private TextView tv_title;//标题
    private TextView tv_author;//作者
    private TextView tv_type;//分类
    private TextView article_content;//内容
    private ImageView qrcode_img; // 二维码
    private TextView keyword1; // 关键词1
    private TextView keyword2; // 关键词2

    // 长图的宽度，默认为屏幕宽度
    private int picWidth;
    // 最终压缩后的长图宽度
    private int finalCompressLongPictureWidth;
    // 长图两边的间距
    private int picMargin;

    // 被认定为长图的长宽比
    private int maxSingleImageRatio = 3;
    private int widthTop = 0;
    private int heightTop = 0;

    private int widthContent = 0;
    private int heightContent = 0;

    private int widthBottom = 0;
    private int heightBottom = 0;

    private Listener listener;

    public void removeListener() {
        this.listener = null;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public SharePicture(Context context) {
        super(context);
        init(context);
    }

    public SharePicture(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SharePicture(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        picWidth = ScreenUtils.getDisplayMetrics().widthPixels;
        picMargin = 40;
        rootView = LayoutInflater.from(context).inflate(R.layout.comic_share_view, this, false);
        initView();
    }

    //初始化布局
    private void initView() {
        top_linear = rootView.findViewById(R.id.top_linear);
        ll_bottom = rootView.findViewById(R.id.ll_bottom);
        ll_author_type = rootView.findViewById(R.id.ll_author_type);
        ll_right = rootView.findViewById(R.id.ll_right);
        llContent = rootView.findViewById(R.id.llContent);
        iv_bookIcon = rootView.findViewById(R.id.iv_bookIcon);
        tv_title = rootView.findViewById(R.id.tv_title);
        tv_author = rootView.findViewById(R.id.tv_author);
        tv_type = rootView.findViewById(R.id.tv_type);
        keyword1 = rootView.findViewById(R.id.keyword1);
        keyword2 = rootView.findViewById(R.id.keyword2);
        article_content = rootView.findViewById(R.id.article_content);
        qrcode_img = rootView.findViewById(R.id.qrcode_img);

        layoutView(top_linear);
        layoutView(ll_bottom);
        layoutView(llContent);

        widthTop = top_linear.getMeasuredWidth();
        heightTop = top_linear.getMeasuredHeight();


        widthContent = llContent.getMeasuredWidth();
        // 文字由于高度可变，所以这里不需要测量高度，后面会手动测量

        widthBottom = ll_bottom.getMeasuredWidth();
        heightBottom = ll_bottom.getMeasuredHeight();
        Log.d(TAG, "drawLongPicture layout top view = " + widthTop + " × " + heightTop);
    }

    /**
     * 手动测量view宽高
     */
    private void layoutView(View v) {
        int width = ScreenUtils.getDisplayMetrics().widthPixels;
        int height = ScreenUtils.getDisplayMetrics().heightPixels;

        v.layout(0, 0, width, height);
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        v.measure(measuredWidth, measuredHeight);
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
    }

    public void setData(ShareInfo info) {
        this.shareInfo = info;
        if (shareInfo.getTitle() != null) {
            tv_title.setText(shareInfo.getTitle());
        }

        if (shareInfo.getAuthor() != null) {
            tv_author.setText(shareInfo.getAuthor());
        }
        if (shareInfo.getType() != null) {
            tv_type.setText(shareInfo.getType());
        }
        if (info.getKeywords() != null && !"".equals(info.getKeywords())) {
            String[] keys = info.getKeywords().split(",");
            if (keys.length == 1) {
                keyword1.setVisibility(View.VISIBLE);
                keyword1.setText(keys[0]);
            } else {
                keyword1.setVisibility(View.VISIBLE);
                keyword2.setVisibility(View.VISIBLE);
                keyword1.setText(keys[0]);
                keyword2.setText(keys[1]);
            }
        }
        GlideApp.with(mContext).load(info.getCover()).into(iv_bookIcon);
        UserInfo userInfo = LoginHelper.getOnLineUser();
        if (userInfo.getAvatar() == null) {
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher);
            Bitmap qrcode_bitmap = QRCodeUtil.createQRCodeBitmap(info.getQrcodeImg(), ScreenUtils.dpToPx(128), ScreenUtils.dpToPx(128), "UTF-8",
                    "H", "1", Color.BLACK, Color.WHITE, bitmap, 0.2F, null);
            qrcode_img.setImageBitmap(qrcode_bitmap);
        } else {
            GlideApp.with(mContext).asBitmap().load(userInfo.getAvatar()).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    Bitmap qrcode_bitmap = QRCodeUtil.createQRCodeBitmap(info.getQrcodeImg(), ScreenUtils.dpToPx(128), ScreenUtils.dpToPx(128), "UTF-8",
                            "H", "1", Color.BLACK, Color.WHITE, resource, 0.2F, null);
                    qrcode_img.setImageBitmap(qrcode_bitmap);
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
        // 先绘制中间部分的文字，计算出文字所需的高度
        String content = shareInfo.getContent();
        if (content.length() > 200) {
            content = content.substring(0, 200) + "...";
        }
        TextPaint contentPaint = article_content.getPaint();
        StaticLayout staticLayout =
                new StaticLayout(content, contentPaint, (ScreenUtils.getDisplayMetrics().widthPixels - picMargin * 2),
                        Layout.Alignment.ALIGN_NORMAL, 1.2F, 0, false);
        heightContent = staticLayout.getHeight();

        // 计算出最终生成的长图的高度 = 上、中、图片总高度、下等个个部分加起来
        int allBitmapHeight = heightTop + heightContent + heightBottom;


        // 创建空白画布
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        Bitmap bitmapAll;
        try {
            bitmapAll = Bitmap.createBitmap(picWidth, allBitmapHeight, config);
        } catch (Exception e) {
            e.printStackTrace();
            config = Bitmap.Config.RGB_565;
            bitmapAll = Bitmap.createBitmap(picWidth, allBitmapHeight, config);
        }
        Canvas canvas = new Canvas(bitmapAll);
//        canvas.drawColor(Color.parseColor("#F5F5F5"));
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);

        Bitmap bgBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.share_bg);
        canvas.drawBitmap(bgBitmap, 0, 0, paint);
        // 绘制top view
        canvas.drawBitmap(getLinearLayoutBitmap(top_linear, widthTop, heightTop), 0, 0, paint);
        canvas.save();

        // 绘制content view
        canvas.translate(ScreenUtils.dpToPx(20), heightTop);
        staticLayout.draw(canvas);

        // 绘制图片view
        canvas.restore();


        // 绘制bottom view
        canvas.drawBitmap(getLinearLayoutBitmap(ll_bottom, widthBottom, heightBottom), 0,
                (heightTop + heightContent + ScreenUtils.dpToPx(16)), paint);

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
                        .setQuality(60)    // 默认压缩质量为80
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
                        .setQuality(60)    // 默认压缩质量为80
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
