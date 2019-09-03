package com.jj.comics.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;

import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.FileUtil;
import com.jj.base.utils.PackageUtil;
import com.jj.base.utils.Utils;
import com.jj.comics.R;
import com.jj.comics.common.constants.RequestCode;

import java.io.File;

public class IntentUtils {
    public static void installApk(File file, BaseActivity context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri apkUri = FileProvider.getUriForFile(context, PackageUtil.getPackageInfo().packageName+".fileprovider", file);
            intent.setDataAndType(apkUri, context.getString(R.string.comic_install_apk));
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file), context.getString(R.string.comic_install_apk));
        }
        context.startActivity(intent);
    }

    public static void upDatePic(Activity activity, String fileName) {
//        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        Uri uri = Uri.fromFile(new File(fileName));
//        intent.setData(Utils.getUri());
//        activity.sendBroadcast(intent);
    }

    public static void openPic(Activity activity) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType(activity.getString(R.string.comic_open_image_type));
        activity.startActivityForResult(photoPickerIntent, RequestCode.OPEN_PIC_REQUEST_CODE);
    }

    public static void cropImageUri(Activity activity, Uri data, String savePath) {
        Intent intent = new Intent(activity.getString(R.string.comic_crop_image_intent));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            try {
                data = FileUtil.getFileProvider(activity, Utils.uriToFile(activity, data));
            } catch (IllegalArgumentException e) {
            }
        }
        File file = new File(savePath);
        Uri toUri = Uri.fromFile(file);
        if (data == null || toUri == null) return;
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        intent.setDataAndType(data, activity.getString(R.string.comic_open_image_type));
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 480);
        intent.putExtra("outputY", 480);
        intent.putExtra("scale", true);
        //将剪切的图片保存到目标Uri中
        intent.putExtra(MediaStore.EXTRA_OUTPUT, toUri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        activity.startActivityForResult(intent, RequestCode.CROP_IMG_REQUEST_CODE);
    }
}
