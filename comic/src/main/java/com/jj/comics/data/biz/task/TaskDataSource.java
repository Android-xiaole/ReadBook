package com.jj.comics.data.biz.task;

import com.jj.comics.data.model.CommonStatusResponse;
import com.jj.comics.data.model.ResponseModel;
import com.jj.comics.data.model.SignAutoResponse;
import com.jj.comics.data.model.SignResponse;
import com.jj.comics.data.model.SignTaskResponse;

import io.reactivex.Observable;

public interface TaskDataSource {

    //签到
    Observable<SignResponse> SignIn(String activityName);

    //签到状态和自动购买
    Observable<SignAutoResponse> signAuto();

    /**
     * 完成分享上报
     *
     * @return
     */
    Observable<CommonStatusResponse> reportShare();

    //领取新手任务奖励
    Observable<ResponseModel> getNewGlod(String type);

    //领取每日任务奖励
    Observable<ResponseModel> getDayGlod(String type);

    Observable<SignTaskResponse> getSignTasks(String activityName);

    Observable<CommonStatusResponse>presentGold();

    Observable<CommonStatusResponse> reportShare(long bookId);

}
