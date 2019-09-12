package com.jj.comics.ui.sort;

import com.jj.base.BaseApplication;
import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.comics.data.biz.content.ContentRepository;
import com.jj.comics.data.model.BookListDataResponse;
import com.jj.comics.data.model.BookListResponse;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.SortListResponse;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class SortListPresent extends BasePresenter<BaseRepository, SortListContract.ISortView> implements SortListContract.ISortPresenter {

    @Override
    public void loadNoverList(int page, int length, long cid) {
        ContentRepository.getInstance().getBookCategories(page, length, cid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<BookListDataResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<BookListDataResponse>() {
                    @Override
                    public void onNext(BookListDataResponse response) {
                        BookListDataResponse.DataBean data = response.getData();
                        if (data != null) {
                            List<BookModel> bookModels = data.getData();
                            if (bookModels != null) {
                                getV().fillNoverList(response.getData().getData(), data.getTotal_num());
                            } else {
                                getV().getListFail(new NetError(BaseApplication.getApplication().getString(com.jj.base.R.string.base_empty_data), NetError.NoDataError));
                            }
                        } else {
                            getV().getListFail(new NetError(BaseApplication.getApplication().getString(com.jj.base.R.string.base_empty_data), NetError.NoDataError));
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().getListFail(error);
                    }
                });
    }
}
