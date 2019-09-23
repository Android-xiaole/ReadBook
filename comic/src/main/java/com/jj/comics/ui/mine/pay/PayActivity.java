package com.jj.comics.ui.mine.pay;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jj.base.dialog.CustomFragmentDialog;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.PackageUtil;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.mine.RechargeCoinAdapter;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.constants.RequestCode;
import com.jj.comics.data.model.PaySettingResponse;
import com.jj.comics.data.model.TLPayResponse;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.ui.dialog.BottomPayDialog;
import com.jj.comics.ui.web.WebActivity;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.eventbus.events.PaySuccessEvent;
import com.jj.comics.util.eventbus.events.UpdateUserInfoEvent;
import com.jj.comics.util.eventbus.events.WxPayEvent;
import com.jj.comics.widget.comic.toolbar.ComicToolBar;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import butterknife.BindView;

@Route(path = RouterMap.COMIC_PAY_ACTIVITY)
public class PayActivity extends BaseActivity<PayPresenter> implements PayContract.IPayView {
    @BindView(R2.id.shubi_Recycler)
    RecyclerView mShubiRecycler;
    @BindView(R2.id.pay_list_refresh)
    SwipeRefreshLayout mRefresh;
    @BindView(R2.id.comic_tool_bar)
    ComicToolBar toolBar;
    private RechargeCoinAdapter mShubiAdapter;
    private CustomFragmentDialog mPayDialog;

    private long mBookId = 0;
    private String payType;//充值类型

