package com.jj.comics.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.jj.base.log.LogUtil;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.comics.data.biz.task.TaskRepository;
import com.jj.comics.data.db.DaoHelper;
import com.jj.comics.data.visittime.AccessTokenResponse;
import com.jj.comics.util.SharedPreManger;
import com.jj.comics.util.reporter.TaskReporter;

/**
 * Author ：le
 * Date ：2019-10-29 9:22
 * Description ：上报用户在线时长和阅读时长任务的服务，暂时用不到，因为application里面规避了非主进程的操作
 */
public class ReportTaskService extends Service {

    private Handler handler;
    private DaoHelper daoHelper;
    private Runnable runnable;
    private boolean is_start;//标记当前服务是否已经启动

    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化相关类
        handler = new Handler();
        daoHelper = new DaoHelper();
        runnable = new Runnable() {
            @Override
            public void run() {
                TaskReporter.reportTimeData(daoHelper);
                handler.postDelayed(this, 10 * 60 * 1000);//设置10min上传一次本地记录
            }
        };
        TaskRepository.getInstance().getReportToken().subscribe(new ApiSubscriber2<AccessTokenResponse>() {
            @Override
            protected void onFail(NetError error) {
                LogUtil.e("LogTime 获取游客token失败");
            }

            @Override
            public void onNext(AccessTokenResponse accessTokenResponse) {
                LogUtil.e("LogTime 获取游客token成功");
                if (accessTokenResponse!=null&&accessTokenResponse.getAccess_token()!=null){
                    LogUtil.e("LogTime accessToken:"+accessTokenResponse.getAccess_token());
                    SharedPreManger.getInstance().saveVisitorToken(accessTokenResponse.getAccess_token());
                }
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //如果未启动就设置定时任务，防止重复启动多次开始定时任务，导致任务错乱
        if (!is_start){
            //十秒后进行一次数据上报，而后没10min执行一次数据上报
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    TaskReporter.reportTimeData(daoHelper);
                    handler.postDelayed(this,10*60*1000);//设置10min上传一次本地记录
                }
            },10000);
        }
        is_start = true;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (runnable!=null){
            handler.removeCallbacks(runnable);
        }
    }
}
