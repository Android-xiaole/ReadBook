package com.jj.comics.ui.mine.pay;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jj.base.BaseApplication;
import com.jj.base.dialog.CustomFragmentDialog;
import com.jj.base.log.LogUtil;
import com.jj.base.ui.BaseFragment;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.mine.RechargeCoinAdapter;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.constants.UmEventID;
import com.jj.comics.data.model.PaySettingResponse;
import com.jj.comics.data.model.TLPayResponse;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.ui.dialog.BottomPayDialog;
import com.jj.comics.ui.web.WebActivity;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.eventbus.events.PaySuccessEvent;
import com.jj.comics.util.eventbus.events.UpdateUserInfoEvent;
import com.jj.comics.util.eventbus.events.WxPayEvent;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.jj.comics.common.constants.RequestCode.TL_ALI_REQUEST_CODE;

@Route(path = RouterMap.COMIC_PAY_ACTIVITY)
public class PayFragment extends BaseFragment<PayPresenter> implements PayContract.IPayView {
    @BindView(R2.id.shubi_Recycler)
    RecyclerView mShubiRecycler;
    @BindView(R2.id.pay_list_refresh)
    SwipeRefreshLayout mRefresh;
    private RechargeCoinAdapter mShubiAdapter;
    private CustomFragmentDialog mPayDialog;
    private CustomFragmentDialog mPayProgressDialog;

    private long mBookId = 0;
    private String payType;//充值类型
    private int GET_TL_STATUS_COUNT = 0;
    private String tlTradeNo;
    private TimerTask mTask = null;
    private String mGoodsName;
    private String mPayWay;

