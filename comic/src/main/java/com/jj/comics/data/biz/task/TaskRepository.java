package com.jj.comics.data.biz.task;

import com.jj.base.net.ComicApiImpl;
import com.jj.base.net.RetryFunction2;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.net.ComicApi;
import com.jj.comics.common.net.RequestBodyBuilder;
import com.jj.comics.data.model.CommonStatusResponse;
import com.jj.comics.data.model.ResponseModel;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

public class TaskRepository implements TaskDataSource {
    private static TaskRepository INSTANCE;

    private TaskRepository() {
    }

    public static TaskRepository getInstance() {
        if (INSTANCE == null) INSTANCE = new TaskRepository();
        return INSTANCE;
    }


    @Override
    public Observable<CommonStatusResponse> reportShare() {

        Observable<CommonStatusResponse> compose = ComicApi.getApi().reportShare()
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryFunction2())
                .compose(ComicApiImpl.<CommonStatusResponse>getApiTransformer2());
        return compose;
    }

    /**
     * 领取新手任务金币
     *
     * @param type
     * @return
     */
    @Override
    public Observable<ResponseModel> getNewGlod(String type) {
        RequestBody build = new RequestBodyBuilder()
                .addProperty(Constants.TaskCode.TYPE, type)
                .build();

        Observable<ResponseModel> compose = ComicApi.getApi().getNewGold(build)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryFunction2())
                .compose(ComicApiImpl.<ResponseModel>getApiTransformer2());
        return compose;
    }

    /**
     * 领取每日任务金币
     *
     * @param type
     * @return
     */
    @Override
    public Observable<ResponseModel> getDayGlod(String type) {
        RequestBody build = new RequestBodyBuilder()
                .addProperty(Constants.TaskCode.TYPE, type)
                .build();

        Observable<ResponseModel> compose = ComicApi.getApi().getDayGold(build)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryFunction2())
                .compose(ComicApiImpl.<ResponseModel>getApiTransformer2());
        return compose;
    }


    /**
     * 金币大放送
     *
     * @return
     */
    @Override
    public Observable<CommonStatusResponse> presentGold() {
        Observable<CommonStatusResponse> compose = ComicApi.getApi().presentGold()
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryFunction2())
                .compose(ComicApiImpl.<CommonStatusResponse>getApiTransformer2());
        return compose;
    }

    @Override
    public Observable<CommonStatusResponse> reportShare(long bookId) {
        RequestBody requestBody = new RequestBodyBuilder()
                .addProperty(Constants.RequestBodyKey.BOOK_ID, bookId)
                .build();
        return ComicApi.getApi().reportShare(requestBody)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryFunction2())
                .observeOn(Schedulers.io());
    }
}
