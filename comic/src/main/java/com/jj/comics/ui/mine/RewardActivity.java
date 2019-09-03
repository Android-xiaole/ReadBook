package com.jj.comics.ui.mine;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jj.base.imageloader.ILFactory;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.CommonUtil;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.mine.HotSearchAdapter;
import com.jj.comics.adapter.mine.RewardAdapter;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.RewardHistoryResponse;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.ui.detail.DetailActivityHelper;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.eventbus.events.ChangeTabBarEvent;

import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 我的打赏页面
 */
@Route(path = RouterMap.COMIC_REWARD_ACTIVITY)
public class RewardActivity extends BaseActivity<RewardPresenter> implements RewardContract.IRewardView {
    //根布局
    @BindView(R2.id.lin_root)
    LinearLayout lin_root;
    //昵称
    @BindView(R2.id.reward_header_name)
    TextView mName;
    //打赏数量
    @BindView(R2.id.reward_manhua_count)
    TextView mTvRewardCount;
    //打赏金额
    @BindView(R2.id.reward_manhua_coins)
    TextView mTvRewardCoins;
    //头像
    @BindView(R2.id.reward_header_img)
    ImageView mHead;
    //猜你喜欢
    @BindView(R2.id.reward_like_recycler)
    RecyclerView likeRecycler;
    //打赏记录
    @BindView(R2.id.reward_recycler)
    RecyclerView reward_recycler;
    //没有数据
    @BindView(R2.id.reward_nodata)
    LinearLayout nodata;
    @BindView(R2.id.reward_already_text)
    TextView already_text;

    private int page = 1;
    //热门搜索的适配器
    private HotSearchAdapter mHotAdapter = new HotSearchAdapter(R.layout.comic_item_search_hot);
    private RewardAdapter mRewardAdapter = new RewardAdapter(R.layout.comic_item_search_hot);

    /**
     * 初始化数据
     *
     * @param savedInstanceState
     */
    @Override
    public void initData(Bundle savedInstanceState) {
        initUserInfoText();
        showProgress();
        likeRecycler.setHasFixedSize(true);
        mHotAdapter = new HotSearchAdapter(R.layout.comic_item_search_hot);
        likeRecycler.setLayoutManager(new GridLayoutManager(this, 3));
        mHotAdapter.bindToRecyclerView(likeRecycler);
        mHotAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                DetailActivityHelper.toDetail(RewardActivity.this,
                        mHotAdapter.getData().get(position).getId(), "我的打赏");
            }
        });
        likeRecycler.setLayoutManager(new GridLayoutManager(this, 3) {
            @Override
            public boolean canScrollVertically() {
                //解决ScrollView里存在多个RecyclerView时滑动卡顿的问题
                //如果你的RecyclerView是水平滑动的话可以重写canScrollHorizontally方法
                return false;
            }
        });

        reward_recycler.setHasFixedSize(true);
        reward_recycler.setLayoutManager(new GridLayoutManager(this, 3));
        mRewardAdapter.bindToRecyclerView(reward_recycler);
        mRewardAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                RewardHistoryResponse.DataBean.RewardHistoryModel reward =
                        mRewardAdapter.getData().get(position);
                if (reward == null) return;

                DetailActivityHelper.toDetail(RewardActivity.this,
                        mRewardAdapter.getData().get(position).getArticleid(), "我的打赏_猜你喜欢");

            }
        });
        reward_recycler.setLayoutManager(new GridLayoutManager(this, 3) {
            @Override
            public boolean canScrollVertically() {
                //解决ScrollView里存在多个RecyclerView时滑动卡顿的问题
                //如果你的RecyclerView是水平滑动的话可以重写canScrollHorizontally方法
                return false;
            }
        });

        /*
          这里需要设置一个根布局，让其一直获取焦点，
          不然会出现recyclerview获取焦点导致界面会往上顶一些距离
         */
        lin_root.setFocusable(true);
        lin_root.setFocusableInTouchMode(true);
        lin_root.requestFocus();
    }

    @Override
    public void onResume() {
        super.onResume();
        reward_recycler.setLayoutManager(new GridLayoutManager(this, 3) {
            @Override
            public boolean canScrollVertically() {
                //解决ScrollView里存在多个RecyclerView时滑动卡顿的问题
                //如果你的RecyclerView是水平滑动的话可以重写canScrollHorizontally方法
                return false;
            }
        });

        /*
          这里需要设置一个根布局，让其一直获取焦点，
          不然会出现recyclerview获取焦点导致界面会往上顶一些距离
         */
        lin_root.setFocusable(true);
        lin_root.setFocusableInTouchMode(true);
        lin_root.requestFocus();
        getP().getRewardsHistory(false);
//        getP().getMyRewardTotalByUser();
        getP().refreshHot(page);
    }

    //加载布局
    @Override
    public int getLayoutId() {
        return R.layout.comic_activity_reward;
    }

    //实例化P层
    @Override
    public RewardPresenter setPresenter() {
        return new RewardPresenter();
    }

    /**
     * 去打赏
     */
    @OnClick(R2.id.reward_go_reward)
    void go_reward() {
//        EventBus.getDefault().post(new Refresh(0));
        EventBusManager.sendChangeTabBarEvent(new ChangeTabBarEvent(0));
        finish();
    }

