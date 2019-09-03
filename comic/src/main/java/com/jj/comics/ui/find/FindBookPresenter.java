package com.jj.comics.ui.find;


import com.jj.base.BaseApplication;
import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.comics.data.biz.content.ContentRepository;
import com.jj.comics.data.model.BookListDataResponse;
import com.jj.comics.data.model.CategoryResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FindBookPresenter extends BasePresenter<BaseRepository, FindBookContract.IFindView> implements FindBookContract.IFindPresenter {
    /**
     * 获取漫画分类类型
     *
     */
    @Override
    public void loadType() {
        ContentRepository.getInstance().getCategoryList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<CategoryResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<CategoryResponse>() {
                    @Override
                    public void onNext(CategoryResponse response) {
                        if (response.getData() != null) {
                            List<CategoryResponse.DataBean> result = new ArrayList<>();
                            List<CategoryResponse.DataBean> typeList = response.getData();
                            for (int i = 0; i < Math.min(7, typeList.size()); i++) {
                                result.add(typeList.get(i));
                            }
                            getV().fillType(result);
                        } else {
                            getV().getTypeFail(new NetError(BaseApplication.getApplication().getString(com.jj.base.R.string.base_empty_data), NetError.NoDataError));
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().getTypeFail(error);
                    }
                });

    }

    /**
     * 加载分类数据
     *
     * @param pageNum   页数
     * @param type1Code 类型
     * @param sort      标签
     */
    @Override
    public void loadList(int pageNum, long type1Code, String sort) {
        ContentRepository.getInstance().getContentListByType(pageNum, 20, type1Code + "", sort)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<BookListDataResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<BookListDataResponse>() {
                    @Override
                    public void onNext(BookListDataResponse data) {
                        if (data.getData() != null && data.getData().getData() != null) {
                            getV().fillData(data.getData().getTotal_num(), data.getData().getData());
                        } else {
                            getV().getDataFail(new NetError(BaseApplication.getApplication().getString(com.jj.base.R.string.base_empty_data), NetError.NoDataError));
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().getDataFail(error);
                    }
                });
    }
}
