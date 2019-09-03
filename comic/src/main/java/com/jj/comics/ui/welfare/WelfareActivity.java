package com.jj.comics.ui.welfare;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.gyf.barlibrary.ImmersionBar;
import com.jj.base.dialog.CustomFragmentDialog;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.Utils;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.mine.WelfareCoinAdapter;
import com.jj.comics.adapter.mine.WelfareItemAdapter;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.constants.RequestCode;
import com.jj.comics.data.model.SignAutoResponse;
import com.jj.comics.data.model.SignResponse;
import com.jj.comics.data.model.SignTaskResponse;
import com.jj.comics.ui.dialog.ShareDialog;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.eventbus.events.ChangeTabBarEvent;
import com.jj.comics.util.eventbus.events.UpdateSignStatusEvent;
import com.jj.comics.widget.NestedRecyclerView;
import com.jj.comics.widget.RecycleViewDivider;
import com.jj.comics.widget.comic.snackbar.BaseTransientBottomBar;
import com.jj.comics.widget.comic.snackbar.TopSnackBar;
import com.jj.comics.widget.comic.toolbar.ComicToolBar;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 金币中心
 */
@Route(path = RouterMap.COMIC_GOLD_CENTER_ACTIVITY)
public class WelfareActivity extends BaseActivity<WelfarePresenter> implements WelfareContract.IWelfareView, WelfareItemAdapter.TaskOnClickListener, ComicToolBar.OnComicToolBarListener {

    @BindView(R2.id.toolBar)
    ComicToolBar toolBar;
    //福利列表
    @BindView(R2.id.comic_welfare_daily_task_recycler)
    RecyclerView dailyRecycler;
    //签到按钮
    ImageView signIn;
    //今日金币数
    TextView mCoins;
    //金币列表
    NestedRecyclerView coinsRecycler;
    //任务adapter
    private WelfareItemAdapter itemAdapter;
    //金币adapter
    private WelfareCoinAdapter coinAdapter;
    //当前金币数
    public String Current_Coins = "0";
    //分享对话框
    private ShareDialog shareDialog;
    private int lastOffset = 0;
    private int lastPosition = 0;

