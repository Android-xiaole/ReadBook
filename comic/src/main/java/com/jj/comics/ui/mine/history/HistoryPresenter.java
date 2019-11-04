package com.jj.comics.ui.mine.history;

import android.util.Log;

import com.jj.base.log.LogUtil;
import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.net.ComicApi;
import com.jj.comics.common.net.ComicSubscriber;
import com.jj.comics.data.biz.content.ContentRepository;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.db.DaoHelper;
import com.jj.comics.data.model.BookCatalogListResponse;
import com.jj.comics.data.model.BookCatalogModel;
import com.jj.comics.data.model.BookListResponse;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.CollectionResponse;
import com.jj.comics.data.model.CommonStatusResponse;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.ui.read.ReadComicActivity;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.ReadComicHelper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;

public class HistoryPresenter extends BasePresenter<BaseRepository, HistoryContract.IHistoryView> implements HistoryContract.IHistoryPresenter {
    private DaoHelper<BookModel> mDaoHelper;
    private Map<Long, BookModel> requestMap;
    private Map<Long, BookModel> localMap;
    public static final boolean SHOW_LOCAL_DATA = true;
    public static boolean IS_LIVING = false;

    private final static boolean IS_DEBUG = false;

    public HistoryPresenter() {
        super();
        IS_LIVING = true;
        mDaoHelper = new DaoHelper<>();
        requestMap = new LinkedHashMap<>();
        localMap = new LinkedHashMap<>();
    }

