package com.jj.comics.ui.read;

import android.util.Log;

import com.jj.base.log.LogUtil;
import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber;
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
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.CommonStatusResponse;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.ReadComicHelper;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.eventbus.events.UpdateReadHistoryEvent;
import com.jj.comics.widget.bookreadview.TxtChapter;
import com.jj.comics.widget.bookreadview.utils.BookRepository;

import org.reactivestreams.Publisher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;


public class ReadComicPresenter extends BasePresenter<BaseRepository, ReadComicContract.IReadComicView> implements ReadComicContract.IReadComicPresenter {
    private ApiSubscriber2<BookCatalogModel> subscriber;
    private DaoHelper<BookModel> daoHelper = new DaoHelper<>();

    public void loadData(BookModel bookModel, List<TxtChapter> requestChapters) {
        getV().showProgress();
        /*
        这里加载的内容时候需要取消上一个subscriber事件，保证一时间只有一个章节内容加载
        防止出现同时加载多个章节数据错乱的问题
         */
        if (subscriber != null && !subscriber.isDisposed()) {
            subscriber.dispose();
        }
        subscriber = new ComicSubscriber<BookCatalogModel>() {
            @Override
            public void onNext(BookCatalogModel model) {
                subscriber = null;
                getV().fillData(model);
            }

            @Override
            protected void onFail(NetError error) {
                subscriber = null;
                ToastUtil.showToastShort(error.getMessage());
            }

            @Override
            protected void onEnd() {
                getV().hideProgress();
                getV().onLoadDataEnd();
            }
        };
        List<Observable<BookCatalogModel>> requests = new ArrayList<>();
        for (TxtChapter requestChapter : requestChapters) {
            requests.add(ReadComicHelper.getComicHelper().getBookCatalogContent((BaseActivity) getV(), bookModel, Long.parseLong(requestChapter.getChapterId())));
        }
        Observable.concat(requests)
        //订阅漫画封装类
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<BookCatalogModel>bindLifecycle())
                .subscribe(subscriber);
    }

    public void downloadFile(BookCatalogModel catalogModel){
        ComicApi.getApi().downloadFile(catalogModel.getUrl()+Constants.IDENTIFICATION_IGNORE)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<ResponseBody, Publisher<File>>() {
                    @Override
                    public Publisher<File> apply(ResponseBody responseBody) throws Exception {
                        try {
                            File file = BookRepository.getInstance().saveChapterFile(catalogModel.getBook_id() + "", catalogModel.getChapterorder() + "", responseBody.byteStream());
                            return Flowable.just(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return Flowable.error(new NetError("IOException",NetError.OtherError));
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .as(bindLifecycle())
                .subscribe(new ApiSubscriber<File>() {
                    @Override
                    public void onNext(File file) {
                        getV().onLoadChapterContent();
                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToastShort(error.getMessage());
                    }

                    @Override
                    protected void onEnd() {
                        super.onEnd();
                        getV().hideProgress();
                    }
                });

    }


    /**
     * @param model
     * @param needFinish true:代表操作成功后需要退出当前界面，也就是弹窗上的那个收藏操作
     *                   false:表示是ComicView上面的子view的收藏操作，不需要退出界面
     */
    @Override
    public void addOrRemoveShelf(final BookModel model, final boolean collectByCurrUser, final boolean needFinish) {
        getV().showProgress();
        List<BookModel> models = new ArrayList<>();
        models.add(model);
        Observable<CommonStatusResponse> observable = collectByCurrUser
                ? UserRepository.getInstance().unCollect(models, getV().getClass().getName())
                : UserRepository.getInstance().collect(model.getId(), getV().getClass().getName());
        observable.observeOn(AndroidSchedulers.mainThread())
                .as(this.<CommonStatusResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<CommonStatusResponse>() {
                    @Override
                    public void onNext(CommonStatusResponse response) {
                        if (response.getData().getStatus()) {
                            getV().onAddOrRemoveShelfSuccess(!collectByCurrUser, needFinish);
                        } else {
                            ToastUtil.showToastShort(response.getMessage());
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToastShort(error.getMessage());
                    }

                    @Override
                    protected void onEnd() {
                        super.onEnd();
                        getV().hideProgress();
                    }
                });
    }

    @Override
    public void getCollectStatus(long id) {
        UserRepository.getInstance().getCollectStatus(id, getV().getClass().getSimpleName())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<CommonStatusResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<CommonStatusResponse>() {
                    @Override
                    public void onNext(CommonStatusResponse response) {
                        getV().fillCollectStatus(response);
                    }

                    @Override
                    protected void onFail(NetError error) {

                    }
                });

    }

    @Override
    public void getFavorStatus(long id) {
        UserRepository.getInstance().getFavorStatus(id, getV().getClass().getSimpleName())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<CommonStatusResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<CommonStatusResponse>() {
                    @Override
                    public void onNext(CommonStatusResponse response) {
                        getV().fillFavorStatus(response);
                    }

                    @Override
                    protected void onFail(NetError error) {

                    }
                });
    }

    /**
     * 获取目录列表
     */
    @Override
    public void getCatalogList(long bookId, int pageNum) {
        if (getV() == null)return;
        ContentRepository.getInstance().getCatalogList(bookId, pageNum, Constants.RequestBodyKey.SORT_ASC)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<BookCatalogListResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<BookCatalogListResponse>() {
                    @Override
                    public void onNext(BookCatalogListResponse response) {
                        getV().onGetCatalogList(response.getData().getData());
                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().onGetCatalogListFail();
                    }

                    @Override
                    protected void onEnd() {
                        super.onEnd();
                        getV().hideProgress();
                    }
                });

    }

    @Override
    public void uploadReadRecord(final BookModel bookModel, final long chapterId,final int chapterorder) {
        final UserInfo loginUser = LoginHelper.getOnLineUser();
        //现将model以userId = 0的状态下保存到本地，代表未上传
        daoHelper.insertOrUpdateRecord(bookModel, 0, chapterId,chapterorder);
        if (loginUser == null) {
            //未登录直接发送通知，不要去做上传处理，因为没有token
            EventBusManager.sendUpdateReadRecord(new UpdateReadHistoryEvent(chapterId,chapterorder));
            return;
        }
        List<BookModel> bookModels = new ArrayList<>();
        bookModels.add(bookModel);
        UserRepository.getInstance().syncHistory(bookModels)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new ApiSubscriber2<CommonStatusResponse>() {
                    @Override
                    public void onNext(CommonStatusResponse commonStatusResponse) {
                        Log.i("sync history:", commonStatusResponse.getMessage());
                        //上传成功之后直接删除
                        daoHelper.delete(bookModel);
                    }

                    @Override
                    protected void onFail(NetError error) {
                    }

                    @Override
                    protected void onEnd() {
                        EventBusManager.sendUpdateReadRecord(new UpdateReadHistoryEvent(chapterId,chapterorder));
                    }
                });
    }

    /**
     * 点赞
     */
    @Override
    public void favorContent(long bookId) {
        getV().showProgress();
        UserRepository.getInstance().favorContent(bookId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<CommonStatusResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<CommonStatusResponse>() {
                    @Override
                    public void onNext(CommonStatusResponse response) {
                        if (response.getData().getStatus()) {
                            getV().onFavorContentSuccess();
                        } else {
                            onFail(new NetError(response.getMessage(), response.getCode()));
                        }
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
    public void sendComment(long bookId, String commentDetail) {
        getV().showProgress();
        ContentRepository.getInstance().sendComment(bookId, commentDetail)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<CommonStatusResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<CommonStatusResponse>() {
                    @Override
                    public void onNext(CommonStatusResponse result) {
                        if (result.getData().getStatus()) {
                            getV().onCommentSuccess(result);
                        } else {
                            getV().showToastShort(result.getMessage());
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().showToastShort(error.getMessage());
                    }

                    @Override
                    protected void onEnd() {
                        getV().hideProgress();
                    }
                });
    }

}
