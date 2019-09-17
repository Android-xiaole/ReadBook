package com.jj.comics.ui.mine.apprentice;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.ApprenticeListResponse;
import com.jj.comics.data.model.ApprenticeModel;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TuziPresenter extends BasePresenter <BaseRepository,TuziFragment> implements TuziContract.ITuziPresenter{
    @Override
    public void getData(int page, int type) {
        UserRepository.getInstance()
                .getApprenticeList(page,type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(bindLifecycle())
                .subscribe(new ApiSubscriber2<ApprenticeListResponse>() {
                    @Override
                    protected void onFail(NetError error) {
                        getV().onGetDataFail(error);
                    }

                    @Override
                    public void onNext(ApprenticeListResponse apprenticeListResponse) {
                        ApprenticeListResponse.DataBeanX data = apprenticeListResponse.getData();
                        if (data != null) {
                            List<ApprenticeModel> models = data.getData();
                            if (models != null) {
                                getV().onGetData(models);
                            }else {
                                getV().onGetDataFail(NetError.noDataError());
                            }
                        }else {
                            getV().onGetDataFail(NetError.noDataError());
                        }
                    }
                });
    }
}
