package com.jj.base.imageloader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;

import java.io.File;

/**
 * Created by wanglei on 2016/11/27.
 */

public interface ILoader {

    void init(ImageProvider imageProvider);

    void loadNet(ImageView target, String url, RequestOptions options);

    void loadNet(Context context, String url, RequestOptions options, SimpleTarget target);

    void loadResource(ImageView target, int resId, RequestOptions options);

    void loadAssets(ImageView target, String assetName, RequestOptions options);

    void loadFile(ImageView target, File file, RequestOptions options);

    void loadUri(ImageView target, Uri uri, RequestOptions options);

    void loadDrawable(Context context, String url, RequestOptions options, Target<Drawable> target);

    void loadFile(Context context, String url, RequestOptions options, Target<File> target);

    File down(Context context, String url, RequestOptions options);

    void clearMemoryCache(Context context);

    void clearDiskCache(Context context);

    void trimMemory(Context context, int level);

    void clear(Context context, View view);

    void resume(Context context);

    void pause(Context context);

}
