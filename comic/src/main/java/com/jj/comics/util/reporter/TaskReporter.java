package com.jj.comics.util.reporter;

import com.jj.base.log.LogUtil;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.comics.data.biz.task.TaskRepository;
import com.jj.comics.data.db.DaoHelper;
import com.jj.comics.data.model.CommonStatusResponse;
import com.jj.comics.data.model.ResponseModel;
import com.jj.comics.data.visittime.OnlineTimeData;
import com.jj.comics.data.visittime.ReadTimeData;
import com.jj.comics.data.visittime.TimeReportData;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

public class TaskReporter {

    /**
     * 上报分享任务
     */
    public static void reportShare(long bookId) {
        if (bookId == 0)return;
        TaskRepository.getInstance()
                .reportShare(bookId)
                .subscribe(new ApiSubscriber2<CommonStatusResponse>() {
                    @Override
                    protected void onFail(NetError error) {
                    }

                    @Override
                    public void onNext(CommonStatusResponse response) {
                    }
                });

    }

    /**
     * 时间上报
     */
    public static void reportTimeData(DaoHelper daoHelper) {
        List<OnlineTimeData> onlineTimeDataAll = daoHelper.getOnlineTimeDataAll();
        List<ReadTimeData> readTimeDataAll = daoHelper.getReadTimeDataAll();
        if (onlineTimeDataAll != null && !onlineTimeDataAll.isEmpty()) {
            List<TimeReportData> userDataList = new ArrayList<>();
            List<TimeReportData> visitorDataList = new ArrayList<>();
            for (OnlineTimeData onlineTimeData : onlineTimeDataAll) {
                TimeReportData data = new TimeReportData();
                data.setLoginTime(onlineTimeData.getLastLoginTime());
                data.setLogoutTime(onlineTimeData.getLastLogoutTime());
                data.setOnlineDuration(onlineTimeData.getDuration());
                data.setDate(onlineTimeData.getDate());
                if (onlineTimeData.getIs_visitor()) {
                    //访客登录
                    data.setToken(onlineTimeData.getUid());
                    visitorDataList.add(data);
                } else {
                    //注册用户登录
                    data.setUid(Long.parseLong(onlineTimeData.getUid()));
                    userDataList.add(data);
                }
            }
            if (!userDataList.isEmpty()) {
                TaskRepository.getInstance().userReport(userDataList)
                        .subscribe(new ApiSubscriber2<ResponseModel>() {
                            @Override
                            protected void onFail(NetError error) {
                                LogUtil.e("LogTime 用户在线时长上报失败："+error.getMessage());
                            }

                            @Override
                            public void onNext(ResponseModel responseModel) {
                                for (TimeReportData data : userDataList) {
                                    daoHelper.delete(data);
                                }
                                LogUtil.e("LogTime 用户在线时长上报成功");
                            }
                        });
            }
            if (!visitorDataList.isEmpty()) {
                TaskRepository.getInstance().visitorReport(userDataList)
                        .subscribe(new ApiSubscriber2<ResponseModel>() {
                            @Override
                            protected void onFail(NetError error) {
                                LogUtil.e("LogTime 访客在线时长上报失败："+error.getMessage());
                            }

                            @Override
                            public void onNext(ResponseModel responseModel) {
                                for (TimeReportData data : visitorDataList) {
                                    daoHelper.delete(data);
                                }
                                LogUtil.e("LogTime 访客在线时长上报成功");
                            }
                        });
            }
        }

        if (readTimeDataAll != null && !readTimeDataAll.isEmpty()) {
            List<TimeReportData> userDataList = new ArrayList<>();
            List<TimeReportData> visitorDataList = new ArrayList<>();
            for (ReadTimeData readTimeData : readTimeDataAll) {
                TimeReportData data = new TimeReportData();
                data.setReadDuration(readTimeData.getDuration());
                data.setDate(readTimeData.getDate());
                data.setBookId(readTimeData.getBoodId());
                if (readTimeData.getIs_visitor()) {
                    //访客登录
                    data.setToken(readTimeData.getUid());
                    visitorDataList.add(data);
                } else {
                    //注册用户登录
                    data.setUid(Long.parseLong(readTimeData.getUid()));
                    userDataList.add(data);
                }
            }
            if (!userDataList.isEmpty()) {
                TaskRepository.getInstance().userReport(userDataList)
                        .subscribe(new ApiSubscriber2<ResponseModel>() {
                            @Override
                            protected void onFail(NetError error) {
                                LogUtil.e("LogTime 用户阅读时长上报失败："+error.getMessage());
                            }

                            @Override
                            public void onNext(ResponseModel responseModel) {
                                for (TimeReportData data : userDataList) {
                                    daoHelper.delete(data);
                                }
                                LogUtil.e("LogTime 用户阅读时长上报成功");
                            }
                        });
            }
            if (!visitorDataList.isEmpty()) {
                TaskRepository.getInstance().visitorReport(userDataList)
                        .subscribe(new ApiSubscriber2<ResponseModel>() {
                            @Override
                            protected void onFail(NetError error) {
                                LogUtil.e("LogTime 访客阅读时长上报失败："+error.getMessage());
                            }

                            @Override
                            public void onNext(ResponseModel responseModel) {
                                for (TimeReportData data : visitorDataList) {
                                    daoHelper.delete(data);
                                }
                                LogUtil.e("LogTime 访客阅读时长上报成功");
                            }
                        });
            }
        }
    }
}
