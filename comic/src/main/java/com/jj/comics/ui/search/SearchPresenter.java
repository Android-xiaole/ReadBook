package com.jj.comics.ui.search;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.comics.data.biz.content.ContentRepository;
import com.jj.comics.data.db.DaoHelper;
import com.jj.comics.data.model.BookListResponse;
import com.jj.comics.data.model.SearchHotKeywordsResponse;
import com.jj.comics.data.model.SearchModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SearchPresenter extends BasePresenter<BaseRepository, SearchContract.ISearchView> implements SearchContract.ISearchPresenter {
    private DaoHelper<SearchModel> mDaoHelper = new DaoHelper<>();

    @Override
    public void loadRecentData() {
        Flowable.create(new FlowableOnSubscribe<List<SearchModel>>() {
            @Override
            public void subscribe(FlowableEmitter<List<SearchModel>> e) throws Exception {

                List<SearchModel> searchModels = mDaoHelper.loadAllSearchKey();
                if (searchModels == null) searchModels = new ArrayList<>();
                e.onNext(searchModels);
                e.onComplete();
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<List<SearchModel>>bindLifecycle())
                .subscribe(new ApiSubscriber<List<SearchModel>>() {
                    @Override
                    public void onNext(List<SearchModel> searchModels) {
                        getV().fillKeyData(searchModels);
                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().hideProgress();
                        getV().fillKeyData(new ArrayList<SearchModel>());
                    }
                });
    }

    @Override
    public void getHotSearchKeywords() {
        ContentRepository.getInstance().getHotSearchKeywords(getV().getClass().getSimpleName())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<SearchHotKeywordsResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<SearchHotKeywordsResponse>() {
                    @Override
                    public void onNext(SearchHotKeywordsResponse response) {
                        getV().fillHotSearchKeywords(response);
                        getV().onComplete();
                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().showToastShort(error.getMessage());
                        getV().onComplete();
                    }
                });
    }

    @Override
    public void dealKey(SearchModel model) {
        mDaoHelper.insertOrUpdateKey(model.getKey());
    }
}
