package com.jj.comics.data.db;

import com.jj.base.BaseApplication;
import com.jj.base.log.LogUtil;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.SearchModel;
import com.jj.comics.data.model.UserCommentFavorData;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.data.visittime.OnlineTimeData;
import com.jj.comics.data.visittime.ReadTimeData;
import com.jj.comics.data.visittime.TimeReportData;
import com.jj.comics.greendao.gen.BookModelDao;
import com.jj.comics.greendao.gen.OnlineTimeDataDao;
import com.jj.comics.greendao.gen.ReadTimeDataDao;
import com.jj.comics.greendao.gen.SearchModelDao;
import com.jj.comics.greendao.gen.UserCommentFavorDataDao;
import com.jj.comics.greendao.gen.UserInfoDao;
import com.jj.comics.util.DateHelper;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.SharedPreManger;
import com.umeng.commonsdk.UMConfigure;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    public void insertORupdateOnlineTimeData(int duration,String lastLoginTime,String lastLogoutTime){
        OnlineTimeDataDao onlineTimeDataDao = manager.getDaoSession().getOnlineTimeDataDao();
        String currentDate = DateHelper.getCurrentDate(Constants.DateFormat.YMD);
        String uid = getUid();
        OnlineTimeData onlineTimeData = getOnlineTimeData(currentDate, uid);
        if (onlineTimeData ==null){
            //新增数据
            onlineTimeData = new OnlineTimeData();
            onlineTimeData.setDate(currentDate);
            onlineTimeData.setUid(uid + "");
            onlineTimeData.setDuration(duration);
            if (lastLoginTime == null){
                //这里如果没有值需要设置一个，因为可能是用户登录或者切换账号之后产生的数据，此时也算一个登录时间
                onlineTimeData.setLastLoginTime(DateHelper.getCurrentDate(Constants.DateFormat.YMDHMS));
            }else{
                onlineTimeData.setLastLoginTime(lastLoginTime);
            }
            onlineTimeData.setLastLogoutTime(lastLogoutTime);
            try {
                Long.parseLong(uid);
                onlineTimeData.setIs_visitor(false);
            }catch (Exception e){
                //解析异常代表是游客登录
                onlineTimeData.setIs_visitor(true);
            }
            onlineTimeDataDao.insert(onlineTimeData);
            LogUtil.e("LogTime 插入在线数据:"+getOnlineTimeData(currentDate,uid).toString());
        }else{
            //更新数据
            if (duration >0){
                //累计在线时长
                onlineTimeData.setDuration(onlineTimeData.getDuration()+duration);
            }
            if (lastLoginTime != null){
                //最近登录时间
                onlineTimeData.setLastLoginTime(lastLoginTime);
            }
            if (lastLogoutTime !=null){
                //最近退出时间
                onlineTimeData.setLastLogoutTime(lastLogoutTime);
            }
            onlineTimeDataDao.update(onlineTimeData);
            LogUtil.e("LogTime 更新在线数据:"+getOnlineTimeData(currentDate,uid).toString());
        }
    }

    /**
     * 获取当前用户id，可能是游客，也可能是登录用户
     * @return
     */
    private String getUid() {
        String uid;
        UserInfo onLineUser = LoginHelper.getOnLineUser();
        if (onLineUser != null && onLineUser.getUid() > 0) {
            //登录用户统计
            uid = onLineUser.getUid() + "";
        } else {
            //游客登录统计
            //优先使用UM统计的ID
            String umidString = UMConfigure.getUMIDString(BaseApplication.getApplication());
            if (umidString != null && umidString.length() > 0) {
                //友盟id可用
                uid = umidString;
            } else {
                //没有UM统计id就从本地共享参数里面读取
                String visitorId = SharedPreManger.getInstance().getVisitorId();
                if (visitorId != null) {
                    //本地有游客的id，可直接使用
                    uid = visitorId;
                } else {
                    //本地没有，就使用创建一个UUID
                    String uuid = UUID.randomUUID().toString();
                    //这么处理是为了保持长度和UM的id一致
                    uid = "j-" + uuid.replace("-", "");
                }
            }
            //此时保存游客id到本地
            SharedPreManger.getInstance().saveVisitorId(uid);
        }
        return uid;
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
     * 根据主键查询OnlineTimeData
     * @param id
     * @return
     */
    public OnlineTimeData getOnlineTimeDataBYid(long id){
        OnlineTimeDataDao onlineTimeDataDao = manager.getDaoSession().getOnlineTimeDataDao();
        List<OnlineTimeData> list = onlineTimeDataDao.queryBuilder().where(OnlineTimeDataDao.Properties.Id.eq(id)).list();
        if (list!=null&&!list.isEmpty()){
            return list.get(0);
        }
        return null;
    }

    /**
     * 插入或更新用户阅读时长数据
     */
    public void insertORupdateReadTimeData(int duration,long bookId,long chapterId){
        ReadTimeDataDao readTimeDataDao = manager.getDaoSession().getReadTimeDataDao();
        String currentDate = DateHelper.getCurrentDate(Constants.DateFormat.YMD);
        String uid = getUid();
        ReadTimeData readTimeData = getReadTimeData(currentDate, uid, bookId);
        if (readTimeData == null){
            //新增数据
            readTimeData = new ReadTimeData();
            readTimeData.setDate(currentDate);
            readTimeData.setUid(uid + "");
            readTimeData.setDuration(duration);
            readTimeData.setBoodId(bookId);
            readTimeData.setChapterId(chapterId);
            try {
                Long.parseLong(uid);
                readTimeData.setIs_visitor(false);
            }catch (Exception e){
                //解析异常代表是游客登录
                readTimeData.setIs_visitor(true);
            }
            readTimeDataDao.insert(readTimeData);
            LogUtil.e("LogTime 插入阅读数据:"+getReadTimeData(currentDate,uid,bookId).toString());
        }else{
            //更新数据
            if (duration > 0){
                readTimeData.setDuration(readTimeData.getDuration()+duration);
            }
            if (chapterId > 0){
                readTimeData.setChapterId(chapterId);
            }
            readTimeDataDao.update(readTimeData);
            LogUtil.e("LogTime 更新阅读数据:"+getReadTimeData(currentDate,uid,bookId).toString());
        }

    }

    /**
     * 获取该天的当前用户阅读时长数据
     */
    public ReadTimeData getReadTimeData(String date,String uid,long bookId){
        ReadTimeDataDao readTimeDataDao = manager.getDaoSession().getReadTimeDataDao();
        List<ReadTimeData> list = readTimeDataDao.queryBuilder().where(ReadTimeDataDao.Properties.Date.eq(date), ReadTimeDataDao.Properties.Uid.eq(uid), ReadTimeDataDao.Properties.BoodId.eq(bookId)).list();
        if (list!=null&&!list.isEmpty()){
            return list.get(0);
        }
        return null;
    }

    /**
     * 获取主键查询ReadTimeData
     */
    public ReadTimeData getReadTimeDataBYid(long id){
        ReadTimeDataDao readTimeDataDao = manager.getDaoSession().getReadTimeDataDao();
        List<ReadTimeData> list = readTimeDataDao.queryBuilder().where(ReadTimeDataDao.Properties.Id.eq(id)).list();
        if (list!=null&&!list.isEmpty()){
            return list.get(0);
        }
        return null;
    }

    public List<OnlineTimeData> getOnlineTimeDataAll(){
        OnlineTimeDataDao onlineTimeDataDao = manager.getDaoSession().getOnlineTimeDataDao();
        return onlineTimeDataDao.loadAll();
    }

    public List<ReadTimeData> getReadTimeDataAll(){
        ReadTimeDataDao readTimeDataDao = manager.getDaoSession().getReadTimeDataDao();
        return readTimeDataDao.loadAll();
    }

    /**
     * 删除不是当日的本地时长记录
     * @param data
     * @param isOnlineTime
     */
    public void deleteTimeData(TimeReportData data,boolean isOnlineTime){
        String currentDate = DateHelper.getCurrentDate(Constants.DateFormat.YMD);
        if (data == null||data.getDate() == null||data.getId()<=0){
            //无效数据
            return;
        }
        //如果不是当天数据那就删除
        if (!data.getDate().equals(currentDate)){
            if (isOnlineTime){
                //用户在线时长数据表
                manager.getDaoSession().getOnlineTimeDataDao().deleteByKey(data.getId());
            }else{
                //用户阅读时长数据表
                manager.getDaoSession().getReadTimeDataDao().deleteByKey(data.getId());
            }
        }else{//是当天数据并且是当前用户就不删除，清空duration字段，因为该条数据还可以重复利用,否则还是删除
            //获取当前用户id
            String uid = getUid();
            //如果是游客登录，那就用token字段去匹配；如果是登录用户，那就用uid字段去匹配是否为当前用户
            if (data.isIs_visitor()&&uid.equals(data.getToken())||!data.isIs_visitor()&&uid.equals(data.getUid()+"")){
                //如果是当前用户，清空duration字段并更新
                if (isOnlineTime){
                    //用户在线时长数据表
                    OnlineTimeData onlineTimeData = getOnlineTimeDataBYid(data.getId());
                    if (onlineTimeData!=null){
                        //清空用户在线时长
                        onlineTimeData.setDuration(0);
                        manager.getDaoSession().getOnlineTimeDataDao().update(onlineTimeData);
                    }
                }else{
                    //用户阅读时长数据表
                    ReadTimeData readTimeData = getReadTimeDataBYid(data.getId());
                    if (readTimeData!=null){
                        //清空用户阅读时长
                        readTimeData.setDuration(0);
                        manager.getDaoSession().getReadTimeDataDao().update(readTimeData);
                    }
                }
            }else{
                //是当天数据，但不是当前用户也直接删除数据
                if (isOnlineTime){
                    //用户在线时长数据表
                    manager.getDaoSession().getOnlineTimeDataDao().deleteByKey(data.getId());
                }else{
                    //用户阅读时长数据表
                    manager.getDaoSession().getReadTimeDataDao().deleteByKey(data.getId());
                }
            }
        }
    }
}