    @Override
    public void initData(Bundle savedInstanceState) {
        payType = getArguments().getString(Constants.IntentKey.PAY_TYPE);
        mBookId = getArguments().getLong(Constants.IntentKey.BOOK_ID, 0);
        mRefresh.setColorSchemeColors(getResources().getColor(R.color.comic_yellow_ffd850));
        mShubiRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
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
                    mGoodsName = dataBean.getDescription();
                }
            }
        });

        showProgress();
        getP().loadData(payType);
    }

    private View getFooterView() {
        View footView = View.inflate(getActivity(), R.layout.comic_pay_footer, null);
        return footView;
    }

    /**
     * 支付对话框
     *
     * @param view
     */
    private void showBottomDialog(View view, final long goodsid) {
        final BottomPayDialog bottomPayDialog = new BottomPayDialog();
        bottomPayDialog.showBottomPop(getActivity(), view, new BottomPayDialog.AliPayOnClickListener() {
            @Override
            public void onClick(View v) {
                bottomPayDialog.dismiss();
                if (goodsid != -1) {
                    showProgress();
                    mPayWay = "通联支付宝";
                    getP().payAliTL(getActivity(), goodsid, mBookId);
                }
            }
        }, new BottomPayDialog.WeChatOnClickListener() {
            @Override
            public void onClick(View v) {
                bottomPayDialog.dismiss();
                if (goodsid != -1) {
                    showProgress();
                    mPayWay = "微信官方";
                    getP().payWx(getActivity(), goodsid, mBookId);
                }
            }
        }, new BottomPayDialog.CancelOnClickListener() {
            @Override
            public void onClick(View v) {
                bottomPayDialog.dismiss();
            }
        });
    }

    private void umeng(String result) {
        String type="-";
        String actionId="-";
        if (payType.equals("1")) {
            type = "金币充值";
            actionId = UmEventID.ACTION_RECHARGE;
        }else {
            type = "会员充值";
            actionId = UmEventID.ACTION_VIP;
        }
        Map<String, String> pay = new HashMap<String, String>();
        pay.put("type",type);
        pay.put("book","" + mBookId);
        pay.put("goods",mGoodsName);
        pay.put("way", mPayWay);
        pay.put("result", result);
        MobclickAgent.onEvent(BaseApplication.getApplication(), actionId, pay);
    }

    /**
     * 支付成功的回调
     * 如果是首冲用户，那就刷新商品列表，去掉首冲角标
     * 最后要更新用户信息
     */
    @Override
    public void onPaySuccess() {
        umeng("支付成功");
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
        TLPayResponse.DataBean data = response.getData();
        String pay_content = "";
        if (data != null) {
            pay_content = data.getPay_content();
            tlTradeNo = data.getOut_trade_no();
            if (TextUtils.isEmpty(pay_content) || TextUtils.isEmpty(tlTradeNo)) {
                showToastShort("获取支付宝支付链接或订单号失败，请重试！");
            } else {
                ARouter.getInstance().build(RouterMap.COMIC_WEBVIEW_ACTIVITY)
                        .withString(WebActivity.URL_KEY, pay_content)
                        .navigation(getActivity(), TL_ALI_REQUEST_CODE);
            }
        }

    }

    @Override
    public void onGetAliTLTradeStatus(boolean trade) {
        GET_TL_STATUS_COUNT++;

        if (trade) {
            onPaySuccess();
            showPayProgress(false);
        } else {
            //延时重试
            if (mTask == null) {
                mTask = new TimerTask() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getP().getAlilTLStatus(tlTradeNo);
                                LogUtil.d("GET_TL_STATUS_COUNT:::" + GET_TL_STATUS_COUNT);
                            }
                        });
                    }
                };
                new Timer().schedule(mTask, 3000, 3000);
            }


            if (GET_TL_STATUS_COUNT == 3) {
                payFail("tl fail");
                if (mTask != null) mTask.cancel();
                GET_TL_STATUS_COUNT = 0;
                mTask = null;
            }
        }


        }

        /**
         * 微信支付的回调
         *
         * @param baseResp
         */
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void wxPay (WxPayEvent baseResp){
            int errCode = baseResp.errCode;
            if (errCode == 0) {
                onPaySuccess();
            } else {
                //微信支付失败 弹窗提示
                payFail(baseResp.errCode + ":" + baseResp.msg);
            }
        }

        @Override
        public void payFail (String msg){
            umeng("支付失败");
            hideProgress();
            showToastShort(msg);
            showPayDialog(false);
            showPayProgress(false);
        }

        private void showPayProgress ( boolean show){
            if (show) {
                if (mPayProgressDialog == null) {
                    mPayProgressDialog = new CustomFragmentDialog();
                    mPayProgressDialog.setCancelable(false);
                }
                mPayProgressDialog.show(getActivity(), getFragmentManager(), R.layout.comic_layout_dilog_progress,
                        R.style.base_dialogTransparent);
            } else {
                if (mPayProgressDialog != null) mPayProgressDialog.dismiss();
            }
        }


        private void showPayDialog ( boolean isSuc){
            if (mPayDialog == null) {
                mPayDialog = new CustomFragmentDialog();
            }
            mPayDialog.show(getActivity(), getFragmentManager(), R.layout.comic_pay_fail, R.style.comic_dialog_window_transparent);
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
        public int getLayoutId () {
            return R.layout.comic_fragment_pay;
        }

        @Override
        public PayPresenter setPresenter () {
            return new PayPresenter();
        }

        @Override
        public void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
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
            }

            if (requestCode == 0x1020) {
                String respCode = data.getExtras().getString("respCode");
                String respMessage = data.getExtras().getString("respMessage");
//                showToastShort(respCode + ":" + respMessage);

                // respCode 值说明
                // "1"：成功，
                // "-1"：失败，
                // "0"：取消，
                // "-2"：错误，
                // "-3"：未知
            } else if (requestCode == TL_ALI_REQUEST_CODE) {
                showPayProgress(true);
                getP().getAlilTLStatus(tlTradeNo);
            }
        }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mPayDialog = null;
        mPayProgressDialog = null;
        if (mTask != null) mTask.cancel();
        mTask = null;
    }

    @Override
        public boolean useEventBus () {
            return true;
        }


    }
