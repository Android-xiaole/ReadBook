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
import com.jj.base.BaseApplication;
import com.jj.base.dialog.GenerateImgProgressDialog;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.R;
import com.jj.comics.adapter.ShareMenuAdapter;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.constants.UmEventID;
import com.jj.comics.data.model.ShareInfo;
import com.jj.comics.data.model.ShareMenuModel;
import com.jj.comics.data.model.ShareMessageModel;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.ShareHelper;
import com.jj.comics.util.reporter.ActionReporter;
import com.jj.comics.widget.SharePicture;
import com.jj.comics.widget.ShareUserPicture;
import com.umeng.analytics.MobclickAgent;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class ShareDialog extends Dialog implements BaseQuickAdapter.OnItemClickListener {

    private BaseActivity activity;

    private Dialog dialog;
    private boolean isShareUser = true;
    private String from;
    private String title;

    public ShareDialog(BaseActivity context, String from, String content) {
        super(context, R.style.comic_Dialog_no_title);
        activity = context;
        isShareUser = true;
        this.from = from;
        this.title = content;
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

    public ShareDialog(BaseActivity context, String from,
                       String content, OnDismissListener onDismissListener) {
        super(context, R.style.comic_Dialog_no_title);
        activity = context;
        this.from = from;
        this.title = content;
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
            shareMessageModel.setShareTitle("金桔小说");
            shareMessageModel.setShareContent(activity.getString(R.string.comic_comic_share_dialog_content));
            UserInfo loginUser = LoginHelper.getOnLineUser();
            String uid = loginUser == null ? "0" : loginUser.getUid() + "";
            shareMessageModel.setShareImgUrl(loginUser.getAvatar());
            String shareUrl = Constants.OPEN_INSTALL_URL + "uid=" + URLEncoder.encode(uid) + "&cid=" + URLEncoder.encode(Constants.CHANNEL_ID) + "&pid=" + URLEncoder.encode(Constants.PRODUCT_CODE) + "&invite_code=" + URLEncoder.encode(loginUser.getInvite_code()) + "&name=" + URLEncoder.encode(loginUser.getNickname()) + "&pic=" + URLEncoder.encode(loginUser.getAvatar());
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
                umengClick("URL", "WX");
                break;
            case WECHATMOMENT://分享朋友圈
                ShareHelper.getInstance().shareToWechatMoment(activity, shareMessageModel);
                umengClick("URL", "WX-PYQ");
                break;
            case QQ://分享QQ
                ShareHelper.getInstance().shareToQQ(activity, shareMessageModel);
                umengClick("URL", "QQ");
                break;
            case QQZONE://分享QQ空间
                ShareHelper.getInstance().shareToQQzone(activity, shareMessageModel);
                umengClick("URL", "QQ-QZONE");
                break;
            case SINA://分享新浪微博
                ShareHelper.getInstance().shareToSina(activity, shareMessageModel);
                umengClick("URL", "WB");
                break;
            case PHOTO://分享图片
                if (dialog == null) dialog = new GenerateImgProgressDialog(activity);
                if (!dialog.isShowing()) dialog.show();

                ShareInfo shareInfo = new ShareInfo();
                shareInfo.setAuthor(shareMessageModel.getAuthor());
                shareInfo.setType(shareMessageModel.getType());
                shareInfo.setKeywords(shareMessageModel.getKeys());
                shareInfo.setContent(shareMessageModel.getShareContent());
                shareInfo.setCover(shareMessageModel.getShareImgUrl());
                shareInfo.setQrcodeImg(shareMessageModel.getShareUrl());

                if (isShareUser) {
                    shareInfo.setTitle("分享用户");
                    shareUserImg(shareInfo);
                } else {
                    shareInfo.setTitle(shareMessageModel.getBookTitle());
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

    private void umengClick(String type, String way) {
        Map<String, Object> action_share = new HashMap<String, Object>();
        action_share.put("from", "-" + from);
        action_share.put("content", "-" + shareMessageModel.getBookTitle());
        action_share.put("type", "-" + type);
        action_share.put("way", "-" + way);
        MobclickAgent.onEventObject(BaseApplication.getApplication(), UmEventID.ACTION_SHARE, action_share);
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
                        ShareImageDialog shareImageDialog = new ShareImageDialog(activity, path,
                                shareInfo, from, false);
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
                        ShareImageDialog shareImageDialog = new ShareImageDialog(activity, path,
                                shareInfo, from, true);
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