    /**
     * @param activity 上下文
     * @param type     充值类型[书币充值:1；会员充值:2]
     * @param bookId   BookModel的id
     */
    public static void toPay(Activity activity, String type, long bookId) {
        ARouter.getInstance().build(RouterMap.COMIC_PAY_ACTIVITY)
                .withString(Constants.IntentKey.PAY_TYPE, type)
                .withLong(Constants.IntentKey.BOOK_ID, bookId)
                .navigation(activity, RequestCode.PAY_REQUEST_CODE);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        payType = getIntent().getStringExtra(Constants.IntentKey.PAY_TYPE);
        mBookId = getIntent().getLongExtra(Constants.IntentKey.BOOK_ID, 0);

        mRefresh.setColorSchemeColors(getResources().getColor(R.color.comic_yellow_ffd850));
        mShubiRecycler.setLayoutManager(new LinearLayoutManager(this));
        mShubiAdapter = new RechargeCoinAdapter(R.layout.comic_item_pay_activity, payType);
        mShubiAdapter.bindToRecyclerView(mShubiRecycler);

        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getP().loadData(payType);
            }
        });
        mShubiAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.btn_toPay) {
                    PaySettingResponse.DataBeanX.DataBean dataBean = mShubiAdapter.getData().get(position);
                    showBottomDialog(view, dataBean.getId());
                }
            }
        });

        if (payType.equals("1")) {
            toolBar.setTitleText("书币充值");
        } else if (payType.equals("2")) {
            toolBar.setTitleText("会员充值");
        }
        showProgress();
        getP().loadData(payType);
    }

    private View getHeadView() {
        View head_view = View.inflate(this, R.layout.comic_pay_header, null);
        return head_view;
    }

    private View getFooterView() {
        View footView = View.inflate(this, R.layout.comic_pay_footer, null);
        return footView;
    }

    /**
     * 支付对话框
     *
     * @param view
     */
    private void showBottomDialog(View view, final long goodsid) {
        final BottomPayDialog bottomPayDialog = new BottomPayDialog();
        bottomPayDialog.showBottomPop(PayActivity.this, view, new BottomPayDialog.AliPayOnClickListener() {
            @Override
            public void onClick(View v) {
                bottomPayDialog.dismiss();
                if (goodsid != -1) {
                    showProgress();
                    if (PackageUtil.isAliPayInstalled(PayActivity.this)) {
                        MobclickAgent.onEvent(PayActivity.this, Constants.UMEventId.HAS_ALIPAY, "已安装");
                    } else {
                        MobclickAgent.onEvent(PayActivity.this, Constants.UMEventId.HAS_ALIPAY, "未安装");
                    }
//                    getP().goPay(ProductPayTypeEnum.AliPay, goodsid, PayActivity.this);
                    getP().payAliTL(PayActivity.this, goodsid, mBookId);
                }
            }
        }, new BottomPayDialog.WeChatOnClickListener() {
            @Override
            public void onClick(View v) {
                bottomPayDialog.dismiss();
                if (goodsid != -1) {
                    showProgress();
//                    getP().goPay(ProductPayTypeEnum.WeChat, goodsPriceModel, PayActivity.this);
                    getP().payWx(PayActivity.this, goodsid, mBookId);
                }
            }
        }, new BottomPayDialog.CancelOnClickListener() {
            @Override
            public void onClick(View v) {
                bottomPayDialog.dismiss();
            }
        });
    }

    /**
     * 支付成功的回调
     * 如果是首冲用户，那就刷新商品列表，去掉首冲角标
     * 最后要更新用户信息
     */
    @Override
    public void onPaySuccess() {
        hideProgress();
        EventBusManager.sendPaySuccessEvent(new PaySuccessEvent());
        showPayDialog(true);
    }

    @Override
    public void fillData(PaySettingResponse response) {
        if (response.getData() != null && response.getData().getData() != null && response.getData().getData().size() != 0) {
            mShubiAdapter.setNewData(response.getData().getData());
            View footerView = getFooterView();
            mShubiAdapter.setFooterView(footerView);
            TextView tv_des = footerView.findViewById(R.id.tv_des);
            tv_des.setText(response.getData().getDescribe());
        }
    }

    @Override
    public void loadEnd() {
        if (mRefresh.isRefreshing()) mRefresh.setRefreshing(false);
        hideProgress();
    }

    /**
     * 获取用户信息的回调
     *
     * @param user
     */
    @Override
    public void onGetUserData(UserInfo user) {
        EventBusManager.sendUpdateUserInfoEvent(new UpdateUserInfoEvent());
    }

    @Override
    public void onGetTLPayInfo(TLPayResponse response) {
        String data = response.getData();
        ARouter.getInstance().build(RouterMap.COMIC_WEBVIEW_ACTIVITY).withString(WebActivity.URL_KEY,data).navigation();
    }

    /**
     * 微信支付的回调
     *
     * @param baseResp
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void wxPay(WxPayEvent baseResp) {
        int errCode = baseResp.errCode;
        if (errCode == 0) {
            onPaySuccess();
        } else {
            //微信支付失败 弹窗提示
            payFail(baseResp.errCode + ":" + baseResp.msg);
        }
    }

    @Override
    public void payFail(String msg) {
        hideProgress();
        showToastShort(msg);
        showPayDialog(false);
    }

    private void showPayDialog(boolean isSuc) {
        if (mPayDialog == null) {
            mPayDialog = new CustomFragmentDialog();
        }
        mPayDialog.show(this, getSupportFragmentManager(), R.layout.comic_pay_fail, R.style.comic_dialog_window_transparent);
        mPayDialog.getDialog().findViewById(R.id.rootView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPayDialog.dismiss();
            }
        });
        ImageView payResult = mPayDialog.getDialog().findViewById(R.id.pay_result);
        if (isSuc) {
            payResult.setImageResource(R.drawable.pay_suc);
        } else {
            payResult.setImageResource(R.drawable.bg_comic_dialog_vippay_zhifushibai);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_activity_pay;
    }

    @Override
    public PayPresenter setPresenter() {
        return new PayPresenter();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
//            switch (requestCode) {
//                case RequestCode.WEB_REQUEST_CODE:
//                    isGotoPay = true;
//                    break;
//            }

        } else if (resultCode == RESULT_CANCELED) {
//            switch (requestCode) {
//                case RequestCode.WEB_REQUEST_CODE:
//                    isGotoPay = false;
//                    showToastShort(this, "您取消了支付");
//                    break;
//            }
        } else if (requestCode == 0x1020) {
            String respCode = data.getExtras().getString("respCode");
            String respMessage = data.getExtras().getString("respMessage");
            showToastShort(respCode + ":" + respMessage);

            // respCode 值说明
            // "1"：成功，
            // "-1"：失败，
            // "0"：取消，
            // "-2"：错误，
            // "-3"：未知
        }

    }


    @Override
    public boolean useEventBus() {
        return true;
    }


}
