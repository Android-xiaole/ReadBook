package com.jj.comics.ui.sort;

import com.jj.base.BaseApplication;
import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.comics.data.biz.content.ContentRepository;
import com.jj.comics.data.model.BookListDataResponse;
import com.jj.comics.data.model.CategoryResponse;
import com.jj.comics.data.model.SortListResponse;
import com.jj.comics.ui.find.FindBookContract;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SortPresenter extends BasePresenter<BaseRepository, SortContract.ISortView> implements SortContract.ISortPresenter {
    /**
     * 获取漫画分类类型
     */
    @Override
    public void loadTypeList(String name) {
        ContentRepository.getInstance().getSortList(name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<SortListResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<SortListResponse>() {
                    @Override
                    public void onNext(SortListResponse response) {
                        if (response.getData() != null) {
                            getV().fillTypeList(response.getData());
                        } else {
                            getV().getTypeListFail(new NetError(BaseApplication.getApplication().getString(com.jj.base.R.string.base_empty_data), NetError.NoDataError));
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().getTypeListFail(error);
                    }
                });
    }
}
