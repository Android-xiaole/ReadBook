package com.jj.comics.ui.mine.pay;

import android.content.Intent;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.gyf.barlibrary.ImmersionBar;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.SharedPref;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.R;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.constants.RequestCode;
import com.jj.comics.data.model.BookCatalogModel;
import com.jj.comics.data.model.PayInfo;
import com.jj.comics.data.model.ResponseModel;
import com.jj.comics.ui.dialog.DialogUtilForComic;
import com.jj.comics.ui.dialog.RechargeNotifyDialog;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.eventbus.events.PaySuccessEvent;
import com.jj.comics.util.eventbus.events.RefreshCatalogListBySubscribeEvent;
import com.jj.comics.util.eventbus.events.UpdateAutoBuyStatusEvent;
import com.jj.comics.util.eventbus.events.UpdateUserInfoEvent;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

@Route(path = RouterMap.COMIC_SUBSCRIBE_ACTIVITY)
public class SubscribeActivity extends BaseActivity<SubscribePresenter> implements SubscribeContract.ISubscribeView {

    private BookCatalogModel catalogModel;
    private boolean tag = false;//false为立即阅读
    boolean isSelected;
    private DialogUtilForComic.OnDialogClickWithSelect mDialogClick = new DialogUtilForComic.OnDialogClickWithSelect() {
        @Override
        public void onConfirm() {
            if (tag) {
                MobclickAgent.onEvent(context, Constants.UMEventId.CLICK_SUBSCRIBE_PAY);
                PayActivity.toPay(context, catalogModel.getBook_id());
            } else {
                MobclickAgent.onEvent(context, Constants.UMEventId.CLICK_SUBSCRIBE_CONFIRM);
                getP().subscribeComic(catalogModel.getBook_id(),catalogModel.getId());
            }
        }

        @Override
        public void onRefused() {
            MobclickAgent.onEvent(context, Constants.UMEventId.CLICK_SUBSCRIBE_CLOSE);
            setResult(RESULT_CANCELED);
            finish();
        }

        @Override
        public void onSelected(boolean selested) {
            isSelected = selested;
            SharedPref.getInstance(SubscribeActivity.this).putBoolean(Constants.SharedPrefKey.AUTO_BUY, isSelected);
        }
    };

    public static void toSubscribe(BaseActivity activity, BookCatalogModel catalogModel) {
        ARouter.getInstance().build(RouterMap.COMIC_SUBSCRIBE_ACTIVITY)
                .withSerializable(Constants.IntentKey.BOOK_CATALOG_MODEL, catalogModel)
//                .withString("title", title)
                .navigation(activity, RequestCode.SUBSCRIBE_REQUEST_CODE);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        isSelected = SharedPref.getInstance().getBoolean(Constants.SharedPrefKey.AUTO_BUY, false);
        catalogModel = (BookCatalogModel) getIntent().getSerializableExtra(Constants.IntentKey.BOOK_CATALOG_MODEL);
        getP().getUserPayInfo();
    }

    private RechargeNotifyDialog mPayDialog;

    @Override
    public void showDiaLog(PayInfo payInfo) {
        if (mPayDialog == null) {
            mPayDialog = new RechargeNotifyDialog();
        }
        mPayDialog.show(getSupportFragmentManager(),payInfo,
                catalogModel,isSelected,mDialogClick);
//        if (payInfo.getIs_vip() == 1){
//            int discountPrice = (int) (catalogModel.getSaleprice() * catalogModel.getVip_discount());
//            tag = discountPrice > payInfo.getTotal_egold();
//        }else{
            tag = catalogModel.getSaleprice() > payInfo.getTotal_egold();
//        }
    }

    @Override
    public void onSubscribe(ResponseModel responseModel) {
        SharedPref.getInstance().putBoolean(Constants.SharedPrefKey.AUTO_BUY, isSelected);
        hideProgress();
        /*
         1.通知阅读页面刷新目录列表
         2.通知我的界面自动购买更新
         */
        EventBusManager.sendRefreshCatalogListBySubscribeEvent(new RefreshCatalogListBySubscribeEvent());
        EventBusManager.sendUpdateAutoBuyStatusEvent(new UpdateAutoBuyStatusEvent());
//        setResult(catalogModel);

        Intent intent = new Intent();
        //这里统一传递章节id处理就行了
        intent.putExtra(Constants.IntentKey.ID, this.catalogModel.getId());
//        intent.putExtra(Constants.IntentKey.BOOK_CATALOG_MODEL,catalogModel);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onSubscribeFail(NetError error) {
        SharedPref.getInstance().putBoolean(Constants.SharedPrefKey.AUTO_BUY, isSelected);
        if (error.getType() == NetError.BalanceError || error.getType() == NetError.AuthError) {
            PayActivity.toPay(this, catalogModel.getBook_id());
        } else {
            ToastUtil.showToastShort(getString(R.string.comic_subscribe_fail) + "：" + error.getMessage());
        }
        hideProgress();
    }

    /**
     * 接受充值金币成功后，刷新用户信息的回调
     * 此时需要刷新当前弹窗页面的金币数量以及按钮状态显示
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshCoinAndBtnStatus(PaySuccessEvent event) {
        if (mPayDialog!=null){
            getP().getUserPayInfo();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_activity_subscribe;
    }

    @Override
    public SubscribePresenter setPresenter() {
        return new SubscribePresenter();
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        ImmersionBar.with(this).transparentStatusBar().fitsSystemWindows(false).init();
    }

    @Override
    public boolean useEventBus() {
        return true;
    }
}
