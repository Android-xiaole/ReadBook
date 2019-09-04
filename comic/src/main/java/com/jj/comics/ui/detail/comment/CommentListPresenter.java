package com.jj.comics.ui.detail.comment;

import com.jj.base.BaseApplication;
import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.R;
import com.jj.comics.data.biz.content.ContentRepository;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.CommentListResponse;
import com.jj.comics.data.model.CommonStatusResponse;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CommentListPresenter extends BasePresenter<BaseRepository, CommentListContract.ICommentListView> implements CommentListContract.ICommentListPresenter {

    /**
     * 获取评论列表数据
     */
    @Override
    public void getCommentData(int pageNum, int pageSize, long objectId) {
        getV().showProgress();
        ContentRepository.getInstance().getCommentListByContent(objectId, pageNum, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<CommentListResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<CommentListResponse>() {
                    @Override
                    public void onNext(CommentListResponse response) {
                        if (response.getData()!=null&&response.getData().getData()!=null){
                            getV().onGetCommentList(response.getData().getData());
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToastShort(error.getMessage());
                    }

                    @Override
                    protected void onEnd() {
                        getV().hideProgress();
                    }
                });
    }

    /**
     * 点赞或者取消点赞的接口
     */
    @Override
    public void favorComment(long id,final String type) {
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

}
