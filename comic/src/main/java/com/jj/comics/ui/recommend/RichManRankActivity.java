package com.jj.comics.ui.recommend;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterViewFlipper;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.gyf.barlibrary.ImmersionBar;
import com.jj.base.imageloader.ILFactory;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.recommend.RecentlyRewardAdapter;
import com.jj.comics.adapter.recommend.RichManRankAdapter;
import com.jj.comics.data.model.RichManModel;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;


/**
 * 土豪排行o
 */
@Route(path = RouterMap.COMIC_RICHMANRANK_ACTIVITY)
public class RichManRankActivity extends BaseActivity<RichManRankPresenter> implements RichManRankContract.IRichManRankView {

    @BindView(R2.id.rv_rankList)
    RecyclerView rv_rankList;//土豪排行列表
    private AdapterViewFlipper vf_rewardNow;
    private ImageView iv_num1,iv_num2,iv_num3;
    private TextView tv_num1,tv_num2,tv_num3;

    private RichManRankAdapter richManRankAdapter;//土豪排行适配器
    private RecentlyRewardAdapter recentlyRewardAdapter;//实时打赏滚动适配器

    @Override
    public void initData(Bundle savedInstanceState) {
        //土豪排行
        richManRankAdapter = new RichManRankAdapter(R.layout.comic_richmanrank_item);
        View view_head = View.inflate(this,R.layout.comic_richmanrank_head,null);

        vf_rewardNow = view_head.findViewById(R.id.vf_rewardNow);
        PropertyValuesHolder p1 = PropertyValuesHolder.ofFloat("translationX",900,0);
        PropertyValuesHolder p2 = PropertyValuesHolder.ofFloat("alpha",0.5f,1);
        ObjectAnimator anim_in = ObjectAnimator.ofPropertyValuesHolder(vf_rewardNow,p1,p2).setDuration(2000);
        anim_in.setInterpolator(new AccelerateDecelerateInterpolator());
        vf_rewardNow.setInAnimation(anim_in);
        PropertyValuesHolder p3 = PropertyValuesHolder.ofFloat("translationX",0,-900);
        PropertyValuesHolder p4 = PropertyValuesHolder.ofFloat("alpha",1,0.5f);
        ObjectAnimator anim_out = ObjectAnimator.ofPropertyValuesHolder(vf_rewardNow,p3,p4).setDuration(2000);
        anim_out.setInterpolator(new AccelerateDecelerateInterpolator());
        vf_rewardNow.setOutAnimation(anim_out);

        iv_num1 = view_head.findViewById(R.id.iv_num1);
        iv_num2 = view_head.findViewById(R.id.iv_num2);
        iv_num3 = view_head.findViewById(R.id.iv_num3);
        tv_num1 = view_head.findViewById(R.id.tv_num1);
        tv_num2 = view_head.findViewById(R.id.tv_num2);
        tv_num3 = view_head.findViewById(R.id.tv_num3);

        richManRankAdapter.setHeaderView(view_head);
        rv_rankList.setLayoutManager(new LinearLayoutManager(this));
        rv_rankList.setHasFixedSize(true);
        rv_rankList.setAdapter(richManRankAdapter);


        //实时打赏滚动
        recentlyRewardAdapter = new RecentlyRewardAdapter(null);
        vf_rewardNow.setAdapter(recentlyRewardAdapter);

        showProgress();
        getP().getRickManRankList();
        getP().getRewardNow(1);

    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_activity_richmanrank;
    }

    @Override
    public RichManRankPresenter setPresenter() {
        return new RichManRankPresenter();
    }

    /**
     * 获取实时打赏数据
     */
    @Override
    public void onGetRewardNowData(List<RichManModel> rewardRecordByAllUsers) {
        recentlyRewardAdapter.setNewData(rewardRecordByAllUsers);
    }

