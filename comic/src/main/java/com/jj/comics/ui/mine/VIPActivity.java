package com.jj.comics.ui.mine;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.mine.VIPAdapter;
import com.jj.comics.adapter.mine.VIPWelfareItemAdapter;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.model.VIPListResponse;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.reporter.ActionReporter;
import com.jj.comics.common.constants.RequestCode;
import com.jj.comics.data.model.GoodsPriceModel;
import com.jj.comics.data.model.SelectionWithGood;
import com.jj.comics.data.model.TaskModel;
import com.jj.comics.data.model.Tasks;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.util.eventbus.events.UpdateUserInfoEvent;
import com.jj.comics.util.eventbus.events.WxPayEvent;
import com.jj.comics.ui.dialog.PayFailDialog;
import com.jj.comics.util.eventbus.EventBusManager;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 会员中心页面
 */
@Route(path = RouterMap.COMIC_VIP_ACTIVITY)
public class VIPActivity extends BaseActivity<VIPPresenter> implements VIPContract.IVIPView,
        VIPWelfareItemAdapter.TaskOnClickListener {
    //会员列表
    @BindView(R2.id.VIP_Recycler)
    RecyclerView mRecycler;
    //会员信息适配器
    private VIPAdapter mAdapter;
    //支付对话框
    private AlertDialog mPayFailDialog;


    @Override
    public void initData(Bundle savedInstanceState) {
        //上传访问会员支付页  key为accessUserPay 友盟事件统计
        MobclickAgent.onEvent(this, Constants.UMEventId.ACCESS_USER_PAY);
        mAdapter = new VIPAdapter(getP(), this, null);
        final GridLayoutManager layoutManager = new GridLayoutManager(this, 2) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mRecycler.setLayoutManager(layoutManager);
        mRecycler.setFocusable(false);
        mRecycler.setFocusableInTouchMode(false);
//        mRecycler.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.VERTICAL, Utils.dip2px(this, 36), getResources().getColor(android.R.color.white)));
        mAdapter.bindToRecyclerView(mRecycler);
        //设置添加不同item 布局样式
        mAdapter.setSpanSizeLookup(new BaseQuickAdapter.SpanSizeLookup() {
            @Override
            public int getSpanSize(GridLayoutManager gridLayoutManager, int position) {
                return mAdapter.getItemViewType(position) == VIPAdapter.TYPE_LEVEL_1 ? 1 : layoutManager.getSpanCount();
            }
        });

        showProgress();
        //回去全部会员数据
        getP().getNewVIPList(false);
        ActionReporter.reportAction(ActionReporter.Event.VIP, null, null, null);
    }

    /**
     * 添加会员数据
     *
     * @param vipListResponses
     */
    @Override
    public void fillData(List<VIPListResponse.DataBean> vipListResponses) {
        hideProgress();
        if (vipListResponses == null) {
            return;
        }
        List<MultiItemEntity> list = new ArrayList<>();
        for (VIPListResponse.DataBean dataBean : vipListResponses) {
            if (vipListResponses == null) {
                break;
            }
            for (VIPListResponse.DataBean.ListBean listBean : dataBean.getList()) {
                dataBean.addSubItem(listBean);
            }
            list.add(dataBean);
        }
        mAdapter.addData(list);
        mAdapter.expandAll();
    }

    @Override
    public void dataFail(NetError error) {
        ToastUtil.showToastShort(error.getMessage());
    }


    /**
     * 网络错误取消对话框
     */
    @Override
    public void dismissLoading() {
        hideProgress();
    }

    /**
     * 加载资源布局
     *
     * @return
     */
    @Override
    public int getLayoutId() {
        return R.layout.comic_activity_vip;
    }

    /**
     * 实例化 P层
     *
     * @return
     */
    @Override
    public VIPPresenter setPresenter() {
        return new VIPPresenter();
    }

    /**
     * 跳转支付页面 未使用
     *
     * @param activity
     */
    public static void toPay(Activity activity) {
        ARouter.getInstance().build(RouterMap.COMIC_VIP_ACTIVITY).navigation(activity, RequestCode.VIP_REQUEST_CODE);
    }

    /**
     * 支付失败时调用
     */
    @Override
    public void payFail(String msg) {
        hideProgress();
        showToastShort(msg);
        AlertDialog payFailDialog = getPayFailDialog(false);
        if (payFailDialog != null && !payFailDialog.isShowing()) payFailDialog.show();
    }

    @Override
    public void onPaySuccess() {
        hideProgress();
        AlertDialog payFailDialog = getPayFailDialog(true);
        if (payFailDialog != null && !payFailDialog.isShowing()) payFailDialog.show();
    }

    /**
     * 调用支付对话框
     *
     * @return
     */
    private AlertDialog getPayFailDialog(boolean isSuc) {
        if (mPayFailDialog == null) {
            mPayFailDialog = new AlertDialog.Builder(this, R.style.comic_Dialog_no_title).create();
            final Window dialogWindow = mPayFailDialog.getWindow();
            mPayFailDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
//                    ImmersionBar.with(context, mPayFailDialog).destroy();
                }
            });


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

    /**
     * 微信支付回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void wxPay(WxPayEvent wxPayEvent) {
        int errCode = wxPayEvent.errCode;
        if (errCode == 0) {
            showToastShort(getString(R.string.comic_pay_success));
//            EventBus.getDefault().post(new EventModel());
            EventBusManager.sendUpdateUserInfoEvent(new UpdateUserInfoEvent());
        } else {
            //微信支付失败 弹窗提示
            payFail(wxPayEvent.errCode + ":" + wxPayEvent.msg);
        }
    }

    /**
     * 会员大礼包 item点击事件
     *
     * @param view      会员大礼包item
     * @param taskModel 单个任务类型信息
     */
    // TODO: 2019/3/27   意见:确定当前礼包做好添加会员等级属性，现在固定值  
    @Override
    public void onClick(View view, TaskModel taskModel) {
//        UserInfo userInfo = LoginHelper.getOnLineUser();
//        if (userInfo.getIsVip()) {
//            if (userInfo.getVipLevel() < 3) {
////            Toast.makeText(VIPActivity.this, R.string.comic_open_higher_vip_remind, Toast.LENGTH_LONG).show();
//                ToastUtil.showToastLong(getString(R.string.comic_open_higher_vip_remind));
//            } else {
//                if ("SVIP".equals(userInfo.getVipName()) && Constants.TaskCode.SVIP_DAILY.equals(taskModel.getCode())) {
//                    //SVIP会员每日领取
//                    getP().reportTask(Constants.TaskCode.SVIP_DAILY);
//                } else if (Constants.TaskCode.OPEN_90_UP_VIP.equals(taskModel.getCode())) {
//                    //开通90天会员领取
//                    getP().reportTask(Constants.TaskCode.OPEN_90_UP_VIP);
//                }
//            }
//        } else {
//            ToastUtil.showToastLong(getString(R.string.comic_open_higher_vip_remind));
//        }
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0x1020) {//汇付宝code
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

}
