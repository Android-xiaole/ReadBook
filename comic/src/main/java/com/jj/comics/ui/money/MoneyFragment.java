package com.jj.comics.ui.money;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jj.base.imageloader.ILFactory;
import com.jj.base.log.LogUtil;
import com.jj.base.ui.BaseCommonFragment;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.SharedPref;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.money.ShareRecommendAdapter;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.PayInfo;
import com.jj.comics.data.model.ShareMessageModel;
import com.jj.comics.data.model.ShareRecommendResponse;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.ui.dialog.ShareDialog;
import com.jj.comics.util.LoginHelper;

import java.util.List;

import butterknife.BindView;


@Route(path = RouterMap.COMIC_MONEY_FRAGMENT)
public class MoneyFragment extends BaseCommonFragment<MoneyPresenter> implements MoneyContract.IMoneyView {
//    @BindView(R2.id.vf_apprentice_now)
//    AdapterViewFlipper mViewFlipper;
//
//    private RecentlyApprenticeAdapter mApprenticeAdapter;

    @BindView(R2.id.iv_money_detail)
    ImageView mIvDetail;
    @BindView(R2.id.iv_money_user_info_avatar)
    ImageView mIvAvatar;
    @BindView(R2.id.tv_money_user_info_name)
    TextView mTvName;
    @BindView(R2.id.tv_money_user_info_total_money)
    TextView mTvMoney;
    @BindView(R2.id.tv_money_user_info_total_apprentice)
    TextView mTvApprentice;
    @BindView(R2.id.btn_invite)
    Button mBtnInvite;
    @BindView(R2.id.rv_money_recommend)
    RecyclerView mRecyclerView;
    @BindView(R2.id.cl_user_info)
    ConstraintLayout mClUserInfo;
    @BindView(R2.id.tv_title_t1)
    TextView tv_title_t1;
    @BindView(R2.id.iv_apprentice_help)
    ImageView iv_apprentice_help;
    @BindView(R2.id.iv_btn_down)
    ImageView iv_btn_down;
    @BindView(R2.id.iv_bg_header)
    ImageView iv_bg_header;
    @BindView(R2.id.tv_title_t2)
    TextView tv_title_t2;
    @BindView(R2.id.tv_t1)
    TextView tv_t1;
    private ShareRecommendAdapter mAdapter;
    private ShareDialog mDialog;
    private PayInfo mPayInfo;
    private boolean isVisible = true;

