package com.jj.comics.ui.mine;

import com.jj.base.log.LogUtil;
import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.comics.data.biz.content.ContentRepository;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.BookListResponse;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.RewardHistoryResponse;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 我的打赏数据层
 */
public class RewardPresenter extends BasePresenter<BaseRepository, RewardContract.IRewardView> implements RewardContract.IRewardPresenter {

    /**
     * 猜你喜欢
     *
     * @param page
     */
    @Override
    public void refreshHot(int page) {
        getGuessLikeSearch()
                .as(this.<BookListResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<BookListResponse>() {
                    @Override
                    public void onNext(BookListResponse bookListResponse) {
                        List<BookModel> bookModelList = bookListResponse.getData();
                        getV().fillHotData(bookModelList);
                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().refreshFail(error);
                    }

                    @Override
                    protected void onEnd() {
                        super.onEnd();
                        getV().hideProgress();
                    }
                });
    }


    /**
     * 猜你喜欢请求
     * @return
     */
    private Observable<BookListResponse> getGuessLikeSearch() {
        return ContentRepository.getInstance().getLikeBookList(getV().getClass().getName())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取打赏记录
     *
     * @param evict
     */
    @Override
    public void getRewardsHistory(boolean evict) {

        UserRepository.getInstance().getRewardsRecord(getV().getClass().getName())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<RewardHistoryResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<RewardHistoryResponse>() {
                    @Override
                    public void onNext(RewardHistoryResponse rewards) {
                        getV().fillRewardData(rewards);
                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().hideProgress();
                        LogUtil.d("error", error.getMessage());
                    }
                });
    }

    @Override
    public boolean useEventBus() {
        return false;
    }
}
