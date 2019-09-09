package com.jj.comics.ui.detail.subdetail;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseVPFragment;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.Utils;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.detail.CommentAdapter;
import com.jj.comics.adapter.detail.RewardRecordByCotentAdapter;
import com.jj.comics.adapter.detail.CommonRecommendAdapter;
import com.jj.comics.common.callback.GlideOnScrollListener;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.constants.RequestCode;
import com.jj.comics.data.db.DaoHelper;
import com.jj.comics.data.model.BookListDataResponse;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.CommentListResponse;
import com.jj.comics.data.model.CommonStatusResponse;
import com.jj.comics.data.model.RewardListResponse;
import com.jj.comics.data.model.UserCommentFavorData;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.ui.detail.ComicDetailActivity;
import com.jj.comics.ui.detail.DetailActivityHelper;
import com.jj.comics.ui.dialog.CommentSendDialog;
import com.jj.comics.ui.dialog.RewardDialog;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.eventbus.events.RefreshComicFavorStatusEvent;
import com.jj.comics.util.eventbus.events.RefreshRewardRecordListEvent;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.UnknownFormatConversionException;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import anet.channel.util.StringUtils;
import butterknife.BindView;

import static android.app.Activity.RESULT_OK;

@Route(path = RouterMap.COMIC_DETAIL_FRAGMENT)
public class SubComicDetailFragment extends BaseVPFragment<SubComicDetailPresenter> implements SubComicDetailContract.ISubComicDetailView
        , BaseQuickAdapter.OnItemChildClickListener{

    private TextView mTextDes;//漫画简介的描述
    private TextView tv_commentCount;//评论数量
    private TextView tv_favorCount;//点赞数量
    private TextView tv_fireCount;//人气数量
    private TextView tv_lookMore;//查看更多评论

    @BindView(R2.id.lin_root)
    LinearLayout lin_root;//页面根布局
    @BindView(R2.id.comic_detail_recycler)
    RecyclerView rv_likeComic;//底部的猜你喜欢列表

    private RecyclerView rv_comment;//评论列表
    private RecyclerView rv_reward;//土豪打赏列表

    private CommonRecommendAdapter mAdapter;//猜你喜欢列表适配器
    private CommentAdapter commentAdapter;//评论列表适配器
    private RewardRecordByCotentAdapter rewardRecordByCotentAdapter;//打赏适配器
    private int type;//请求猜你喜欢的type(cid)
    private long id;//漫画主键id
    private int page = 1;//猜你喜欢的分页

    private RewardDialog rewardDialog;//打赏弹窗
    private CommentSendDialog commentSendDialog;//发表评论弹窗
    private BookModel model;
    private int commentListPosition;//记录用户点击点赞列表的位置
    private DaoHelper daoHelper = new DaoHelper();

    @Override
    public int getLayoutId() {
        return R.layout.comic_fragment_detail;
    }

    @Override
    public SubComicDetailPresenter setPresenter() {
        return new SubComicDetailPresenter();
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        model = (BookModel) getArguments().getSerializable(Constants.IntentKey.BUNDLE);
        rv_likeComic.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rv_likeComic.addOnScrollListener(new GlideOnScrollListener());
        mAdapter = new CommonRecommendAdapter(R.layout.comic_item_search_watchingcomicdata);
        mAdapter.bindToRecyclerView(rv_likeComic, true);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (getActivity() instanceof ComicDetailActivity && id != mAdapter.getData().get(position).getId()) {
                    DetailActivityHelper.toDetail(getActivity(), mAdapter.getData().get(position).getId(),
                            "详情页_推荐");
                    getActivity().finish();
                }
            }
        });
        mAdapter.setHeaderAndEmpty(true);
        mAdapter.setEmptyClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page = 1;
                showProgress();
                getP().loadLikeComicList(model.getId(),page, type);
            }
        });
        mAdapter.addHeaderView(getHeadView());

        refresh(model);

        /*
          这里需要设置一个根布局，让其一直获取焦点，如果直接用recyclerview做根布局，
          当viewpager在切换或者点击刷新的时候会让recyclerview频繁获取焦点导致界面一直滑动到顶部，用户体验比较差
         */
        lin_root.setFocusable(true);
        lin_root.setFocusableInTouchMode(true);
        lin_root.requestFocus();
    }

    private View getHeadView() {
        View view = getLayoutInflater().inflate(R.layout.comic_like_head, (ViewGroup) rv_likeComic.getParent(), false);
        if (mTextDes == null) mTextDes = view.findViewById(R.id.comic_detail_des);
        tv_commentCount = view.findViewById(R.id.tv_commentCount);
        tv_favorCount = view.findViewById(R.id.tv_favorCount);
        tv_fireCount = view.findViewById(R.id.tv_fireCount);
        tv_lookMore = view.findViewById(R.id.tv_lookMore);

        //土豪打赏
        rv_reward = view.findViewById(R.id.rv_reward);
        rv_reward.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rv_reward.setNestedScrollingEnabled(false);
        rewardRecordByCotentAdapter = new RewardRecordByCotentAdapter(R.layout.comic_detail_rewardlist_item);
        rewardRecordByCotentAdapter.setEmptyView(R.layout.comic_detail_reward_emptyview, rv_reward);
        rv_reward.setAdapter(rewardRecordByCotentAdapter);
        View emptyView = rewardRecordByCotentAdapter.getEmptyView();
        //土豪排行空布局打赏点击事件
        emptyView.findViewById(R.id.btn_reward).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginHelper.getOnLineUser() == null) {
                    ARouter.getInstance().build(RouterMap.COMIC_LOGIN_ACTIVITY).navigation(getActivity());
                    return;
                }
                if (model == null)return;
                MobclickAgent.onEvent(getActivity(), Constants.UMEventId.REWARD_DETAIL, model.getId() + " : " + model.getTitle());
                if (rewardDialog == null)
                    rewardDialog = new RewardDialog(getBaseActivity(), null,model.getId());
                rewardDialog.show();
            }
        });

        //精彩评论
        rv_comment = view.findViewById(R.id.rv_comment);
        rv_comment.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_comment.setNestedScrollingEnabled(false);
        commentAdapter = new CommentAdapter(R.layout.comic_detail_comment_item);
        commentAdapter.setOnItemChildClickListener(this);
        commentAdapter.setEmptyView(R.layout.comic_detail_comment_emptyview, rv_comment);
        rv_comment.setAdapter(commentAdapter);
        commentAdapter.getEmptyView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginHelper.getOnLineUser() == null) {
                    ARouter.getInstance().build(RouterMap.COMIC_LOGIN_ACTIVITY).navigation(getActivity());
                    return;
                }
                if (model == null)return;
                MobclickAgent.onEvent(getContext(), Constants.UMEventId.COMMENT_DETAIL, model.getId() + " : " + model.getTitle());
                if (commentSendDialog == null){
                    commentSendDialog = new CommentSendDialog(getActivity(),null);
                    commentSendDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String content = commentSendDialog.getContent();
                            if (StringUtils.isBlank(content)) {
                                ToastUtil.showToastShort(getString(R.string.comic_comment_not_allow_null));
                            }else{
                                getP().sendComment(model.getId(),commentSendDialog.getContent());
                            }
                        }
                    });
                }
                commentSendDialog.show();
            }
        });
        view.findViewById(R.id.comic_detail_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page++;
                showProgress();
                getP().loadLikeComicList(model.getId(),page, type);
            }
        });
        //发表评论点击事件
        view.findViewById(R.id.tv_toComment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginHelper.getOnLineUser() == null) {
                    ARouter.getInstance().build(RouterMap.COMIC_LOGIN_ACTIVITY).navigation(getActivity());
                    return;
                }
                if (model == null)return;
                MobclickAgent.onEvent(getContext(), Constants.UMEventId.COMMENT_DETAIL, model.getId() + " : " + model.getTitle());
                if (commentSendDialog == null){
                    commentSendDialog = new CommentSendDialog(getActivity(),null);
                    commentSendDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String content = commentSendDialog.getContent();
                            if (StringUtils.isBlank(content)) {
                                ToastUtil.showToastShort(getString(R.string.comic_comment_not_allow_null));
                            }else{
                                getP().sendComment(model.getId(),commentSendDialog.getContent());
                            }
                        }
                    });
                }
                commentSendDialog.show();
            }
        });
        //查看更多评论点击事件
        tv_lookMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(RouterMap.COMIC_COMMENTlIST_ACTIVITY)
                        .withLong(Constants.IntentKey.BOOK_ID, id)
                        .navigation(getActivity(), RequestCode.COMMENT_REQUEST_CODE);
            }
        });
        return view;
    }

    /**
     * 刷新当前界面需要展示的数据，三个接口集中调用的地方
     *
     * @param model
     */
    public void refresh(BookModel model) {
        page = 1;
        type = model.getCategory_id().get(0);
        id = model.getId();
        if (mTextDes != null){
            try {
                mTextDes.setText(String.format(getString(R.string.comic_comic_desc) + Html.fromHtml(model.getIntro().replace("%", "%%"))));
            } catch (UnknownFormatConversionException e) {
                e.printStackTrace();
                mTextDes.setText(String.format(getString(R.string.comic_comic_desc) + model.getIntro()));
            }
            tv_commentCount.setText("评论 "+Utils.convertUnit(model.getReviewnum()));
            tv_favorCount.setText("点赞 "+Utils.convertUnit(model.getGoodnum()));
            tv_fireCount.setText("人气 "+model.getHot_const());
        }

        //获取打赏排行列表
        getP().getRewardRecordList(model.getId());
        //获取评论列表
        getP().getCommentData(model.getId());
        //获取猜你喜欢的漫画列表
        getP().loadLikeComicList(model.getId(),page, type);
    }

    /**
     * 获取当前内容打赏列表数据回调
     *
     */
    @Override
    public void onGetRewardRecordList(List<RewardListResponse.DataBean.RewardRecordBean> rewardRecords) {
        if (rewardRecordByCotentAdapter == null) return;
        rewardRecordByCotentAdapter.setNewData(rewardRecords);
    }

    /**
     * 获取评论列表数据回调
     */
    @Override
    public void onGetCommentData(CommentListResponse commentListResponse) {
        if (commentAdapter == null) return;
        if (commentListResponse != null && commentListResponse.getData() != null && commentListResponse.getData().getData()!=null) {
            commentAdapter.setNewData(commentListResponse.getData().getData());
            if (commentListResponse.getData().getData().size() >= 5){
                tv_lookMore.setVisibility(View.VISIBLE);
            }else{
                tv_lookMore.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 获取猜你喜欢漫画列表 成功的回调
     */
    @Override
    public void onLoadLikeComicList(BookListDataResponse response) {
        if (page >= response.getData().getTotal_num()){
            page = 0;
        }
        if (mAdapter!=null)mAdapter.setNewData(response.getData().getData());
    }

    /**
     * 获取猜你喜欢漫画列表 失败的回调
     *
     * @param error
     */
    @Override
    public void onLoadLikeComicListFail(NetError error) {
        if (page != 1){
            page--;
        }
    }

    /**
     * 点赞或者取消点赞成功的回调，用来刷新评论列表,只用刷新点赞的那一项就可以了
     * 此外还需要更新本地数据库保存用户点赞评论的数据
     */
    @Override
    public void onFavorCommentSuccess(String type) {
        CommentListResponse.DataBeanX.DataBean dataBean = commentAdapter.getData().get(commentListPosition);
        UserCommentFavorData favorData = daoHelper.getUserCommentFavorDataById(LoginHelper.getOnLineUser().getUid(), dataBean.getId());
        if (favorData!=null){
            favorData.setUpdate_time(System.currentTimeMillis());
        }else{
            favorData = new UserCommentFavorData();
            favorData.setUserId(LoginHelper.getOnLineUser().getUid());
            favorData.setCommentId(dataBean.getId());
            favorData.setContent(dataBean.getContent());
            favorData.setCreate_time(System.currentTimeMillis());
        }
        switch (type){
            case Constants.RequestBodyKey.COMMENT_TYPE_ADD://点赞
                favorData.setIsFavor(true);
                dataBean.setGoodnum(dataBean.getGoodnum()+1);
                break;
            case Constants.RequestBodyKey.COMMENT_TYPE_SUB://取消点赞
                favorData.setIsFavor(false);
                dataBean.setGoodnum(dataBean.getGoodnum()-1);
                break;
        }
        daoHelper.insertOrUpdateUserCommentFavorData(favorData);
        commentAdapter.notifyItemChanged(commentListPosition);
    }

    @Override
    public void onCommentSuccess(CommonStatusResponse response) {
        showToastShort(getString(R.string.comic_report_success));
        if (commentSendDialog != null) {
            commentSendDialog.clearInput();
            commentSendDialog.dismiss();
        }
        getP().getCommentData(model.getId());
        getP().getComicDetail(id);
    }

    @Override
    public void onLoadComicDetail(BookModel model) {
        tv_commentCount.setText("评论 "+model.getReviewnum());
        tv_favorCount.setText("点赞 "+model.getGoodnum());
        tv_fireCount.setText("人气 "+model.getHot_const());
    }


    /**
     * 评论列表的子view点击事件
     *
     * @param adapter
     * @param view
     * @param position
     */
    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, final View view, final int position) {
        if (view.getId() == R.id.iv_thumbUp) {
            commentListPosition = position;
            UserInfo loginUser = LoginHelper.getOnLineUser();
            if (loginUser == null) {
                //未登录不允许点赞 直接跳转到登录界面
                ARouter.getInstance().build(RouterMap.COMIC_LOGIN_ACTIVITY)
                        .navigation(getActivity());
                return;
            } else {
                CommentListResponse.DataBeanX.DataBean dataBean = commentAdapter.getData().get(position);
                UserCommentFavorData favorData = daoHelper.getUserCommentFavorDataById(loginUser.getUid(),dataBean.getId());
                if (favorData!=null&&favorData.getIsFavor()){
                    //用户已经点赞就去执行取消点赞
                    getP().favorComment(dataBean.getId(),Constants.RequestBodyKey.COMMENT_TYPE_SUB);
                }else{
                    //用户未点赞就去执行点赞
                    getP().favorComment(dataBean.getId(),Constants.RequestBodyKey.COMMENT_TYPE_ADD);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RequestCode.LOGIN_REQUEST_CODE:
                    if (model == null)return;
                    getP().getCommentData(model.getId());
                    getP().getRewardRecordList(model.getId());
                    break;
                case RequestCode.READ_REQUEST_CODE:
                    //默认是刷新评论
                    int result_state = data == null ? RequestCode.COMMENT_REQUEST_CODE : data.getIntExtra(Constants.IntentKey.RESULT_STATE, RequestCode.COMMENT_REQUEST_CODE);
                    if (result_state == RequestCode.COMMENT_REQUEST_CODE) {
                        getP().getCommentData(id);
                        getP().getComicDetail(id);
                    } else if (result_state == RequestCode.REWARD_REQUEST_CODE) {
                        getP().getRewardRecordList(id);
                    }
                    break;
                case RequestCode.COMMENT_REQUEST_CODE:
                    getP().getCommentData(id);
                    break;
            }
        }

    }

    /**
     * 接收打赏成功后,刷新打赏列表的通知
     *
     * @param refreshRewardRecordListEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshRewardRecordList(RefreshRewardRecordListEvent refreshRewardRecordListEvent) {
        getP().getRewardRecordList(id);
    }

    /**
     * 点赞成功的通知，此时要调用获取漫画详情接口，刷新对应数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshFavorCount(RefreshComicFavorStatusEvent event) {
        getP().getComicDetail(id);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (rewardDialog != null) rewardDialog.release();
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

}
