package com.jj.comics.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.R;
import com.jj.comics.adapter.detail.RewardGifAdapter;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.biz.goods.GoodsRepository;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.CommonStatusResponse;
import com.jj.comics.data.model.PayInfoResponse;
import com.jj.comics.data.model.RewardGiftsResponse;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.ui.mine.pay.PayActivity;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.eventbus.events.PaySuccessEvent;
import com.jj.comics.util.eventbus.events.RefreshRewardRecordListEvent;
import com.jj.comics.util.eventbus.events.UpdateUserInfoEvent;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 打赏弹窗
 */
public class RewardDialog extends Dialog implements View.OnClickListener, BaseQuickAdapter.OnItemClickListener {

    private RecyclerView rv_rewardGifList;//打赏礼物列表
    private RewardGifAdapter rewardGifAdapter;//打赏礼物适配器

    private TextView tv_count;
    private TextView tv_userCoin;
    private FrameLayout fl_toReward, fl_toPay;

    private int gifNums = 1;//当前礼物数量
    private RewardGiftsResponse.DataBean reward;//记录当前选择的礼物
    private long contentId;
    private UserInfo loginUser;
    private BaseActivity mContext;
    private int userCoins;//用户金币数量

    public RewardDialog(@NonNull final BaseActivity context, OnDismissListener onDismissListener,
                        long contentId) {
        super(context, R.style.comic_CustomDialog);
        this.setOnDismissListener(onDismissListener);
        EventBus.getDefault().register(this);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams attributes = dialogWindow.getAttributes();
        attributes.gravity = Gravity.BOTTOM;
        dialogWindow.setAttributes(attributes);

        this.contentId = contentId;
        this.mContext = context;
        View contentView = View.inflate(context, R.layout.comic_detail_reward, null);
        setContentView(contentView);
        contentView.findViewById(R.id.iv_close).setOnClickListener(this);
        contentView.findViewById(R.id.iv_up).setOnClickListener(this);
        contentView.findViewById(R.id.iv_down).setOnClickListener(this);
        tv_count = contentView.findViewById(R.id.tv_count);
        fl_toReward = contentView.findViewById(R.id.fl_toReward);
        fl_toReward.setOnClickListener(this);
        fl_toPay = contentView.findViewById(R.id.fl_toPay);
        fl_toPay.setOnClickListener(this);
        tv_userCoin = contentView.findViewById(R.id.tv_userCoin);
        rv_rewardGifList = contentView.findViewById(R.id.rv_rewardGifList);
        rewardGifAdapter = new RewardGifAdapter(R.layout.comic_detail_reward_item);
        rewardGifAdapter.setOnItemClickListener(this);
        rv_rewardGifList.setHasFixedSize(true);
        rv_rewardGifList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        rv_rewardGifList.setAdapter(rewardGifAdapter);

        getRewardGoodsList();
        getUserPayInfo();
    }

    @Override
    public void show() {
        super.show();
        //设置金币数量
        getUserPayInfo();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_close) {
            dismiss();
        } else {
            if (reward == null) {//对象为空表示还未获取到礼物列表
                return;
            }
            if (id == R.id.iv_up) {//增加礼物数量
                gifNums++;
                tv_count.setText(gifNums + "");
                if (!isBalanceEnough()) return;
            } else if (id == R.id.iv_down) {//减少礼物数量
                if (gifNums == 1) {
                    return;
                }
                gifNums--;
                tv_count.setText(gifNums + "");
                isBalanceEnough();
            } else if (id == R.id.fl_toReward) {//豪气打赏
                if (!isBalanceEnough()) return;
                reward();
            } else if (id == R.id.fl_toPay) {//我要充值
                if (mContext instanceof Activity)
                    PayActivity.toPay(mContext, contentId);
                else {
                    ARouter.getInstance().build(RouterMap.COMIC_PAY_ACTIVITY).
                            withLong(Constants.IntentKey.BOOK_ID, contentId).navigation(mContext);
                }
            }
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        gifNums = 1;
        tv_count.setText(gifNums + "");
        rewardGifAdapter.notifyAllItem(position);
        reward = rewardGifAdapter.getData().get(position);
        isBalanceEnough();
    }

    /**
     * 计算余额是否充足 true:充足|false:不足
     */
    private boolean isBalanceEnough() {
        if (reward == null)return false;
        if (gifNums * reward.getPrice() > userCoins) {//余额不足
            fl_toReward.setVisibility(View.GONE);
            fl_toPay.setVisibility(View.VISIBLE);
            return false;
        } else {
            fl_toReward.setVisibility(View.VISIBLE);
            fl_toPay.setVisibility(View.GONE);
            return true;
        }
    }

    /**
     * 接受充值金币成功后，刷新用户信息的回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshCoin(PaySuccessEvent event) {
        if (reward == null) {
            return;
        }
        getUserPayInfo();
    }

    /**
     * 打赏
     */
    private void reward() {
        mContext.showProgress();
        UserRepository.getInstance().doReward(contentId, reward.getType(), gifNums)
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.<CommonStatusResponse>autoDisposable(AndroidLifecycleScopeProvider.from(mContext.getLifecycle(), Lifecycle.Event.ON_DESTROY)))
                .subscribe(new ApiSubscriber2<CommonStatusResponse>() {
                    @Override
                    public void onNext(CommonStatusResponse response) {
                        if (response.getData().getStatus()) {//打赏成功
                            ToastUtil.showToastShort(mContext.getResources().getString(R.string.comic_reward_success));
                            dismiss();

                            EventBusManager.sendRefreshRewardRecordListEvent(new RefreshRewardRecordListEvent());//通知刷新打赏列表
                            EventBusManager.sendUpdateUserInfoEvent(new UpdateUserInfoEvent());//通知我的界面刷新用户数据
                        } else {
                            ToastUtil.showToastShort(response.getMessage());
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToastShort(error.getMessage());
                    }

                    @Override
                    protected void onEnd() {
                        mContext.hideProgress();
                    }
                });
    }

    /**
     * 获取打赏礼物列表
     */
    private void getRewardGoodsList() {
        mContext.showProgress();
        GoodsRepository.getInstance().getRewardGoodsList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.<RewardGiftsResponse>autoDisposable(AndroidLifecycleScopeProvider.from(mContext.getLifecycle(), Lifecycle.Event.ON_DESTROY)))
                .subscribe(new ApiSubscriber2<RewardGiftsResponse>() {
                    @Override
                    public void onNext(RewardGiftsResponse response) {
                        rewardGifAdapter.setNewData(response.getData());
                        reward = rewardGifAdapter.getData().get(0);
                        isBalanceEnough();
                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToastShort(error.getMessage());
                    }

                    @Override
                    protected void onEnd() {
                        mContext.hideProgress();
                    }
                });
    }

    /**
     * 获取用户余额信息
     */
    private void getUserPayInfo() {
        UserRepository.getInstance().getUserPayInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.<PayInfoResponse>autoDisposable(AndroidLifecycleScopeProvider.from(mContext.getLifecycle(), Lifecycle.Event.ON_DESTROY)))
                .subscribe(new ApiSubscriber2<PayInfoResponse>() {

                    @Override
                    public void onNext(PayInfoResponse payInfoResponse) {
                        if (payInfoResponse.getData() != null) {
                            userCoins = payInfoResponse.getData().getTotal_egold();
                            tv_userCoin.setText(userCoins+"");
                            isBalanceEnough();
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToastShort(error.getMessage());
                    }
                });
    }

    /**
     * 释放资源
     */
    public void release() {
        EventBus.getDefault().unregister(this);
    }


}
