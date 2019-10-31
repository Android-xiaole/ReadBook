package com.jj.comics.data.biz.task;

import com.jj.comics.data.model.CommonStatusResponse;
import com.jj.comics.data.model.ResponseModel;
import com.jj.comics.data.visittime.AccessTokenResponse;
import com.jj.comics.data.visittime.TimeReportData;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;

public interface TaskDataSource {


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

    Observable<CommonStatusResponse> presentGold();

    Observable<CommonStatusResponse> reportShare(long bookId);

    Observable<AccessTokenResponse> getReportToken();

    Observable<ResponseModel> userReport(List<TimeReportData> dataList);

    Observable<ResponseModel> visitorReport(List<TimeReportData> dataList);


}
