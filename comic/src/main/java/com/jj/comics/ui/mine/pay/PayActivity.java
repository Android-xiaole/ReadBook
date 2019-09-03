package com.jj.comics.ui.mine.pay;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.PackageUtil;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.Utils;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.mine.RechargeCoinAdapter;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.constants.RequestCode;
import com.jj.comics.data.model.GoodsPriceModel;
import com.jj.comics.data.model.PayCenterInfoResponse;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.ui.dialog.BottomPayDialog;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.eventbus.events.PaySuccessEvent;
import com.jj.comics.util.eventbus.events.UpdateUserInfoEvent;
import com.jj.comics.util.eventbus.events.WxPayEvent;
import com.jj.comics.util.reporter.ActionReporter;
import com.jj.comics.widget.UniversalItemDecoration;
import com.jj.comics.widget.comic.toolbar.ComicToolBar;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.COMIC_PAY_ACTIVITY)
public class PayActivity extends BaseActivity<PayPresenter> implements PayContract.IPayView {
    @BindView(R2.id.shubi_Recycler)
    RecyclerView mShubiRecycler;
    @BindView(R2.id.pay_list_refresh)
    SwipeRefreshLayout mRefresh;
    @BindView(R2.id.pay_confirm_charge)
    Button pay;
    private RechargeCoinAdapter mShubiAdapter;
    private AlertDialog mPayFailDialog;

    private long mBookId = 0;

    @Override
    public void initData(Bundle savedInstanceState) {
        mBookId = getIntent().getLongExtra(Constants.IntentKey.BOOK_ID, 0);
        //上传访问充值中心事件
        MobclickAgent.onEvent(this, Constants.UMEventId.ACCESS_REHARGE_CENTER);
        mRefresh.setColorSchemeColors(getResources().getColor(R.color.comic_yellow_ffd850));
        mShubiAdapter = new RechargeCoinAdapter(null);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mShubiAdapter.setSpanSizeLookup(new BaseQuickAdapter.SpanSizeLookup() {
            @Override
            public int getSpanSize(GridLayoutManager gridLayoutManager, int position) {
                if (mShubiAdapter.getItemViewType(position + mShubiAdapter.getHeaderLayoutCount()) == GoodsPriceModel.VIP)
                    return 1;
                return 2;
            }
        });
        mShubiRecycler.setLayoutManager(layoutManager);
        mShubiAdapter.bindToRecyclerView(mShubiRecycler);
        ActionReporter.reportAction(ActionReporter.Event.PAY, null, null, null);

        showProgress();
        getP().loadData();

        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getP().loadData();
            }
        });
        ComicToolBar comicToolBar = findViewById(R.id.comic_tool_bar);
