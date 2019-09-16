package com.jj.comics.ui.mine.rebate.detail;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.RebateListResponse;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RebatePresenter extends BasePresenter<BaseRepository,RebateFragment> implements RebateContract.IRebatePresenter {
    @Override
    public void getRebateList(int page) {
        UserRepository.getInstance()
                .getRebateList(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(bindLifecycle())
                .subscribe(new ApiSubscriber2<RebateListResponse>() {
                    @Override
                    protected void onFail(NetError error) {
                        getV().onGetRebateListFail(NetError.noDataError());
                    }

                    @Override
                    public void onNext(RebateListResponse rebateListResponse) {
                        RebateListResponse.DataBeanX data = rebateListResponse.getData();
                        if (data != null) {
                            List<RebateListResponse.DataBeanX.RebateModel> rebateModels = data.getData();
                            if (rebateModels != null) {
                                getV().onGetRebateListSucc(rebateModels);
                            }else {
                                getV().onGetRebateListFail(NetError.noDataError());
                            }
                        }else {
                            getV().onGetRebateListFail(NetError.noDataError());
                        }
                    }
                });
    }
}
