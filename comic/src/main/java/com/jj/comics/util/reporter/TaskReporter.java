package com.jj.comics.util.reporter;

import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.comics.data.biz.task.TaskRepository;
import com.jj.comics.data.model.CommonStatusResponse;

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

}
