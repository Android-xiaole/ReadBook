package com.jj.comics.ui.dialog;

import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.BoringLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jj.base.imageloader.ILFactory;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.SharedPref;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.R;
import com.jj.comics.adapter.ShareMenuAdapter;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.model.ShareInfo;
import com.jj.comics.data.model.ShareMenuModel;
import com.jj.comics.data.model.ShareMessageModel;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.ShareHelper;
import com.jj.comics.util.SignUtil;
import com.jj.comics.util.reporter.ActionReporter;
import com.jj.comics.widget.SharePicture;
import com.jj.comics.widget.bookreadview.utils.ScreenUtils;
import com.jj.sdk.GlideApp;
import com.youth.banner.loader.ImageLoader;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import androidx.constraintlayout.solver.Metrics;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class ShareImageDialog extends Dialog implements BaseQuickAdapter.OnItemClickListener {

    private BaseActivity activity;
    private String mPath;

    public ShareImageDialog(BaseActivity context, String path, ShareInfo info) {
        super(context, R.style.comic_Dialog_no_title);
        activity = context;
        mPath = path;
        Window window = getWindow();
        if (window == null) {
            ToastUtil.showToastShort(context.getString(R.string.comic_window_null));
            return;
        }
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        View contentView = View.inflate(context, R.layout.comic_share_image_dialog, null);
        setContentView(contentView);
        ImageView shareImg = contentView.findViewById(R.id.share_image);
        ImageView bookIcon = contentView.findViewById(R.id.iv_bookIcon);
        LinearLayout bookll = contentView.findViewById(R.id.book_ll);
        TextView title = contentView.findViewById(R.id.tv_title);
        TextView tv_author = contentView.findViewById(R.id.tv_author);
        TextView tv_type = contentView.findViewById(R.id.tv_type);
        TextView book_content = contentView.findViewById(R.id.book_content);
        TextView keyword1 = contentView.findViewById(R.id.keyword1);
        TextView keyword2 = contentView.findViewById(R.id.keyword2);
        if (info == null) {
            shareImg.setVisibility(View.VISIBLE);
            bookll.setVisibility(View.GONE);
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            Matrix matrix = new Matrix();
            float size = 0.9f;
            matrix.setScale(size, size);
            shareImg.setImageMatrix(matrix);
            shareImg.setImageBitmap(bitmap);
        } else {
            bookll.setVisibility(View.VISIBLE);
            shareImg.setVisibility(View.GONE);
            ILFactory.getLoader().loadNet(bookIcon, info.getCover(),
                    new RequestOptions().error(R.drawable.img_loading).placeholder(R.drawable.img_loading));
            if (info.getTitle() != null) {
                title.setText(info.getTitle());
            }

            if (info.getAuthor() != null) {
                tv_author.setText(info.getAuthor());
            }
            if (info.getType() != null) {
                tv_type.setText(info.getType());
            }

            if (info.getContent() != null) {
                book_content.setText(info.getContent());
            }
            if (info.getKeywords() != null) {
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
        }

        RecyclerView rv_shareMenu = contentView.findViewById(R.id.rv_shareMenu);
        ShareMenuAdapter shareMenuAdapter = new ShareMenuAdapter(R.layout.comic_share_dialog_item);
        List<ShareMenuModel> list = new ArrayList<>();
        list.add(new ShareMenuModel(R.drawable.icon_share_wechat, ShareMenuModel.ShareMenuTypeEnum.WECHAT));
        list.add(new ShareMenuModel(R.drawable.icon_share_friends, ShareMenuModel.ShareMenuTypeEnum.WECHATMOMENT));
        list.add(new ShareMenuModel(R.drawable.icon_share_qq, ShareMenuModel.ShareMenuTypeEnum.QQ));
        list.add(new ShareMenuModel(R.drawable.icon_share_weibo, ShareMenuModel.ShareMenuTypeEnum.SINA));
        shareMenuAdapter.setNewData(list);
        rv_shareMenu.setAdapter(shareMenuAdapter);
        rv_shareMenu.setLayoutManager(new GridLayoutManager(context, 4));
        shareMenuAdapter.setOnItemClickListener(this);

        contentView.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private ShareMessageModel shareMessageModel;

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (shareMessageModel == null) {
            shareMessageModel = new ShareMessageModel();
            ActionReporter.reportAction(ActionReporter.Event.APP_SHARE, null, null, null);
        } else {
            ActionReporter.reportAction(ActionReporter.Event.CONTENT_SHARE, null, null, null);
        }
        shareMessageModel.setShareImgRes(R.drawable.ic_launcher);//默认展示的分享图片
        ShareMenuModel shareMenuModel = (ShareMenuModel) adapter.getData().get(position);
        switch (shareMenuModel.getType()) {
            case WECHAT://分享微信
                ShareHelper.getInstance().shareImageToWechat(activity, mPath);
                break;
            case WECHATMOMENT://分享朋友圈
                ShareHelper.getInstance().shareImageToWechatMoment(activity, mPath);
                break;
            case QQ://分享QQ
                ShareHelper.getInstance().shareImageToQQ(activity, mPath);
                break;
            case QQZONE://分享QQ空间
                ShareHelper.getInstance().shareToQQzone(activity, shareMessageModel);
                break;
            case SINA://分享新浪微博
                ShareHelper.getInstance().shareToSina(activity, shareMessageModel);
                break;
        }
        dismiss();
    }

    /**
     * 复制链接的方法
     *
     * @param url
     */
    public void copyLink(String url) {
        // 从API11开始android推荐使用android.content.ClipboardManager
        // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
        ClipboardManager cm = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(url);
        ToastUtil.showToastShort(activity.getString(R.string.comic_copy_success));
    }

    public void show() {
        super.show();
    }
}
