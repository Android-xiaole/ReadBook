package com.jj.comics.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.jj.comics.greendao.gen.DaoMaster;
import com.jj.comics.greendao.gen.UserInfoDao;

import org.greenrobot.greendao.database.Database;

public class DevOpenHelper extends DaoMaster.DevOpenHelper {

    public DevOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
//        if (oldVersion < newVersion) {
//            MigrationHelper.getInstance().migrate(db, UserInfoDao.class);
////            MigrationHelper.getInstance().migrate(db, UserInfoDao.class);
//            //更改过的实体类(新增的不用加)   更新UserDao文件 可以添加多个  XXDao.class 文件
////             MigrationHelper.getInstance().migrate(db, UserDao.class,XXDao.class);
//        } else {
//            super.onUpgrade(db, oldVersion, newVersion);
//        }
    }
}