    @Override
    public void onFetchData(List<RichManModel> rewardTotalList) {
        String name1 = getString(R.string.comic_has_not_text);
        String name2 = getString(R.string.comic_has_not_text);
        String name3 = getString(R.string.comic_has_not_text);
        String coin1 = getString(R.string.comic_has_not_text);
        String coin2 = getString(R.string.comic_has_not_text);
        String coin3 = getString(R.string.comic_has_not_text);
        if (rewardTotalList != null) {
            if (rewardTotalList.size() == 0) {

            } else {
                String coin = String.format(getString(R.string.comic_coin_format), " ");
                if (rewardTotalList.size() == 1) {
                    name1 = rewardTotalList.get(0).getUsername();
                    coin1 = rewardTotalList.get(0).getTotal() + coin;
                    ILFactory.getLoader()
                            .loadNet(iv_num1, rewardTotalList.get(0).getAvatar(),
                                    new RequestOptions().transforms(new CenterCrop(), new CircleCrop())
                                            .error(R.drawable.icon_user_avatar_default));
                } else if (rewardTotalList.size() == 2) {
                    name1 = rewardTotalList.get(0).getUsername();
                    name2 = rewardTotalList.get(1).getUsername();
                    coin1 = rewardTotalList.get(0).getTotal() + coin;
                    coin2 = rewardTotalList.get(1).getTotal() + coin;
                    ILFactory.getLoader()
                            .loadNet(iv_num1, rewardTotalList.get(0).getAvatar(),
                                    new RequestOptions().transforms(new CenterCrop(), new CircleCrop())
                                            .error(R.drawable.icon_user_avatar_default));
                    ILFactory.getLoader()
                            .loadNet(iv_num2, rewardTotalList.get(1).getAvatar(),
                                    new RequestOptions().transforms(new CenterCrop(), new CircleCrop())
                                            .error(R.drawable.icon_user_avatar_default));

                } else if (rewardTotalList.size() == 3) {
                    name1 = rewardTotalList.get(0).getUsername();
                    name2 = rewardTotalList.get(1).getUsername();
                    name3 = rewardTotalList.get(2).getUsername();
                    coin1 = rewardTotalList.get(0).getTotal() + coin;
                    coin2 = rewardTotalList.get(1).getTotal() + coin;
                    coin3 = rewardTotalList.get(2).getTotal() + coin;

                    ILFactory.getLoader()
                            .loadNet(iv_num1, rewardTotalList.get(0).getAvatar(),
                                    new RequestOptions().transforms(new CenterCrop(), new CircleCrop())
                                            .error(R.drawable.icon_user_avatar_default));

                    ILFactory.getLoader()
                            .loadNet(iv_num2, rewardTotalList.get(1).getAvatar(),
                                    new RequestOptions().transforms(new CenterCrop(), new CircleCrop())
                                            .error(R.drawable.icon_user_avatar_default));

                    ILFactory.getLoader()
                            .loadNet(iv_num3, rewardTotalList.get(2).getAvatar(),
                                    new RequestOptions().transforms(new CenterCrop(), new CircleCrop())
                                            .error(R.drawable.icon_user_avatar_default));
                } else {
                    name1 = rewardTotalList.get(0).getUsername();
                    name2 = rewardTotalList.get(1).getUsername();
                    name3 = rewardTotalList.get(2).getUsername();
                    coin1 = rewardTotalList.get(0).getTotal() + coin;
                    coin2 = rewardTotalList.get(1).getTotal() + coin;
                    coin3 = rewardTotalList.get(2).getTotal() + coin;

                    ILFactory.getLoader()
                            .loadNet(iv_num1, rewardTotalList.get(0).getAvatar(),
                                    new RequestOptions().transforms(new CenterCrop(), new CircleCrop())
                                            .error(R.drawable.icon_user_avatar_default));

                    ILFactory.getLoader()
                            .loadNet(iv_num2, rewardTotalList.get(1).getAvatar(),
                                    new RequestOptions().transforms(new CenterCrop(), new CircleCrop())
                                            .error(R.drawable.icon_user_avatar_default));

                    ILFactory.getLoader()
                            .loadNet(iv_num3, rewardTotalList.get(2).getAvatar(),
                                    new RequestOptions().transforms(new CenterCrop(), new CircleCrop())
                                            .error(R.drawable.icon_user_avatar_default));

                    richManRankAdapter.setNewData(rewardTotalList.subList(3,
                            rewardTotalList.size()));
                }
            }
        }
        tv_num1.setText(name1);
        tv_num2.setText(name2);
        tv_num3.setText(name3);
    }

    @Override
    protected void initImmersionBar() {
        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.comic_fecc34)
//                .navigationBarColor(R.color.comic_ffffff)
                .statusBarDarkFont(true, 0.2f)
                .init();
    }
}
