package com.jj.comics.ui.dialog;

import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jj.base.dialog.GenerateImgProgressDialog;
import com.jj.base.ui.BaseActivity;
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
import com.jj.comics.util.reporter.ActionReporter;
import com.jj.comics.widget.SharePicture;
import com.jj.comics.widget.ShareUserPicture;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class ShareDialog extends Dialog implements BaseQuickAdapter.OnItemClickListener {

    private BaseActivity activity;

    private Dialog dialog;
    private boolean isShareUser = true;
    public ShareDialog(BaseActivity context) {
        super(context, R.style.comic_Dialog_no_title);
        activity = context;
        isShareUser = true;
        Window window = getWindow();
        if (window == null) {
            ToastUtil.showToastShort(context.getString(R.string.comic_window_null));
            return;
        }
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        View contentView = View.inflate(context, R.layout.comic_share_dialog, null);
        setContentView(contentView);

        RecyclerView rv_shareMenu = contentView.findViewById(R.id.rv_shareMenu);
        ShareMenuAdapter shareMenuAdapter = new ShareMenuAdapter(R.layout.comic_share_dialog_item);
        List<ShareMenuModel> list = new ArrayList<>();
        list.add(new ShareMenuModel(R.drawable.icon_share_wechat, ShareMenuModel.ShareMenuTypeEnum.WECHAT));
        list.add(new ShareMenuModel(R.drawable.icon_share_friends, ShareMenuModel.ShareMenuTypeEnum.WECHATMOMENT));
        list.add(new ShareMenuModel(R.drawable.icon_share_qq, ShareMenuModel.ShareMenuTypeEnum.QQ));
//        list.add(new ShareMenuModel(R.drawable.icon_share_qq_space, ShareMenuModel.ShareMenuTypeEnum.QQZONE));
        list.add(new ShareMenuModel(R.drawable.icon_share_weibo, ShareMenuModel.ShareMenuTypeEnum.SINA));
//        list.add(new ShareMenuModel(R.drawable.icon_share_copy_link, ShareMenuModel.ShareMenuTypeEnum.COPYLINK));
//        list.add(new ShareMenuModel(R.drawable.icon_share_report, ShareMenuModel.ShareMenuTypeEnum.REPORT));
        list.add(new ShareMenuModel(R.drawable.icon_share_photo, ShareMenuModel.ShareMenuTypeEnum.PHOTO));
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

    public ShareDialog(BaseActivity context, OnDismissListener onDismissListener) {
        super(context, R.style.comic_Dialog_no_title);
        activity = context;
        setOnDismissListener(onDismissListener);

        Window window = getWindow();
        if (window == null) {
            ToastUtil.showToastShort(context.getString(R.string.comic_window_null));
            return;
        }
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        View contentView = View.inflate(context, R.layout.comic_share_dialog, null);
        setContentView(contentView);

        RecyclerView rv_shareMenu = contentView.findViewById(R.id.rv_shareMenu);
        ShareMenuAdapter shareMenuAdapter = new ShareMenuAdapter(R.layout.comic_share_dialog_item);
        List<ShareMenuModel> list = new ArrayList<>();
        list.add(new ShareMenuModel(R.drawable.icon_share_wechat, ShareMenuModel.ShareMenuTypeEnum.WECHAT));
        list.add(new ShareMenuModel(R.drawable.icon_share_friends, ShareMenuModel.ShareMenuTypeEnum.WECHATMOMENT));
//        list.add(new ShareMenuModel(R.drawable.icon_share_qq_space, ShareMenuModel.ShareMenuTypeEnum.QQZONE));
        list.add(new ShareMenuModel(R.drawable.icon_share_qq, ShareMenuModel.ShareMenuTypeEnum.QQ));
        list.add(new ShareMenuModel(R.drawable.icon_share_weibo, ShareMenuModel.ShareMenuTypeEnum.SINA));
//        list.add(new ShareMenuModel(R.drawable.icon_share_copy_link, ShareMenuModel.ShareMenuTypeEnum.COPYLINK));
//        list.add(new ShareMenuModel(R.drawable.icon_share_report, ShareMenuModel.ShareMenuTypeEnum.REPORT));
        list.add(new ShareMenuModel(R.drawable.icon_share_photo, ShareMenuModel.ShareMenuTypeEnum.PHOTO));
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
            isShareUser = true;
            shareMessageModel = new ShareMessageModel();
            shareMessageModel.setShareTitle("");
            shareMessageModel.setShareContent(activity.getString(R.string.comic_comic_share_dialog_content));
            UserInfo loginUser = LoginHelper.getOnLineUser();
            String uid = loginUser == null ? "0" : loginUser.getUid() + "";
            shareMessageModel.setShareImgUrl(loginUser.getAvatar());
            String shareUrl = Constants.OPEN_INSTALL_URL + "uid=" + uid + "&cid=" + Constants.CHANNEL_ID + "&pid=" + Constants.PRODUCT_CODE + "&invite_code=" + loginUser.getInvite_code() + "&name=" + loginUser.getNickname() + "&pic=" + loginUser.getAvatar();
            shareMessageModel.setShareUrl(shareUrl);
            shareMessageModel.setBookTitle(loginUser.getNickname());
            ActionReporter.reportAction(ActionReporter.Event.APP_SHARE, null, null, null);
        } else {
            ActionReporter.reportAction(ActionReporter.Event.CONTENT_SHARE, null, null, null);
        }
        shareMessageModel.setShareImgRes(R.drawable.ic_launcher);//默认展示的分享图片
        ShareMenuModel shareMenuModel = (ShareMenuModel) adapter.getData().get(position);
        switch (shareMenuModel.getType()) {
            case WECHAT://分享微信
                ShareHelper.getInstance().shareToWechat(activity, shareMessageModel);
                break;
            case WECHATMOMENT://分享朋友圈
                ShareHelper.getInstance().shareToWechatMoment(activity, shareMessageModel);
                break;
            case QQ://分享QQ
                ShareHelper.getInstance().shareToQQ(activity, shareMessageModel);
                break;
            case QQZONE://分享QQ空间
                ShareHelper.getInstance().shareToQQzone(activity, shareMessageModel);
                break;
            case SINA://分享新浪微博
                ShareHelper.getInstance().shareToSina(activity, shareMessageModel);
                break;
            case PHOTO://分享图片
                if (dialog == null) dialog = new GenerateImgProgressDialog(activity);
                if (!dialog.isShowing()) dialog.show();

                ShareInfo shareInfo = new ShareInfo();
                shareInfo.setTitle(shareMessageModel.getBookTitle());
                shareInfo.setAuthor(shareMessageModel.getAuthor());
                shareInfo.setType(shareMessageModel.getType());
                shareInfo.setKeywords(shareMessageModel.getKeys());
                shareInfo.setContent(shareMessageModel.getShareContent());
                shareInfo.setCover(shareMessageModel.getShareImgUrl());
                shareInfo.setQrcodeImg(shareMessageModel.getShareUrl());

                if (isShareUser) {
                    shareUserImg(shareInfo);
                } else {
                    shareContentImage(shareInfo);
                }
                break;
            case COPYLINK://复制链接
                copyLink(shareMessageModel.getShareUrl());
                break;
            case REPORT://举报
                ToastUtil.showToastShort(activity.getString(R.string.comic_tip_success));
                break;
        }
        dismiss();
    }

    private void shareContentImage(ShareInfo shareInfo) {
        SharePicture sharePicture = new SharePicture(activity);
        sharePicture.setData(shareInfo);
        sharePicture.setListener(new SharePicture.Listener() {
            @Override
            public void onSuccess(String path) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        ShareImageDialog shareImageDialog = new ShareImageDialog(activity, path, shareInfo);
                        shareImageDialog.show();
                    }
                });
            }

            @Override
            public void onFail() {
                Log.i("share_picture", "error");
            }
        });
        sharePicture.startDraw();
    }

    /**
     * 分享用户图片
     *
     * @param shareInfo
     */
    private void shareUserImg(ShareInfo shareInfo) {
        ShareUserPicture shareUserPicture = new ShareUserPicture(activity);
        shareUserPicture.setData(shareInfo);
        shareUserPicture.setListener(new ShareUserPicture.Listener() {
            @Override
            public void onSuccess(String path) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        ShareImageDialog shareImageDialog = new ShareImageDialog(activity, path, null);
                        shareImageDialog.show();
                    }
                });
            }

            @Override
            public void onFail() {
                Log.i("share_picture", "error");
            }
        });
        shareUserPicture.startDraw();
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

    public void show(ShareMessageModel shareMessageModel) {
        isShareUser = false;
        this.shareMessageModel = shareMessageModel;
        super.show();
    }
}