    @Override
    public void initData(Bundle savedInstanceState) {
        //上传访问金币中心页  key为accessGoldCenter
        MobclickAgent.onEvent(this, Constants.UMEventId.ACCESS_GOLD_CENTER);
        toolBar.addChildClickListener(this);
        itemAdapter = new WelfareItemAdapter(WelfareActivity.this, null);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        dailyRecycler.setLayoutManager(layoutManager);
        dailyRecycler.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.VERTICAL, Utils.dip2px(this, 1), getResources().getColor(android.R.color.white)));
        itemAdapter.bindToRecyclerView(dailyRecycler);
        itemAdapter.setHeaderView(getHeaderView());

        showProgress();
        //签到和任务信息获取
        getP().getSignTasks();
        //签到状态和自动购买信息获取
        getP().signAuto();
    }

    private View getHeaderView() {
        View head_view = View.inflate(this, R.layout.comic_activity_welfare_head, null);
        signIn = head_view.findViewById(R.id.comic_welfare_sign_in);
        mCoins = head_view.findViewById(R.id.comic_welfare_coins);
        coinsRecycler = head_view.findViewById(R.id.welfare_coins_recycler);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getP().getSignIn();
            }
        });

        //金币数据填充
        coinAdapter = new WelfareCoinAdapter(R.layout.comic_welfare_coins_item);
        coinsRecycler.setHasFixedSize(true);
        coinsRecycler.setLayoutManager(new GridLayoutManager(this, 7));
        coinsRecycler.setFocusableInTouchMode(false);
        coinsRecycler.setFocusable(false);
        coinsRecycler.setLayoutManager(new GridLayoutManager(this, 7) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        coinAdapter.bindToRecyclerView(coinsRecycler);
        return head_view;
    }

    @Override
    public void fillSignAuto(SignAutoResponse response) {
        if (response.getData().getIs_check() == 0) {
            signIn.setClickable(true);
            signIn.setImageResource(R.drawable.comic_welfare_qiandao_normal);
        } else {
            signIn.setClickable(false);
            signIn.setImageResource(R.drawable.comic_welfare_qiandao_press);
        }
    }

    @Override
    public void fillSignTasks(SignTaskResponse response) {
        coinAdapter.setNewData(response.getData().getQiandao_list());
        int chceckRecord = response.getData().getCheckrecord();
        if (chceckRecord >= 7) {
            chceckRecord = 6;
        }
        Current_Coins = response.getData().getQiandao_list().get(chceckRecord).getValue();
        mCoins.setText("" + response.getData().getTake());
        List<MultiItemEntity> list = new ArrayList<>();
        for (SignTaskResponse.DataBean.TaskListBean taskListBean : response.getData().getTask_list()) {
            if (taskListBean.getList() == null) {
                break;
            }
            for (SignTaskResponse.DataBean.TaskListBean.ListBean listBean : taskListBean.getList()) {
                listBean.setP_type(taskListBean.getType());
                taskListBean.addSubItem(listBean);
            }
            list.add(taskListBean);
        }
        itemAdapter.setNewData(list);
        itemAdapter.expandAll();
    }

    @Override
    public void getCoinSuccess() {
        snackBar("金币领取成功");
    }

    @Override
    public void getCoinFail(String msg) {
        snackBar(msg);
    }

    /**
     * 填充签到数据
     *
     * @param signResponse
     */
    @Override
    public void fillSignInData(SignResponse signResponse) {
        if (signResponse.getCode() == 200) {
            EventBusManager.sendUpdateSignStatusEvent(new UpdateSignStatusEvent());
            signIn.setClickable(false);
            signIn.setImageResource(R.drawable.comic_welfare_qiandao_press);
            getP().getSignTasks();
            showGetRewardDialog(Current_Coins);
        }
    }

    private CustomFragmentDialog customFragmentDialog;

    /**
     * 签到成功对话框
     *
     * @param reward
     */
    @Override
    public void showGetRewardDialog(String reward) {
        if (customFragmentDialog == null) customFragmentDialog = new CustomFragmentDialog();
        customFragmentDialog.show(this, getSupportFragmentManager(), R.layout.comic_dialog_sign_sucess_new);
        WindowManager.LayoutParams attributes = customFragmentDialog.getDialog().getWindow().getAttributes();
        attributes.gravity = Gravity.TOP;
        TextView coins = customFragmentDialog.getDialog().findViewById(R.id.comic_welfare_coins);


        coins.setText("" + reward);
        TextView confirm = customFragmentDialog.getDialog().findViewById(R.id.dialog_sign_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customFragmentDialog.dismiss();
                if (shareDialog == null) {
                    shareDialog = new ShareDialog(WelfareActivity.this);
                }
                shareDialog.show();
            }
        });
    }

    /**
     * 加载布局
     *
     * @return
     */
    @Override
    public int getLayoutId() {
        return R.layout.comic_activity_welfare;
    }

    /**
     * 实例化P 层
     *
     * @return
     */
    @Override
    public WelfarePresenter setPresenter() {
        return new WelfarePresenter();
    }

    // 跳转支付页 未使用
    public static void toPay(Activity activity) {
        ARouter.getInstance().build(RouterMap.COMIC_GOLD_CENTER_ACTIVITY).navigation(activity, RequestCode.WELFARE_REQUEST_CODE);
    }

    /**
     * 退出按钮
     *
     * @param childView
     */
    @Override
    public void onComicToolBarLeftIconClick(View childView) {
        finish();
    }

    /**
     * 分享按钮
     *
     * @param childView
     */
    @Override
    public void onComicToolBarRightIconClick(View childView) {
        if (shareDialog == null) {
            shareDialog = new ShareDialog(WelfareActivity.this);
        }
        shareDialog.show();
    }

    /**
     * 任务中心点击 事件
     *
     * @param type
     * @param getReward
     */
    @Override
    public void onClick(String pType, String type, boolean getReward) {
        if ("vip".equals(pType) || "everyday".equals(pType)) { //每日任务
            if (Constants.TaskCode.SIGN_CHECK.equals(type)) {
                getP().getSignIn();
            } else if (Constants.TaskCode.ACTIVE_VIP.equals(type) && !getReward) {
                ARouter.getInstance().build(RouterMap.COMIC_VIP_ACTIVITY).navigation(this, RequestCode.WELFARE_REQUEST_CODE);
            } else if (Constants.TaskCode.SHARE.equals(type) || Constants.TaskCode.INVITE.equals(type)) {
                if (getReward) {
                    getP().getDayGold(type);//领取金币
                } else {
                    if (shareDialog == null) {
                        shareDialog = new ShareDialog(WelfareActivity.this);
                    }
                    shareDialog.show();
                }
            } else {
                if (getReward) {
                    getP().getDayGold(type);
                } else {
                    EventBusManager.sendChangeTabBarEvent(new ChangeTabBarEvent(1));
                    finish();
                }
            }
        } else if ("new".equals(pType)) { //新手任务
            if (getReward) {
                getP().getNewGold(type);//领取金币
            } else {
                EventBusManager.sendChangeTabBarEvent(new ChangeTabBarEvent(1));
                finish();
            }
        }
    }

    /**
     * 状态栏修改
     */
    @Override
    protected void initImmersionBar() {
        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.base_color_ffffff)
                .statusBarDarkFont(true, 0.2f)
                .init();
    }

    /**
     * 分享等成功返回后保证任务状态实时刷新
     */
    @Override
    public void onResume() {
        super.onResume();
        getP().getSignTasks();
    }

    /**
     * 记录RecyclerView当前位置
     */
    private void getPositionAndOffset() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) dailyRecycler.getLayoutManager();
        //获取可视的第一个view
        View topView = layoutManager.getChildAt(0);
        if (topView != null) {
            //获取与该view的顶部的偏移量
            lastOffset = topView.getTop();
            //得到该View的数组位置
            lastPosition = layoutManager.getPosition(topView);
        }
    }

    @Override
    protected void onDestroy() {
        if (toolBar != null) {
            toolBar.removeChildClickListener();
        }
        super.onDestroy();
    }

    /**
     * 金币领取弹窗
     *
     * @param msg
     */
    private void snackBar(String msg) {
        if (dailyRecycler == null) return;
        TopSnackBar topSnackBarbar = TopSnackBar.make(dailyRecycler, msg, BaseTransientBottomBar.LENGTH_LONG);
        topSnackBarbar.setActionTextColor(getResources().getColor(R.color.comic_ff3333));
        topSnackBarbar.getView().setBackgroundColor(getResources().getColor(R.color.comic_yellow_ffd850));
        topSnackBarbar.show();
    }

}
