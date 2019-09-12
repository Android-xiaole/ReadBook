package com.jj.comics.ui.mine.pay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.gyf.barlibrary.ImmersionBar;
import com.jj.base.dialog.CustomFragmentDialog;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.SharedPref;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.R;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.constants.RequestCode;
import com.jj.comics.data.model.BookCatalogModel;
import com.jj.comics.data.model.BookModel;
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

    private CustomFragmentDialog buyDialog;//购买弹窗
    private BookCatalogModel catalogModel;
    private BookModel bookModel;

    public static void toSubscribe(BaseActivity activity, BookModel bookModel,BookCatalogModel catalogModel) {
        ARouter.getInstance().build(RouterMap.COMIC_SUBSCRIBE_ACTIVITY)
                .withSerializable(Constants.IntentKey.BOOK_MODEL,bookModel)
                .withSerializable(Constants.IntentKey.BOOK_CATALOG_MODEL, catalogModel)
                .navigation(activity, RequestCode.SUBSCRIBE_REQUEST_CODE);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        bookModel = (BookModel) getIntent().getSerializableExtra(Constants.IntentKey.BOOK_MODEL);
        catalogModel = (BookCatalogModel) getIntent().getSerializableExtra(Constants.IntentKey.BOOK_CATALOG_MODEL);
        getP().getUserPayInfo();
    }

    @Override
    public void showDiaLog(PayInfo payInfo) {
        if (bookModel == null||catalogModel == null){
            finish();
            return;
        }
        if (buyDialog == null) {
            buyDialog = new CustomFragmentDialog();
        }
        buyDialog.show(this, getSupportFragmentManager(), R.layout.comic_detail_pay_dialog);
        TextView tv_buyCoinNum = buyDialog.getDialog().findViewById(R.id.tv_buyCoinNum);
        TextView tv_myCoinNum = buyDialog.getDialog().findViewById(R.id.tv_myCoinNum);
        Button btn_pay = buyDialog.getDialog().findViewById(R.id.btn_pay);
        ImageView iv_buyTitle = buyDialog.getDialog().findViewById(R.id.iv_buyTitle);

        tv_myCoinNum.setText(payInfo.getTotal_egold()+"");
        tv_buyCoinNum.setText(catalogModel.getSaleprice() + "书币");
        boolean canBuy = catalogModel.getSaleprice() < payInfo.getTotal_egold();
        if (canBuy){
            btn_pay.setText("确认支付");
        }else{
            btn_pay.setText("立即充值");
        }
        if (bookModel.getBatchbuy()==2){//整本购买
            iv_buyTitle.setImageResource(R.drawable.icon_pay_dialog_buy_all);
        }else{
            iv_buyTitle.setImageResource(R.drawable.icon_pay_dialog_buy);
        }

        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canBuy) {
                    getP().subscribeComic(catalogModel.getBook_id(),catalogModel.getId());
                } else {
                    PayActivity.toPay(context, catalogModel.getBook_id());
                }
            }
        });
        buyDialog.getDialog().findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyDialog.dismiss();
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    @Override
    public void onSubscribe(ResponseModel responseModel) {
        hideProgress();
        /*
         1.通知阅读页面刷新目录列表
         2.通知我的界面自动购买更新
         */
        EventBusManager.sendRefreshCatalogListBySubscribeEvent(new RefreshCatalogListBySubscribeEvent());
        EventBusManager.sendUpdateAutoBuyStatusEvent(new UpdateAutoBuyStatusEvent());

        Intent intent = new Intent();
        //这里统一传递章节id处理就行了
        intent.putExtra(Constants.IntentKey.ID, this.catalogModel.getId());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onSubscribeFail(NetError error) {
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
        if (buyDialog!=null){
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