    @Override
    public void initData(Bundle savedInstanceState) {

        //滚动显示提现记录，本期不显示
//        PropertyValuesHolder p1 = PropertyValuesHolder.ofFloat("translationX",900,0);
//        PropertyValuesHolder p2 = PropertyValuesHolder.ofFloat("alpha",0.5f,1);
//        ObjectAnimator anim_in = ObjectAnimator.ofPropertyValuesHolder(mViewFlipper,p1,p2).setDuration(2000);
//        anim_in.setInterpolator(new AccelerateDecelerateInterpolator());
//        mViewFlipper.setInAnimation(anim_in);
//        PropertyValuesHolder p3 = PropertyValuesHolder.ofFloat("translationX",0,-900);
//        PropertyValuesHolder p4 = PropertyValuesHolder.ofFloat("alpha",1,0.5f);
//        ObjectAnimator anim_out = ObjectAnimator.ofPropertyValuesHolder(mViewFlipper,p3,p4).setDuration(2000);
//        anim_out.setInterpolator(new AccelerateDecelerateInterpolator());
//        mViewFlipper.setOutAnimation(anim_out);
//
//        //实时打赏滚动
//        mApprenticeAdapter = new RecentlyApprenticeAdapter(null);
//        mViewFlipper.setAdapter(mApprenticeAdapter);
        getP().getShow();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ShareRecommendAdapter(R.layout.comic_item_share_recommend);
        mAdapter.bindToRecyclerView(mRecyclerView, false);


        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            private ShareMessageModel shareMessageModel;

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                if (!LoginHelper.interruptLogin(getBaseActivity(), null)) return;

                ShareRecommendResponse.DataBean model =
                        (ShareRecommendResponse.DataBean) adapter.getData().get(position);

                if (mDialog == null) {
                    mDialog = new ShareDialog(getBaseActivity(), "赚-内容推荐", model.getTitle());
                }

                if (shareMessageModel == null) shareMessageModel = new ShareMessageModel();
                shareMessageModel.setShareTitle(String.format(getString(R.string.comic_share_title), model.getTitle()));
                String shareContent = model.getIntro().trim().length() == 0 ? getString(R.string.comic_null_abstract) : String.format(getString(R.string.comic_comic_desc) + Html.fromHtml(model.getIntro()));
                shareMessageModel.setShareContent(shareContent);
                shareMessageModel.setBookTitle(model.getTitle());
                shareMessageModel.setAuthor(model.getAuthor());
                shareMessageModel.setShareImgUrl(model.getCover());
                UserInfo loginUser = LoginHelper.getOnLineUser();
                if (loginUser == null) {
                    ARouter.getInstance().build(RouterMap.COMIC_LOGIN_ACTIVITY).navigation(getBaseActivity());
                    return;
                }
                String uid = loginUser == null ? "0" : loginUser.getUid() + "";
                long chapterid = model.getChapterid();
                String shareUrl = SharedPref.getInstance().getString(Constants.ShareCode.SHARE_BASE_URL,Constants.CONTENT_URL) + "download.html?" + "uid=" + uid + "&cid=" + Constants.CHANNEL_ID + "&pid=" + Constants.PRODUCT_CODE + "&book_id=" + model.getId() + "&chapter_id=" + chapterid + "&invite_code=" + loginUser.getInvite_code();
                shareMessageModel.setShareUrl(shareUrl);
                shareMessageModel.setBoolId(model.getId());
                BookModel bookModel = new BookModel();
                bookModel.setId(model.getId());
                bookModel.setTitle(model.getTitle());
                bookModel.setCover(model.getCover());
                bookModel.setAuthor(model.getAuthor());
                bookModel.setKeywords(model.getKeywords());
                bookModel.setTag(model.getTag());
                mDialog.show(bookModel);
            }
        });

        mBtnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!LoginHelper.interruptLogin(getBaseActivity(), null)) return;
                if (mDialog == null) mDialog = new ShareDialog(getBaseActivity(), "赚", "");
                mDialog.show();
            }
        });

        mTvMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPayInfo != null)
                    ARouter.getInstance().build(RouterMap.COMIC_MY_REBATE_ACTIVITY).withSerializable(Constants.IntentKey.PAY_INFO, mPayInfo).navigation();
            }
        });

        mTvApprentice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ARouter.getInstance().build(RouterMap.COMIC_MINE_APPRENTICE_ACTIVITY).navigation();
            }
        });
    }

    private void updateInfo() {
        UserInfo userInfo = LoginHelper.getOnLineUser();
        if (userInfo != null && isVisible) {
            mClUserInfo.setVisibility(View.VISIBLE);

            RequestOptions options = new RequestOptions();
            options.circleCrop();
            ILFactory.getLoader().loadNet(mIvAvatar, userInfo.getAvatar(),
                    options);
            mTvName.setText(userInfo.getNickname());

        } else {
            mClUserInfo.setVisibility(View.GONE);
        }

        RequestOptions options = new RequestOptions();
        options.encodeQuality(100);
        ILFactory.getLoader().loadNet(mIvDetail, "http://fanli.jjmh668.cn/prd/apprentice.png",
                options);

        getP().getUserPayInfo();

        getP().getShareRecommend();
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_fragment_money;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        LogUtil.d("HIDDEN：" + hidden);
        super.onHiddenChanged(hidden);
        if (!hidden) {
            updateInfo();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateInfo();
    }

    @Override
    public MoneyPresenter setPresenter() {
        return new MoneyPresenter();
    }

    @Override
    public void onGetUserPayInfo(PayInfo payInfo) {
        mPayInfo = payInfo;
        if (LoginHelper.getOnLineUser() != null) {
            mTvMoney.setText(payInfo.getTotal_rebate_amount() + "");
            mTvApprentice.setText(payInfo.getDisciple_num() + "");
        }
    }

    @Override
    public void setShow(boolean isShow) {
        isVisible = isShow;
        if (isShow) {
            if (LoginHelper.getOnLineUser() != null) {
                mClUserInfo.setVisibility(View.VISIBLE);
            } else {
                mClUserInfo.setVisibility(View.GONE);
            }
            tv_title_t1.setText("如何收徒");
            mBtnInvite.setText("立即邀请赚钱");
            iv_apprentice_help.setBackgroundResource(R.drawable.img_apprentice_help);
            iv_bg_header.setBackgroundResource(R.drawable.bg_money_header);
            iv_apprentice_help.setVisibility(View.VISIBLE);
            iv_bg_header.setVisibility(View.VISIBLE);
            tv_title_t1.setVisibility(View.VISIBLE);
            mBtnInvite.setVisibility(View.VISIBLE);
            tv_title_t2.setVisibility(View.VISIBLE);
            tv_t1.setVisibility(View.VISIBLE);
            iv_btn_down.setVisibility(View.VISIBLE);
            mIvDetail.setVisibility(View.VISIBLE);
        } else {
            mClUserInfo.setVisibility(View.GONE);
            tv_title_t1.setText("如何邀请");
            mBtnInvite.setText("立即邀请");
            iv_apprentice_help.setBackgroundResource(R.drawable.img_apprentice_help_share);
            iv_bg_header.setBackgroundResource(R.drawable.bg_money_header_share);
            iv_apprentice_help.setVisibility(View.VISIBLE);
            iv_bg_header.setVisibility(View.VISIBLE);
            tv_title_t1.setVisibility(View.VISIBLE);
            mBtnInvite.setVisibility(View.VISIBLE);
            tv_title_t2.setVisibility(View.GONE);
            tv_t1.setVisibility(View.GONE);
            iv_btn_down.setVisibility(View.GONE);
            mIvDetail.setVisibility(View.GONE);
        }
    }

    @Override
    public void onGetShareRecommend(List<ShareRecommendResponse.DataBean> list) {
        mAdapter.setNewData(list);
    }
}
