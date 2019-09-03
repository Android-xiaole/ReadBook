package com.jj.comics.ui.recommend;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.data.biz.content.ContentRepository;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.BookListDataResponse;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.CommonStatusResponse;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FreeComicPresenter extends BasePresenter<BaseRepository, FreeComicContract.IRecentlyView> implements FreeComicContract.IRecentlyPresenter{
    private static final int RECOMMEND_PAGE_SIZE = 10;

    @Override
    public void loadData(int pageNum,String type) {
        ContentRepository.getInstance()
                .getForFree(pageNum, RECOMMEND_PAGE_SIZE,type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiSubscriber2<BookListDataResponse>() {
                    @Override
                    public void onNext(BookListDataResponse response) {
                        getV().fillData(response.getData().getData());
                    }

                    @Override
                    protected void onFail(NetError error) {

                    }
                });
    }

    @Override
    public void collect(final BookModel bookModel){
        UserRepository.getInstance().collect(bookModel.getId(), getV().getClass().getName())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<CommonStatusResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<CommonStatusResponse>() {
                    @Override
                    public void onNext(CommonStatusResponse response) {
                        if (response.getData().getStatus()) {
                            bookModel.setIs_collect(true);
                            getV().collectSuccess(bookModel);
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
                        super.onEnd();
                        getV().hideProgress();
                    }
                });
    }

}
