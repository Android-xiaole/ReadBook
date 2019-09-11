package com.jj.comics.ui.mine.coin;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.comics.data.biz.user.UserDataSource;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.ConsumeDetailListResponse;
import com.jj.comics.data.model.PayCenterInfoResponse;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class ConsumeDetailPreseneter extends BasePresenter<BaseRepository,ConsumeDetailContract.IConsumeDetailView> implements ConsumeDetailContract.IConsumeDetailPresenter{

    @Override
    public void getConsumeDetail(long bookid) {
        UserRepository.getInstance()
                .getConsumeDetail(bookid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiSubscriber2<ConsumeDetailListResponse>() {
                    @Override
                    protected void onFail(NetError error) {
                        error.getMessage();
                        getV().getDataFail(error);
                    }

                    @Override
                    public void onNext(ConsumeDetailListResponse response) {
                        ConsumeDetailListResponse.DataBeanX data = response.getData();
                        if (data != null) {
                            List<ConsumeDetailListResponse.DataBeanX.ConsumeDetail> detailList = data.getData();
                            if (detailList != null) {
                                getV().fillData(detailList);
                            }else {
                                getV().getDataFail(new NetError("No data",NetError.NoDataError));
                            }
                        }

                    }
                });
    }
}
