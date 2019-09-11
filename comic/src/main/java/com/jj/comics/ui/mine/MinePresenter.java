package com.jj.comics.ui.mine;


import android.os.Environment;
import android.util.Log;

import com.jj.base.BaseApplication;
import com.jj.base.imageloader.ILFactory;
import com.jj.base.log.LogUtil;
import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.base.utils.FileUtil;
import com.jj.base.utils.SharedPref;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.biz.task.TaskRepository;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.FeedbackStatusModel;
import com.jj.comics.data.model.PayInfoResponse;
import com.jj.comics.data.model.SignAutoResponse;
import com.jj.comics.data.model.SignTaskResponse;
import com.jj.comics.data.model.UserInfoResponse;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.eventbus.events.LogoutEvent;

import java.io.File;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.ResourceSubscriber;

public class MinePresenter extends BasePresenter<BaseRepository, MineContract.IMineView> implements MineContract.IMinePresenter {

    @Override
    public void getUserInfo() {
        getV().showProgress();
        UserRepository.getInstance().getUserInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<UserInfoResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<UserInfoResponse>() {
                    @Override
                    public void onNext(UserInfoResponse responseModel) {
                        UserInfoResponse.DataBean data = responseModel.getData();
                        if (data!=null&&data.getBaseinfo()!=null){
                            LoginHelper.updateUser(data.getBaseinfo());
                            getV().onGetUserInfo(data.getBaseinfo());
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToastShort(error.getMessage());
                        if (error.getType() == NetError.AuthError) {
                            EventBusManager.sendLogoutEvent(new LogoutEvent());
                        }
                    }

                    @Override
                    protected void onEnd() {
                        super.onEnd();
                        getV().hideProgress();
                    }
                });
    }

    @Override
    public void getUserPayInfo(){
        UserRepository.getInstance().getUserPayInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<PayInfoResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<PayInfoResponse>() {

                    @Override
                    public void onNext(PayInfoResponse payInfoResponse) {
                        getV().onGetUserPayInfo(payInfoResponse.getData());
                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToastShort(error.getMessage());
                    }
                });
    }

    public void getFeedbackStatus(){
        UserRepository.getInstance().getFeedbackStatus()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<FeedbackStatusModel>bindLifecycle())
                .subscribe(new ApiSubscriber2<FeedbackStatusModel>() {
                    @Override
                    public void onNext(FeedbackStatusModel feedbackStatusModel) {
                        getV().onGetFeedbackStatus(feedbackStatusModel.getData().getNumber());
                    }

                    @Override
                    protected void onFail(NetError error) {
                    }
                });
    }

    @Override
    public void getTaskStatus() {
        Observable<SignTaskResponse> signTaskResponseObservable = TaskRepository.getInstance()
                .getSignTasks(getV().getClass().getName())
                .subscribeOn(Schedulers.io());

        Observable<SignAutoResponse> signAutoResponseObservable = TaskRepository.getInstance()
                .signAuto();

        Observable.zip(signTaskResponseObservable,
                signAutoResponseObservable,(t1, t2) -> {
                    int count = 0;
                    SignTaskResponse.DataBean data = t1.getData();

                    List<SignTaskResponse.DataBean.TaskListBean> task_list = data.getTask_list();
                    for (SignTaskResponse.DataBean.TaskListBean taskListBean : task_list) {
                        if (taskListBean != null) {
                            List<SignTaskResponse.DataBean.TaskListBean.ListBean> list = taskListBean.getList();
                            if (list != null) {
                                for(SignTaskResponse.DataBean.TaskListBean.ListBean listBean
                                        : list) {
                                    if (listBean != null) {
                                        if (listBean.getIs_take() == 1) {
                                            count ++;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    SignAutoResponse.DataBean signAutoResponse = t2.getData();
                    if (signAutoResponse != null) {
                        if (signAutoResponse.getIs_check() == 0) count++;
                    }

                    return count;
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.bindLifecycle())
                .subscribe(new ApiSubscriber2<Integer>() {
                    @Override
                    protected void onFail(NetError error) {
//                        getV().showToastShort("" + error.getMessage());
                    }

                    @Override
                    public void onNext(Integer integer) {
                        getV().onGetTaskInfo(integer);
                    }
                });
    }

    @Override
    public void autoBuy(final boolean isChecked) {
        SharedPref.getInstance().putBoolean(Constants.SharedPrefKey.AUTO_BUY, isChecked);
    }

    @Override
    public boolean hasAllSubscribe() {
        return SharedPref.getInstance().getBoolean(Constants.SharedPrefKey.AUTO_BUY, false);
    }

    @Override
    public void clearCache() {
        Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> flowableEmitter) throws Exception {
                try {
                    FileUtil.deleteFilesByDirectory(BaseApplication.getApplication().getCacheDir());
                    FileUtil.deleteFilesByDirectory(BaseApplication.getApplication().getFilesDir());
                    FileUtil.deleteFile("/data/data/"
                            + BaseApplication.getApplication().getPackageName() + "/shared_prefs/pdr.xml");
                    if (Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)) {
                        FileUtil.deleteFilesByDirectory(BaseApplication.getApplication().getExternalCacheDir());
                        FileUtil.deleteFilesByDirectory(BaseApplication.getApplication().getExternalFilesDir(""));
                    }
                    FileUtil.deleteFilesByDirectory(new File("/data/data/"
                            + BaseApplication.getApplication().getPackageName() + "/app_webview"));
                    ILFactory.getLoader().clearDiskCache(BaseApplication.getApplication());
                    flowableEmitter.onNext(FileUtil.getCaCheSize(BaseApplication.getApplication()));
                } catch (Exception e) {
                    flowableEmitter.onError(e);
                }
                flowableEmitter.onComplete();
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<String>bindLifecycle())
                .subscribe(new ResourceSubscriber<String>() {
                    @Override
                    public void onNext(String size) {
                        ILFactory.getLoader().clearMemoryCache(BaseApplication.getApplication());
                        getV().setCacheSize(size);
                    }

                    @Override
                    public void onError(Throwable t) {
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    public void signAuto() {
        TaskRepository.getInstance().signAuto()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<SignAutoResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<SignAutoResponse>() {
                    @Override
                    public void onNext(SignAutoResponse response) {
                        if (response.getData() != null) {
                            getV().fillSignAuto(response);
                        } else {
                            ToastUtil.showToastShort(response.getMessage());
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
//                        ToastUtil.showToastShort(error.getMessage());
                    }
                });
    }
}
