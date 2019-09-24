package com.jj.comics.ui.mine.login;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.LoginResponse;
import com.jj.comics.data.model.ResponseModel;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.util.SharedPreManger;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;

public class BindPhonePresenter extends BasePresenter<BaseRepository, BindPhoneContract.View> implements BindPhoneContract.Presenter {


    @Override
    public void getCode(String phoneNum, String type) {
        getV().showProgress();
        UserRepository.getInstance().getThirdLoginCode(phoneNum,type)
                .subscribeOn(AndroidSchedulers.mainThread())
                .as(bindLifecycle())
                .subscribe(new ApiSubscriber2<ResponseModel>() {
                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToastShort(error.getMessage());
                    }

                    @Override
                    public void onNext(ResponseModel responseModel) {

                    }

                    @Override
                    protected void onEnd() {
                        super.onEnd();
                        getV().hideProgress();
                    }
                });
    }

    @Override
    public void bindPhone(String phoneNum, String code,String inviteCode, String openid) {
        getV().showProgress();
        UserRepository.getInstance().bindPhone(phoneNum,code,inviteCode,openid)
                .flatMap(new Function<LoginResponse, ObservableSource<UserInfo>>() {
                    @Override
                    public ObservableSource<UserInfo> apply(LoginResponse loginResponse) throws Exception {
                        if (loginResponse.getData()!=null&&loginResponse.getData().getUser_info()!=null){
                            SharedPreManger.getInstance().saveToken(loginResponse.getData().getBearer_token());
                            return UserRepository.getInstance().saveUser(loginResponse.getData().getUser_info());
                        }else{
                            return Observable.error(NetError.noDataError());
                        }
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .as(bindLifecycle())
                .subscribe(new ApiSubscriber2<UserInfo>() {
                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToastShort(error.getMessage());
                    }

                    @Override
                    public void onNext(UserInfo userInfo) {
                        getV().onBindPhone(userInfo);
                    }

                    @Override
                    protected void onEnd() {
                        super.onEnd();
                        getV().hideProgress();
                    }
                });
    }
}