    @Override
    public void getHistoryList() {
        requestMap.clear();
        localMap.clear();
        UserInfo userInfo = LoginHelper.getOnLineUser();
        if (userInfo == null) {
            getV().onLoadHistoryList(mDaoHelper.queryAllReadRecord());
            return;
        }

        UserRepository.getInstance().getHistoryList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<CollectionResponse, List<BookModel>>() {
                    @Override
                    public List<BookModel> apply(CollectionResponse collectionResponse) throws Exception {
                        //在这里做一下本地数据和服务端数据的合并操作
                        List<BookModel> localBookModels = mDaoHelper.queryAllReadRecord();
                        List<BookModel> contractList = new ArrayList<>();
                        if (collectionResponse.getData()!=null&&collectionResponse.getData().getData()!=null){

                            //遍历本地数据
                            for (BookModel localBookModel : localBookModels) {
                                //用本地记录去覆盖服务端的记录，因为本地一定是最新的记录
                                localMap.put(localBookModel.getId(),localBookModel);
                            }
                            for (BookModel bookModel : collectionResponse.getData().getData()) {
                                if (!localMap.containsKey(bookModel.getId())) {
                                    requestMap.put(bookModel.getId(),bookModel);
                                }
                            }
                            localMap.putAll(requestMap);
                            for (Map.Entry<Long,BookModel> entry : localMap.entrySet()) {
                                contractList.add(entry.getValue());
                            }
                            return contractList;
                        }else{//如果服务端没有数据就显示本地的数据
                            return localBookModels;
                        }
                    }
                })
                .as(this.<List<BookModel>>bindLifecycle())
                .subscribe(new ApiSubscriber2<List<BookModel>>() {
                    @Override
                    public void onNext(List<BookModel> bookModels) {
                        getV().onLoadHistoryList(bookModels);
                    }

                    @Override
                    protected void onFail(NetError error) {
                    }

                    @Override
                    protected void onEnd() {
                        getV().hideProgress();
                        uploadRecord();
                    }
                });
    }

    @Override
    public void loadRecommendData() {
        ContentRepository.getInstance().getWatchingComicData(getV().getClass().getName())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<BookListResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<BookListResponse>() {
                    @Override
                    public void onNext(BookListResponse bookListResponse) {
                        List<BookModel> bookModelList = bookListResponse.getData();
                        if (bookModelList != null) {
                            getV().onLoadRecommendList(bookModelList);
                        } else {
                            getV().getFooterDataFail(new NetError("DATA EMPTY", NetError.NoDataError));
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().getFooterDataFail(error);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    public void uploadRecord() {
        final UserInfo onLineUser = LoginHelper.getOnLineUser();
        //未登录不处理
        if (onLineUser == null)return;
        //查询出本地未上传的记录
        final List<BookModel> localUnloadRecord = mDaoHelper.queryAllReadRecordByUserid(0);
        //没有未上传的记录也不处理
        if (localUnloadRecord.isEmpty())return;

        UserRepository.getInstance().syncHistory(localUnloadRecord)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiSubscriber2<CommonStatusResponse>() {
                    @Override
                    public void onNext(CommonStatusResponse commonStatusResponse) {
                        Log.i("sync",commonStatusResponse.getMessage());
                        //上传成功之后直接删除本地记录
                        for (BookModel localBookModel : localUnloadRecord) {
                            localBookModel.setUserId(onLineUser.getUid());
                            mDaoHelper.delete(localBookModel);
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        Log.i("sync",error.getMessage());
                    }
                });
    }

    @Override
    public void toRead(final BookModel bookModel, final long chapterid) {
        getV().showProgress();
        Observable.just(chapterid)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<Long, ObservableSource<Long>>() {
                    @Override
                    public ObservableSource<Long> apply(Long chapterid) throws Exception {
                        if (chapterid == 0){//没有阅读记录就默认取章节目录的第一张
                            /*
                                这里接口做了缓存处理，针对chapterid字段来说，章节目录数据源一般不会实时变动
                                另一方面增强用户体验，立即阅读按钮点击频率是比较高了，能少开线程最好
                             */
                            return ContentRepository.getInstance().getNewCatalogList(bookModel.getId(),1,Constants.RequestBodyKey.SORT_ASC,bookModel.getUpdate_chapter_time())
                                    .subscribeOn(Schedulers.io())
                                    .flatMap(new Function<BookCatalogListResponse, ObservableSource<Long>>() {
                                        @Override
                                        public ObservableSource<Long> apply(BookCatalogListResponse response) throws Exception {
                                            return Observable.just(response.getData().getData().get(0).getId());
                                        }
                                    });
                        }else{
                            return Observable.just(chapterid);
                        }
                    }
                }).flatMap(new Function<Long, ObservableSource<BookCatalogModel>>() {
            @Override
            public ObservableSource<BookCatalogModel> apply(Long chapterid) throws Exception {
                return ReadComicHelper.getComicHelper().getBookCatalogContent(((BaseActivity)getV()),bookModel,chapterid)
                        .subscribeOn(Schedulers.io());
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .as(this.<BookCatalogModel>bindLifecycle())
                .subscribe(new ComicSubscriber<BookCatalogModel>() {
                    @Override
                    public void onNext(BookCatalogModel catalogModel) {
                        ReadComicActivity.toRead(((BaseActivity)getV()),bookModel,
                                catalogModel);
                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToastShort(error.getMessage());
                    }

                    @Override
                    protected void onEnd() {
                        getV().hideProgress();
                    }
                });
    }

    @Override
    public void deleteHistory(List<BookModel> delete,int position) {
        if (delete == null || delete.size() == 0) {
            return;
        }
        if (LoginHelper.getOnLineUser() == null) {
            //未登录状态下显示的都是本地数据，直接删除即可
            for (BookModel bookModel : delete) {
                mDaoHelper.delete(bookModel);
            }
            getV().onDeleteComplete(position);
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < delete.size(); i++) {
            builder.append(delete.get(i).getId()).append(",");
        }
        String ids = builder.substring(0, builder.length() - 1).toString();
        //删除历史
        UserRepository.getInstance().deleteReadRecord(ids)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<CommonStatusResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<CommonStatusResponse>() {
                    @Override
                    public void onNext(CommonStatusResponse commonStatusResponse) {
                        if (commonStatusResponse.getData().getStatus()) {
                            //删除操作成功，也要删除本地记录
                            for (BookModel bookModel : delete) {
                                if (bookModel.getUserId() == 0){
                                    mDaoHelper.delete(bookModel);
                                }
                            }
                            getV().onDeleteComplete(position);
                        } else {
                            ToastUtil.showToastShort(commonStatusResponse.getMessage());
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        Log.i("del", error.getMessage());
                    }
                });
    }

    @Override
    public void detachV() {
        IS_LIVING = false;
        super.detachV();
    }

    private void e(String msg) {
        if (IS_DEBUG)
            LogUtil.e("HistoryPresenter--------------->" + msg);
    }
}
