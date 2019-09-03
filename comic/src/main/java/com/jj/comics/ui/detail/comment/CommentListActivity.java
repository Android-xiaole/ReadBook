package com.jj.comics.ui.detail.comment;

import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.detail.CommentAdapter;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.db.DaoHelper;
import com.jj.comics.data.model.CommentListResponse;
import com.jj.comics.data.model.CommonStatusResponse;
import com.jj.comics.data.model.UserCommentFavorData;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.ui.dialog.CommentSendDialog;
import com.jj.comics.util.LoginHelper;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import anet.channel.util.StringUtils;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 查看全部评论列表的页面
 */
@Route(path = RouterMap.COMIC_COMMENTlIST_ACTIVITY)
public class CommentListActivity extends BaseActivity<CommentListPresenter> implements CommentListContract.ICommentListView,BaseQuickAdapter.OnItemChildClickListener {

    @BindView(R2.id.rv_comment)
    RecyclerView rv_comment;

    private CommentAdapter commentAdapter;//评论列表适配器
    private long bookId;
    private int pageNum = 1;//分页页码
    private int pageSize = 10;//分页大小
    private DaoHelper daoHelper = new DaoHelper();
    private int commentListPosition;//记录当前点赞位置

    private CommentSendDialog commentSendDialog;//发表评论弹窗

    @Override
    public void initData(Bundle savedInstanceState) {
        bookId = getIntent().getLongExtra(Constants.IntentKey.BOOK_ID, 0);

        rv_comment.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter(R.layout.comic_detail_comment_item);
        commentAdapter.setOnItemChildClickListener(this);
        rv_comment.setAdapter(commentAdapter);

        commentAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                pageNum++;
                getP().getCommentData(pageNum,pageSize, bookId);
            }
        },rv_comment);

        getP().getCommentData(pageNum,pageSize, bookId);
    }

    @OnClick(R2.id.fl_comment)
    public void onClick(View view){
        if (commentSendDialog == null){
            commentSendDialog = new CommentSendDialog(this,null);
            commentSendDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String content = commentSendDialog.getContent();
                    if (StringUtils.isBlank(content)) {
                        ToastUtil.showToastShort(getString(R.string.comic_comment_not_allow_null));
                    }else{
                        getP().sendComment(bookId,commentSendDialog.getContent());
                    }
                }
            });
        }
        commentSendDialog.show();
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_activity_commentlist;
    }

    @Override
    public CommentListPresenter setPresenter() {
        return new CommentListPresenter();
    }

    /**
     * 评论列表子view点击事件
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
                ARouter.getInstance().build(RouterMap.COMIC_LOGIN_ACTIVITY).navigation(this);
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

    /**
     * 获取评论列表的回调
     */
    @Override
    public void onGetCommentList(List<CommentListResponse.DataBeanX.DataBean> commentInfos){
        if (commentAdapter == null) return;
        if (commentInfos == null||commentInfos.size() == 0){
            commentAdapter.loadMoreEnd();
        }else{
            if (pageNum == 1){
                commentAdapter.setNewData(commentInfos);
            }else{
                commentAdapter.addData(commentInfos);
            }
            commentAdapter.loadMoreComplete();
        }
    }

    /**
     * 点赞成功的回调，用来刷新评论列表,只用刷新点赞的那一项就可以了
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
        setResult(RESULT_OK);
    }

    @Override
    public void onCommentSuccess(CommonStatusResponse result) {
        showToastShort(getString(R.string.comic_report_success));
        if (commentSendDialog != null) {
            commentSendDialog.clearInput();
            commentSendDialog.dismiss();
        }
        pageNum = 1;
        getP().getCommentData(pageNum,pageSize, bookId);
        setResult(RESULT_OK);
    }

}
