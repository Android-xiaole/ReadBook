package com.jj.comics.ui.mine.userinfo;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.CommentListResponse;
import com.jj.comics.data.model.ResponseModel;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class MyCommentsPresenter extends BasePresenter<BaseRepository, MyCommentsContract.IMyCommentsView> implements MyCommentsContract.IMyCommentsPresenter {

    @Override
    public void getCommentsList(long userId, int pageNum, int pageSize) {

        UserRepository.getInstance().getCommentsRecordByUser(getV().getClass().getName(), userId, pageNum, pageSize)
                .observeOn(AndroidSchedulers.mainThread())
//                .as(AutoDispose.<CommentModel>autoDisposable(AndroidLifecycleScopeProvider.from(getV().getLifecycle())))
                .as(this.<CommentListResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<CommentListResponse>() {
                    @Override
                    public void onNext(CommentListResponse commentListResponse) {
//                        List<CommentListResponse.CommentInfosBean> commentInfos = commentListResponse.getCommentInfos();
//                        getV().onNewData(commentInfos);
                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().onDataErr(error.getMessage());
                    }
                });
    }

    @Override
    public void deleteCommentById(long userId, int commentId) {
        UserRepository.getInstance().deleteCommentByIds(getV().getClass().getName(), userId, commentId)
                .observeOn(AndroidSchedulers.mainThread())
//                .compose(getV().<ResponseModel>bindToLifecycle())
//                .as(AutoDispose.<ResponseModel>autoDisposable(AndroidLifecycleScopeProvider.from(getV().getLifecycle())))
                .as(this.<ResponseModel>bindLifecycle())
                .subscribe(new ApiSubscriber2<ResponseModel>() {
                    @Override
                    public void onNext(ResponseModel resp) {
                        getV().onCommentDelete(resp);
                    }

                    @Override
                    protected void onFail(NetError error) {
                        error.getMessage();
                    }
                });
    }

}