//        comicToolBar.setBackgroundColorRootView(Color.parseColor("#FF4C5D"));
        mShubiRecycler.addItemDecoration(new UniversalItemDecoration() {
            @Override
            public Decoration getItemOffsets(int position) {
                Log.i("Decoration", "position: " + position + "size: " + mShubiAdapter.getItemCount());
                ColorDecoration decoration = new ColorDecoration();
                if (position >= mShubiAdapter.getItemCount() - 1) {
                    decoration.right = Utils.dip2px(PayActivity.this, 15);
                    decoration.left = Utils.dip2px(PayActivity.this, 15);
                } else {
                    if (mShubiAdapter.getData().get(position).getItemType() == 2) {
                        if (position == 0) {
                            decoration.top = Utils.dip2px(PayActivity.this, 15);
                        }
                        decoration.left = Utils.dip2px(PayActivity.this, 15);
                        decoration.right = Utils.dip2px(PayActivity.this, 15);
                        decoration.bottom = Utils.dip2px(PayActivity.this, 15);
                    } else {
                        if (mShubiAdapter.getData().get(0).getItemType() == 1){
                            if (position == 0||position == 1) {
                                decoration.top = Utils.dip2px(PayActivity.this, 15);
                            }
                        }
                        decoration.left = Utils.dip2px(PayActivity.this, 15);
                        decoration.right = Utils.dip2px(PayActivity.this, 10.5f);
                        decoration.bottom = Utils.dip2px(PayActivity.this, 15);
//                        if (position % 2 == 1 && position < mShubiAdapter.getItemCount() - 1) {
//                            decoration.left = Utils.dip2px(PayActivity.this, 15);
//                            decoration.right = Utils.dip2px(PayActivity.this, 10.5f);
//                            decoration.bottom = Utils.dip2px(PayActivity.this, 15);
//                        } else if (position % 2 == 0 && position < mShubiAdapter.getItemCount() - 2) {
//                            decoration.right = Utils.dip2px(PayActivity.this, 15);
//                            decoration.left = Utils.dip2px(PayActivity.this, 10.5f);
//                            decoration.bottom = Utils.dip2px(PayActivity.this, 15);
//                        }
                    }
                }
                decoration.decorationColor = Color.WHITE;
                return decoration;
            }
        });
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
                    getP().payAli(PayActivity.this, goodsid, mBookId);
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
        }, new BottomPayDialog.HuifubaoOnClickListener() {
            @Override
            public void onClick(View v) {
                bottomPayDialog.dismiss();
                getP().payHuifubao(PayActivity.this, goodsid, mBookId);
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
        AlertDialog payFailDialog = getPayFailDialog(true);
        if (payFailDialog != null && !payFailDialog.isShowing()) payFailDialog.show();

//        if (!LoginHelper.getOnLineUser().getPaid()) {
//            mShubiAdapter.setNewData(goodsPriceList, true);
//        }
        //支付成功后重新获取用户信息，刷新当前页面金币余额
//        getP().getUserData();
    }


    @Override
    public void fillData(List<PayCenterInfoResponse.PayCenterInfo> rechargeCoinList) {
        if (mShubiAdapter.getFooterLayoutCount() <= 0)
            mShubiAdapter.addFooterView(mShubiAdapter.getFooterView(this));
//        if (mShubiAdapter.getHeaderLayoutCount() <= 0)
//            mShubiAdapter.addHeaderView(mShubiAdapter.getHeaderView(this));
        mShubiAdapter.setNewData(rechargeCoinList);
        if (mRefresh.isRefreshing()) mRefresh.setRefreshing(false);
        hideProgress();
    }

    @Override
    public void loadFail(String msg) {
        ToastUtil.showToastShort(msg);
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

    /**
     * 微信支付的回调
     *
     * @param baseResp
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void wxPay(WxPayEvent baseResp) {
        int errCode = baseResp.errCode;
        if (errCode == 0) {
//            showToastShort(getString(R.string.comic_pay_success));
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
        AlertDialog payFailDialog = getPayFailDialog(false);
        if (payFailDialog != null && !payFailDialog.isShowing()) payFailDialog.show();
    }

    private AlertDialog getPayFailDialog(boolean isSuc) {
        if (mPayFailDialog == null) {
            mPayFailDialog = new AlertDialog.Builder(this, R.style.comic_Dialog_no_title).create();
//            ImmersionBar.with(context, mPayFailDialog)
//                    .statusBarColor(R.color.base_color_ffd850)
//                    .init();
            final Window dialogWindow = mPayFailDialog.getWindow();
            mPayFailDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
//                    ImmersionBar.with(context, mPayFailDialog).destroy();
                }
            });
//            mPayFailDialog.setOnShowListener(new DialogInterface.OnShowListener() {
//                @Override
//                public void onShow(DialogInterface dialog) {
//                    if (dialogWindow.getDecorView().findViewById(android.R.id.content) != null)
//                }
//            });


            WindowManager.LayoutParams attributes = dialogWindow.getAttributes();

            attributes.gravity = Gravity.CENTER;
            attributes.height = WindowManager.LayoutParams.MATCH_PARENT;
            attributes.width = WindowManager.LayoutParams.MATCH_PARENT;

            dialogWindow.setAttributes(attributes);
            mPayFailDialog.setCancelable(false);
            mPayFailDialog.show();
            dialogWindow.setContentView(R.layout.comic_pay_fail);
            ImageView payResult = dialogWindow.findViewById(R.id.par_result);
            if (isSuc) {
                payResult.setImageResource(R.drawable.pay_suc);
            } else {
                payResult.setImageResource(R.drawable.bg_comic_dialog_vippay_zhifushibai);
            }
            dialogWindow.findViewById(R.id.comic_pay_fail_dismiss).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPayFailDialog.dismiss();
                }
            });

        }
        return mPayFailDialog;
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_activity_pay;
    }

    @Override
    public PayPresenter setPresenter() {
        return new PayPresenter();
    }

    public static void toPay(Activity activity, long comicId) {
        ARouter.getInstance().build(RouterMap.COMIC_PAY_ACTIVITY).
                withLong(Constants.IntentKey.BOOK_ID, comicId).navigation(activity, RequestCode.PAY_REQUEST_CODE);
    }

//    @OnClick(R2.id.pay_list_home)
//    void toHome() {
//        ARouter.getInstance().build(RouterMap.COMIC_MAIN_ACTIVITY).navigation(this);
//        finish();
//    }

    @OnClick(R2.id.pay_confirm_charge)
    void pay() {
        PayCenterInfoResponse.PayCenterInfo selectModel = mShubiAdapter.getSelect();
        if (selectModel != null) {
//                    showProgress();
//                    ProductPayTypeEnum payType = mShubiAdapter.getPayType();
//                    HashMap<String, String> map = new HashMap<>();
//                    map.put("pay_way", payType.getName());
//                    map.put("pay_count", selectModel.getPrice() + "元");
//                    map.put("pay_goods", selectModel.getVip_level() + "");
//                    MobclickAgent.onEvent(context, Constants.UMEventId.CLICK_RECHARGE_CENTER_PAY, map);
//                    long comicId = getIntent().getLongExtra("comicId", 0);
            showBottomDialog(pay, selectModel.getId());
        }
    }

    @Override
    protected void onDestroy() {
        if (mShubiAdapter != null) mShubiAdapter.unbind();
        super.onDestroy();
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


    public CharSequence getReminderText() {
        return getP().getReminderText(this);
    }

    @Override
    public boolean useEventBus() {
        return true;
    }


}
