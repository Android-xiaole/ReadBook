package com.jj.comics.data.db;

import android.content.Context;

import com.jj.base.BaseApplication;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.greendao.gen.DaoMaster;
import com.jj.comics.greendao.gen.DaoSession;

import org.greenrobot.greendao.query.QueryBuilder;

/**
 * Created by 皓然 on 2017/8/20.
 */

public class DaoManager {
    private static volatile DaoManager manager;
    private static DaoMaster.DevOpenHelper helper;
    private static DaoSession daoSession;
    private Context mContext;

    private DaoManager() {
        this.mContext = BaseApplication.getApplication();
    }

    public static DaoManager getInstance() {
        if (manager == null) {
            synchronized (DaoManager.class) {
                if (manager == null) {
                    manager = new DaoManager();
                }
            }
        }
        return manager;
    }

    private static DaoMaster daoMaster;

    //判断是否存在数据库，没有就创建
    public DaoMaster getDaoMaster() {
        if (daoMaster == null) {
            helper = new DevOpenHelper(mContext, Constants.DB_NAME, null);
            daoMaster = new DaoMaster(helper.getWritableDb());
        }
        return daoMaster;
    }

    //   数据处理
    public DaoSession getDaoSession() {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster();
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

    //输出日志
    public void setDebug() {
        QueryBuilder.LOG_SQL = Constants.DEBUG;
        QueryBuilder.LOG_VALUES = Constants.DEBUG;
    }

    public void close() {
        closeHelper();
        closeSession();
    }

    public void closeHelper() {
        if (helper != null) {
            helper.close();
            helper = null;
        }
    }

    //关闭session
    public void closeSession() {
        if (daoSession != null) {
            daoSession.clear();
            daoSession = null;
        }
    }
}
