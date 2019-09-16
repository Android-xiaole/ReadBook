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

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class ShareDialog extends Dialog implements BaseQuickAdapter.OnItemClickListener {

    private BaseActivity activity;

    public ShareDialog(BaseActivity context) {
        super(context, R.style.comic_Dialog_no_title);
        activity = context;

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
//        list.add(new ShareMenuModel(R.drawable.icon_share_friends, ShareMenuModel.ShareMenuTypeEnum.WECHATMOMENT));
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
//        list.add(new ShareMenuModel(R.drawable.icon_share_friends, ShareMenuModel.ShareMenuTypeEnum.WECHATMOMENT));
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

    private ShareMessageModel shareMessageModel;

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (shareMessageModel == null) {
            shareMessageModel = new ShareMessageModel();
            shareMessageModel.setShareTitle(activity.getString(R.string.comic_share_dialog_title));
            shareMessageModel.setShareContent(activity.getString(R.string.comic_comic_share_dialog_content));
            shareMessageModel.setShareImgUrl(SharedPref.getInstance(activity).getString(Constants.SharedPrefKey.SHARE_IMG_KEY, activity.getString(R.string.comic_share_dialog_default_img)));
            UserInfo loginUser = LoginHelper.getOnLineUser();
            String uid = loginUser == null ? "0" : loginUser.getUid() + "";
            String channel_name = Constants.CHANNEL_ID;
            String signCode = "";
            if (channel_name.contains("-")) {
                String[] code = channel_name.split("-");
                signCode = code[code.length - 1];
            } else {
                signCode = channel_name;
            }
            String sign = SignUtil.sign(Constants.PRODUCT_CODE + signCode);
            shareMessageModel.setShareUrl(String.format(activity.getString(R.string.comic_share_dialog_url), SharedPref.getInstance().getString(Constants.SharedPrefKey.SHARE_HOST_KEY, Constants.SharedPrefKey.SHARE_HOST), uid, channel_name, sign) + "&pid=" + Constants.PRODUCT_CODE + "&noShare=false");
            ActionReporter.reportAction(ActionReporter.Event.APP_SHARE, null, null, null);
        } else {
            ActionReporter.reportAction(ActionReporter.Event.CONTENT_SHARE, null, null, null);
        }
        shareMessageModel.setShareImgRes(R.drawable.icon);//默认展示的分享图片
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
                ShareInfo shareInfo = new ShareInfo();
                shareInfo.setTitle("测试");
                shareInfo.setTitle("liu");
                shareInfo.setContent("fadfadfsadfasda");
                shareInfo.setCover("https://www.baidu.com/s?wd=%E4%BB%8A%E6%97%A5%E6%96%B0%E9%B2%9C%E4%BA%8B&tn=SE_Pclogo_6ysd4c7a&sa=ire_dl_gh_logo&rsv_dl=igh_logo_pc");
                shareInfo.setKeywords("key1");
                shareInfo.setQrcodeImg("https://www.baidu.com/s?wd=%E4%BB%8A%E6%97%A5%E6%96%B0%E9%B2%9C%E4%BA%8B&tn=SE_Pclogo_6ysd4c7a&sa=ire_dl_gh_logo&rsv_dl=igh_logo_pc");
                SharePicture sharePicture = new SharePicture(activity);
                sharePicture.setData(shareInfo);
                sharePicture.setListener(new SharePicture.Listener() {
                    @Override
                    public void onSuccess(String path) {

                    }

                    @Override
                    public void onFail() {
                        Log.i("share_picture", "error");
                    }
                });
//                ToastUtil.showToastShort("未实现");
                sharePicture.startDraw();
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
        this.shareMessageModel = shareMessageModel;
        super.show();
    }
}
