package com.jj.comics.ui.detail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.constants.RequestCode;
import com.jj.comics.data.model.BookCatalogModel;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.ui.dialog.DialogUtilForComic;
import com.jj.comics.ui.dialog.LoginNotifyDialog;
import com.jj.comics.ui.read.ReadComicActivity;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.eventbus.events.RefreshDetailActivityDataEvent;

import net.frakbot.jumpingbeans.JumpingBeans;

import androidx.annotation.Nullable;
import butterknife.BindView;

/**
 * 详情页跳转到阅读页的loading页面
 */
@Route(path = RouterMap.COMIC_LOADING_ACTIVITY)
public class LoadingActivity extends BaseActivity<LoadingPresenter> implements LoadingContract.View {

    @BindView(R2.id.tv_loading)
    TextView tv_loading;

    private BookModel bookModel;
    private long chapterid;

    /**
     * 跳转到loading界面
     */
    public static void toLoading(Activity context, BookModel bookModel, long chapterid) {
        ARouter.getInstance().build(RouterMap.COMIC_LOADING_ACTIVITY)
                .withSerializable(Constants.IntentKey.BOOK_MODEL, bookModel)
                .withLong(Constants.IntentKey.BOOK_CHAPTER_ID, chapterid)
                .navigation(context);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        JumpingBeans.with(tv_loading)
                .makeTextJump(tv_loading.getText().length() - 6, tv_loading.getText().length())
                .setLoopDuration(1000)
                .build();
        bookModel = (BookModel) getIntent().getSerializableExtra(Constants.IntentKey.BOOK_MODEL);
        chapterid = getIntent().getLongExtra(Constants.IntentKey.BOOK_CHAPTER_ID, 0);
        if (bookModel != null) {
            getP().toRead(bookModel, chapterid);
        } else {
            ToastUtil.showToastShort("数据异常");
            finish();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.comic_activity_loading;
    }

    @Override
    public LoadingPresenter setPresenter() {
        return new LoadingPresenter();
    }

    @Override
    public void loadSuccess(BookCatalogModel catalogModel) {
        ReadComicActivity.toRead(this, bookModel, catalogModel);
        finish();
    }

    @Override
    public void loadFail(NetError error) {
        if (error.getType() == NetError.AuthError){
            //用户未登录，弹出登录弹窗
            createLoginDialog();
        }else{
            ToastUtil.showToastShort(error.getMessage());
            finish();
        }
    }

    private LoginNotifyDialog loginNotifyDialog;
    public void createLoginDialog() {
        if (loginNotifyDialog == null) loginNotifyDialog = new LoginNotifyDialog();
        loginNotifyDialog.show(getSupportFragmentManager(), new DialogUtilForComic.OnDialogClick() {
            @Override
            public void onConfirm() {
                loginNotifyDialog.dismiss();
                ARouter.getInstance().build(RouterMap.COMIC_LOGIN_ACTIVITY)
                        .navigation(LoadingActivity.this, RequestCode.LOGIN_REQUEST_CODE);
            }

            @Override
            public void onRefused() {
                loginNotifyDialog.dismiss();
                finish();
            }


        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (bookModel == null) {
                /*
                这里必须得判空，因为如果当app长时间置于后台，该页面会被回收，当重新启动的时候
                由于model是接口获取的，会有一定延时，但此时onActivityResult会执行，拿到的model却是null,
                因此保险起见必须做非空判断
                 */
                finish();
                return;
            }
            switch (requestCode) {
                case RequestCode.LOGIN_REQUEST_CODE:
                    if (bookModel != null) {
                        //这里需要发送一条通知高度详情页面刷新详情和收藏状态接口
                        EventBusManager.sendRefreshDetailActivityData(new RefreshDetailActivityDataEvent());
                        getP().toRead(bookModel, chapterid);
                    } else {
                        ToastUtil.showToastShort("数据异常");
                        finish();
                    }
                    break;
                case RequestCode.SUBSCRIBE_REQUEST_CODE:
                    if (bookModel != null) {
                        getP().toRead(bookModel, chapterid);
                    } else {
                        ToastUtil.showToastShort("数据异常");
                        finish();
                    }
                    break;
            }
        }else if (resultCode == RESULT_CANCELED) {
            if (requestCode == RequestCode.SUBSCRIBE_REQUEST_CODE||requestCode == RequestCode.LOGIN_REQUEST_CODE) {
                finish();
            }
        }
    }
}
