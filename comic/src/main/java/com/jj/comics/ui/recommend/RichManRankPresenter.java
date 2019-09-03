package com.jj.comics.ui.recommend;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.base.net.RetryFunction2;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.RichDataResponse;
import com.jj.comics.data.model.RichManModel;
import com.jj.comics.data.model.RichResponse;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class RichManRankPresenter extends BasePresenter<BaseRepository, RichManRankContract.IRichManRankView> implements RichManRankContract.IRichManRankPresenter {


    @Override
    public void getRickManRankList() {

        UserRepository.getInstance().getRewardRankingListOfUser(getV().getClass().getName())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<RichResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<RichResponse>() {
                    @Override
                    public void onNext(RichResponse model) {
                        List<RichManModel> dataBeanList = model.getData();
                        getV().onFetchData(dataBeanList);
                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().showToastShort(error.getMessage());
                        getV().hideProgress();
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        getV().hideProgress();
                    }
                });
    }



    @Override
    public void getRewardNow(int pageNum) {
        UserRepository.getInstance().rewardRecordByAllUser(getV().getClass().getName(),pageNum)
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(new RetryFunction2(getV().getClass().getName()))
                .as(this.<RichDataResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<RichDataResponse>() {
                    @Override
                    public void onNext(RichDataResponse responseModel) {
                        RichDataResponse.DataBeanX modelData = responseModel.getData();
                        if (modelData != null) {
                            List<RichManModel> data = modelData.getData();
                            if (data != null) {
                                getV().onGetRewardNowData(data);
                            }else {

                            }
                        }else {
                            getV().showToastShort("数据加载失败");
                            getV().hideProgress();
                        }


                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().showToastShort(error.getMessage());
                        getV().hideProgress();
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        getV().hideProgress();
                    }
                });
    }

    @Override
    public boolean useEventBus() {
        return false;
    }
}
