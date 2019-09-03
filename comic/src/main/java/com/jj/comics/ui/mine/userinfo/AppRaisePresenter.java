package com.jj.comics.ui.mine.userinfo;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;

public class AppRaisePresenter extends BasePresenter<BaseRepository, AppRaiseContract.IAppRaiseView> implements AppRaiseContract.IAppRaisePresenter {

    @Override
    public void getMyStarList(int pageNum, int pageSize) {

//        UserRepository.getInstance().getAppRaiseInofList(getV().getClass().getName(), pageNum, pageSize)
//                .observeOn(AndroidSchedulers.mainThread())
////                .as(AutoDispose.<AppRaiseInfoModel>autoDisposable(AndroidLifecycleScopeProvider.from(getV().getLifecycle())))
//                .as(this.<AppRaiseInfoModel>bindLifecycle())
//                .subscribe(new ApiSubscriber2<AppRaiseInfoModel>() {
//                    @Override
//                    public void onNext(AppRaiseInfoModel responseModel) {
//                        getV().updateData(responseModel.getRaiseInfoList());
//                    }
//
//
//                    @Override
//                    protected void onFail(NetError error) {
//                        getV().updateDataErr(error.getMessage());
//                    }
//                });
    }

    @Override
    public boolean useEventBus() {
        return false;
    }
}
