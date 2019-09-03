package com.jj.comics.util.reporter;

import android.app.Activity;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.jj.base.BaseApplication;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.R;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.biz.task.TaskRepository;
import com.jj.comics.data.model.CommonStatusResponse;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.widget.comic.snackbar.BaseTransientBottomBar;
import com.jj.comics.widget.comic.snackbar.TopSnackBar;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class TaskReporter {

    /**
     * 上报分享任务
     */
    public static void reportShare() {
        UserInfo userInfo = LoginHelper.getOnLineUser();

        if (userInfo == null) {
            return;
        }
        TaskRepository.getInstance().reportShare()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiSubscriber2<CommonStatusResponse>() {
                    @Override
                    public void onNext(CommonStatusResponse response) {
                        if (response.getData() != null) {
                            showSnackBar(Constants.TaskCode.SHARE);
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToastShort(error.getMessage());
                    }
                });
    }

    public static void showSnackBar(String taskCode){
        final Activity topActivity = BaseApplication.getApplication().getTopActivity();
        if (topActivity == null)return;
        //以前使用的是getCurrentFocus()方法，但返回的view可能为空
        View focusView = topActivity.getWindow().getDecorView().getRootView();
        if (focusView == null)return;

        String describeMsg = null;
        switch (taskCode){
            case Constants.TaskCode.COLLECT:
                describeMsg = "收藏漫画任务完成";
                break;
            case Constants.TaskCode.LIKE:
                describeMsg = "点赞漫画任务完成";
                break;
            case Constants.TaskCode.COMMENT:
                describeMsg = "发表一次评论任务完成";
                break;
            case Constants.TaskCode.NEW_COMMENT:
                describeMsg = "第一次评论任务完成";
                break;
            case Constants.TaskCode.GIVE_REWARD:
                describeMsg = "第一次打赏任务完成";
                break;
            case Constants.TaskCode.SHARE:
                describeMsg = "分享漫画任务完成";
                break;
            case Constants.TaskCode.READING:
                describeMsg = "阅读漫画任务完成";
                break;
        }
        TopSnackBar topSnackBarbar = TopSnackBar.make(focusView, describeMsg, BaseTransientBottomBar.LENGTH_LONG)
                .setAction("领取金币", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ARouter.getInstance().build(RouterMap.COMIC_GOLD_CENTER_ACTIVITY).navigation(topActivity);
                    }
                });
        topSnackBarbar.setActionTextColor(topActivity.getResources().getColor(R.color.comic_ff3333));
        topSnackBarbar.getView().setBackgroundColor(topActivity.getResources().getColor(R.color.comic_yellow_ffd850));
        topSnackBarbar.show();
    }
}