//    @OnClick(R2.id.reward_list_home)
//    void toHome() {
//        ARouter.getInstance().build(RouterMap.COMIC_MAIN_ACTIVITY).navigation(this);
//        finish();
//    }

    /**
     * 刷新猜你喜欢
     */
    @OnClick(R2.id.reward_refresh)
    void refreshData() {
        page++;
        getP().refreshHot(page);
    }

    /**
     * 初始化用户信息
     */
    private void initUserInfoText() {
        UserInfo info = LoginHelper.getOnLineUser();
        if (info != null) {
            if (TextUtils.isEmpty(info.getAvatar())) {
                ILFactory.getLoader().loadResource(mHead, R.drawable.icon_user_avatar_default,
                        new RequestOptions().transform(new CircleCrop()));
            } else {
                ILFactory.getLoader()
                        .loadNet(mHead, info.getAvatar(),
                                new RequestOptions().transform(new CircleCrop()).error(R.drawable.icon_user_avatar_default));
            }
            mName.setText(info.getNickname());
        }

    }

    /**
     *
     */
    @Override
    public void fillHotData(List<BookModel> bookModelList) {
        if (bookModelList != null)
            mHotAdapter.setNewData(bookModelList);
    }

    /**
     * 填充打赏记录数据
     *
     */
    @Override
    public void fillRewardData(RewardHistoryResponse rewardHistoryResponse) {
        getP().refreshHot(1);
        RewardHistoryResponse.DataBean dataBean = rewardHistoryResponse.getData();
        List<RewardHistoryResponse.DataBean.RewardHistoryModel> data =
                dataBean.getList();
        //空数据
        if (data == null || CommonUtil.checkEmpty(data)) {
            nodata.setVisibility(View.VISIBLE);
            reward_recycler.setVisibility(View.GONE);
            already_text.setVisibility(View.GONE);
        } else {//有数据
            reward_recycler.setVisibility(View.VISIBLE);
            already_text.setVisibility(View.VISIBLE);
            nodata.setVisibility(View.GONE);
            mRewardAdapter.setNewData(data);

            mTvRewardCount.setText(getString(R.string.comic_reward_comic_text) + dataBean.getNum());
            mTvRewardCoins.setText(getString(R.string.comic_money_value_text) + dataBean.getActvalue());
        }
    }


    /**
     * 刷新失败
     *
     * @param error
     */
    @Override
    public void refreshFail(NetError error) {
        if (error.getType() == NetError.NullDataError) {
            page = 1;
            getP().refreshHot(page);
        } else {
            showToastShort(error.getMessage());
            page--;
        }
    }
}
