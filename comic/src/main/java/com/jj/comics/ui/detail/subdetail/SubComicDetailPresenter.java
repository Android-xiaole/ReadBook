package com.jj.comics.ui.detail.subdetail;


import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.data.biz.content.ContentRepository;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.BookListDataResponse;
import com.jj.comics.data.model.BookModelResponse;
import com.jj.comics.data.model.CommentListResponse;
import com.jj.comics.data.model.CommonStatusResponse;
import com.jj.comics.data.model.RewardListResponse;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SubComicDetailPresenter extends BasePresenter<BaseRepository, SubComicDetailContract.ISubComicDetailView> implements SubComicDetailContract.ISubComicDetailPresenter {

    /**
     * 获取猜你喜欢的列表
     */
    @Override
    public void loadLikeComicList(long id, int pageNum, int sectionId) {
        ContentRepository.getInstance().getLikeBookList(id, pageNum, sectionId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<BookListDataResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<BookListDataResponse>() {
                    @Override
                    public void onNext(BookListDataResponse response) {
                        if (response.getData() != null && response.getData().getData() != null) {
                            getV().onLoadLikeComicList(response);
                        } else {
                            onFail(new NetError(response.getMessage(), response.getCode()));
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().onLoadLikeComicListFail(error);
                    }

                    @Override
                    protected void onEnd() {
                        getV().hideProgress();
                    }
                });

    }

    /**
     * 获取评论列表数据
     */
    @Override
    public void getCommentData(long id) {
        ContentRepository.getInstance().getCommentListByContent(id, 1, 5)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<CommentListResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<CommentListResponse>() {
                    @Override
                    public void onNext(CommentListResponse commentListResponse) {
                        getV().onGetCommentData(commentListResponse);
                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToastShort(error.getMessage());
                    }
                });
    }

    /**
     * 获取内容打赏排行列表
     */
    @Override
    public void getRewardRecordList(long id) {
        ContentRepository.getInstance().getRewardRecordByContent(id , 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<RewardListResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<RewardListResponse>() {
                    @Override
                    public void onNext(RewardListResponse response) {
                        if (response.getData()!=null&&response.getData().getData()!=null){
                            getV().onGetRewardRecordList(response.getData().getData());
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToastShort(error.getMessage());
                    }
                });
    }

    /**
     * 点赞或者取消点赞评论的接口
     */
    @Override
    public void favorComment(long id, final String type) {
        getV().showProgress();
        UserRepository.getInstance().favorComment(id, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<CommonStatusResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<CommonStatusResponse>() {
                    @Override
                    public void onNext(CommonStatusResponse response) {
                        if (response.getData().getStatus()){
                            getV().onFavorCommentSuccess(type);
                        }else{
                            ToastUtil.showToastShort(response.getMessage());
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToastShort(error.getMessage());
                    }

                    @Override
                    protected void onEnd() {
                        super.onEnd();
                        getV().hideProgress();
                    }
                });
    }

    @Override
    public void sendComment(long bookId, String commentDetail) {
        getV().showProgress();
        ContentRepository.getInstance().sendComment(bookId, commentDetail)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<CommonStatusResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<CommonStatusResponse>() {
                    @Override
                    public void onNext(CommonStatusResponse result) {
                        if (result.getData().getStatus()) {
                            getV().onCommentSuccess(result);
                        } else {
                            getV().showToastShort(result.getMessage());
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().showToastShort(error.getMessage());
                    }

                    @Override
                    protected void onEnd() {
                        getV().hideProgress();
                    }
                });

    }

    @Override
    public void getComicDetail(long id) {
        ContentRepository.getInstance().getContentDetail(id, getV().getClass().getName())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<BookModelResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<BookModelResponse>() {
                    @Override
                    public void onNext(final BookModelResponse response) {
                        getV().onLoadComicDetail(response.getData());
                    }

                    @Override
                    protected void onFail(NetError error) {
                    }
                });

    }
}
