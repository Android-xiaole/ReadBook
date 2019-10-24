package com.jj.comics.data.db;

import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.SearchModel;
import com.jj.comics.data.model.UserCommentFavorData;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.data.visittime.OnlineTimeData;
import com.jj.comics.data.visittime.ReadTimeData;
import com.jj.comics.greendao.gen.BookModelDao;
import com.jj.comics.greendao.gen.OnlineTimeDataDao;
import com.jj.comics.greendao.gen.SearchModelDao;
import com.jj.comics.greendao.gen.UserCommentFavorDataDao;
import com.jj.comics.greendao.gen.UserInfoDao;
import com.jj.comics.util.DateHelper;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 皓然 on 2017/8/20.
 */

public class DaoHelper<T> {
    private DaoManager manager;
    private Class<T> clazz;

    public DaoHelper() {
        manager = DaoManager.getInstance();
        manager.setDebug();
    }

    private Class<T> getClazz() {
        if (clazz == null) {//获取泛型的Class对象
            clazz = ((Class<T>) (((ParameterizedType) (this.getClass().getGenericSuperclass())).getActualTypeArguments()[0]));
        }
        return clazz;
    }

    // 插入增加
    public boolean insert(T t) {
        return manager.getDaoSession().insert(t) != -1;
    }

    //删除
    public boolean delete(T t) {
        try {
            manager.getDaoSession().delete(t);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 删除所有
    public boolean deleteAll() {
        try {
            manager.getDaoSession().deleteAll(clazz);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

//    public boolean deleteAllSearch() {
//        try {
//            manager.getDaoSession().getDBSearchResultDao().deleteAll();
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

    //列出所有
    public List<T> listAll() {
        return manager.getDaoSession().loadAll(getClazz());
    }

    public T find(long id) {
        return manager.getDaoSession().load(clazz, id);
    }


    //更新
    public boolean update(T t) {
        try {
            manager.getDaoSession().update(t);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean insertOrUpdate(T t, int id) {
        try {
            if (find(id) == null)
                insert(t);
            else
                update(t);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 用户登陆之后的操作
     */
    public boolean updateUser(UserInfo userInfo) {
        UserInfoDao userInfoDao = manager.getDaoSession().getUserInfoDao();
        List<UserInfo> userInfos = userInfoDao.loadAll();
        for (UserInfo info : userInfos) {
            if (info.getIsLogin()) {
                info.setIsLogin(false);
                userInfoDao.update(info);
            }
        }
        if (!userInfo.getIsLogin()) {
            userInfo.setIsLogin(true);
        }
        return manager.getDaoSession().insertOrReplace(userInfo) > 0;
    }

    public void logOffAllUser() {
//        UserInfoDao userInfoDao = manager.getDaoSession().getUserInfoDao();
//        List<UserInfo> userInfos = userInfoDao.loadAll();
//        for (UserInfo info : userInfos) {
//            if (info.getIsLogin()) {
//                info.setIsLogin(false);
//                userInfoDao.update(info);
//            }
//        }

        UserInfoDao userInfoDao = manager.getDaoSession().getUserInfoDao();
        if (userInfoDao != null) {
            userInfoDao.deleteAll();
        }
    }

    public UserInfo getLoginUser() {
        List<UserInfo> list = manager.getDaoSession().getUserInfoDao().queryBuilder().where(UserInfoDao.Properties.IsLogin.eq(true)).list();
        if (list == null || list.isEmpty()) return null;
        return list.get(0);
    }

    public List<SearchModel> loadAllSearchKey() {
        return manager.getDaoSession().getSearchModelDao().queryBuilder().orderDesc(SearchModelDao.Properties.Time).list();
    }

    public void insertOrUpdateKey(String key) {
        SearchModel model = new SearchModel(key, System.currentTimeMillis());
        SearchModelDao dao = manager.getDaoSession().getSearchModelDao();
        dao.insertOrReplace(model);
    }

    public void deleteKey() {
        SearchModelDao dao = manager.getDaoSession().getSearchModelDao();
        dao.deleteAll();
    }

    /**
     * 保存历史记录 逻辑如下：
     * userId统一设置=0代表未上传，上传成功之后会直接删除该条记录
     *
     * @param model        转化为记录model的目录model
     * @param userId       用户id
     * @param chapterid    章节id
     * @param chapterorder 阅读至多少话
     * @param chaptername  章节名称
     */
    public void insertOrUpdateRecord(BookModel model, long userId, long chapterid, int chapterorder,String chaptername) {
        BookModel localBookModel = queryBookModelByBookidAndUid(model.getId(), userId);
        if (localBookModel != null) {
            //如果本地存在这本书，就设置主键去执行更新操作
            model.set_id(localBookModel.get_id());
            //设置更新时间
            model.setUpdate_time(System.currentTimeMillis());
        } else {//不存在这本书，没有设置主键，后续会自动执行插入操作
            //设置创建时间
            model.setCreate_time(System.currentTimeMillis());
            //这里插入的时候同时设置更新时间是为了方便展示的时候排序
            model.setUpdate_time(System.currentTimeMillis());
        }
        model.setUserId(userId);
        model.setChapterid(chapterid);
        model.setOrder(chapterorder);
        model.setChaptername(chaptername);
        BookModelDao bookModelDao = manager.getDaoSession().getBookModelDao();
        bookModelDao.insertOrReplace(model);
    }

    /**
     * 根据一本书的id和userId查询这本书对应该用户的记录
     *
     * @return
     */
    public BookModel queryBookModelByBookidAndUid(long bookId, long userId) {
        BookModelDao bookModelDao = manager.getDaoSession().getBookModelDao();
        List<BookModel> list = bookModelDao.queryBuilder().where(BookModelDao.Properties.Id.eq(bookId), BookModelDao.Properties.UserId.eq(userId)).list();
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    /**
     * 根据userid查询阅读记录
     *
     * @warn 使用的时候无需判空处理，默认返回空集合
     */
    public List<BookModel> queryAllReadRecordByUserid(long userId) {
        List<BookModel> list = manager.getDaoSession().getBookModelDao().queryBuilder().where(BookModelDao.Properties.UserId.eq(userId)).orderDesc(BookModelDao.Properties.Update_time).list();
        return list == null ? new ArrayList<BookModel>() : list;
    }

    /**
     * 查询本地所有的阅读记录，userid=0的记录，userid=0表示未上传的记录
     *
     * @return
     */
    public List<BookModel> queryAllReadRecord() {
        List<BookModel> list;
        list = manager.getDaoSession().getBookModelDao().queryBuilder().where(BookModelDao.Properties.UserId.eq(0)).orderDesc(BookModelDao.Properties.Update_time).list();
        return list == null ? new ArrayList<BookModel>() : list;
    }


    //删除已上传的数据
    public void deleteSomeReadRecords() {
//        Iterator<BookModel> iterator = queryAllReadRecordByUserid().iterator();
//        BookModelDao bookModelDao = manager.getDaoSession().getBookModelDao();
//        while (iterator.hasNext()) {
//            BookModel bookModel = iterator.next();
//            if (bookModel.getUserId() > 0) {
//                bookModelDao.delete(bookModel);
//            }
//        }
    }

    /**
     * 查询章节内容
     *
     * @param id
     * @return
     */
    public BookModel getRecordByMainId(int id) {
//        List<ReadRecords> list = manager.getDaoSession().getReadRecordsDao().queryBuilder()
//                .where(ReadRecordsDao.Properties.ObjectId.eq(id))
//                .orderDesc(ReadRecordsDao.Properties.BrowseTime)
//                .list();
//        if (list == null || list.isEmpty()) return null;
        List<BookModel> list = manager.getDaoSession().getBookModelDao().queryBuilder()
                .where(BookModelDao.Properties.Id.eq(id))
                .orderDesc(BookModelDao.Properties.Update_time).list();
        if (list == null || list.isEmpty()) return null;
        return list.get(0);
    }

    /**
     * 插入或更新用户点赞评论信息
     */
    public void insertOrUpdateUserCommentFavorData(UserCommentFavorData data) {
        UserCommentFavorDataDao favorDataDao = manager.getDaoSession().getUserCommentFavorDataDao();
        favorDataDao.insertOrReplace(data);
    }

    /**
     * 根据用户id和评论id获取用户点赞评论数据
     */
    public UserCommentFavorData getUserCommentFavorDataById(long userId, long commentId) {
        UserCommentFavorDataDao favorDataDao = manager.getDaoSession().getUserCommentFavorDataDao();
        List<UserCommentFavorData> list = favorDataDao.queryBuilder().where(UserCommentFavorDataDao.Properties.UserId.eq(userId), UserCommentFavorDataDao.Properties.CommentId.eq(commentId)).list();
        if (list == null || list.size() == 0) {
            return null;
        } else {
            return list.get(0);
        }
    }


    /**
     * 插入或更新用户在线时长数据
     */
    public void insertORupdateOnlineTimeData(OnlineTimeData data){
        OnlineTimeDataDao onlineTimeDataDao = manager.getDaoSession().getOnlineTimeDataDao();
        onlineTimeDataDao.insertOrReplace(data);
    }

    /**
     * 获取该天的当前用户的在线时长数据
     */
    public OnlineTimeData getOnlineTimeData(String date,String uid){
        OnlineTimeDataDao onlineTimeDataDao = manager.getDaoSession().getOnlineTimeDataDao();
        List<OnlineTimeData> list = onlineTimeDataDao.queryBuilder().where(OnlineTimeDataDao.Properties.Date.eq(date),OnlineTimeDataDao.Properties.Uid.eq(uid)).list();
        if (list!=null&&!list.isEmpty()){
            return list.get(0);
        }
        return null;
    }

    /**
     * 插入或更新用户阅读时长数据
     */
    public void insertORupdateReadTimeData(ReadTimeData data){

    }

}

