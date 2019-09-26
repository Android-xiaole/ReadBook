package com.jj.comics.ui.money;

import android.util.Log;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.net.ComicApi;
import com.jj.comics.data.biz.content.ContentRepository;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.BookCatalogContentResponse;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.PayInfoResponse;
import com.jj.comics.data.model.ShareRecommendResponse;
import com.jj.comics.widget.bookreadview.utils.BookRepository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class MoneyPresenter extends BasePresenter <BaseRepository, MoneyFragment> implements MoneyContract.IMoneyPresenter{
    /**
     * 获取首章节内容
     *
     * @param bookModel
     * @param chapterId
     */
    public void getFirstChapterContent(BookModel bookModel, long chapterId) {

        ContentRepository.getInstance().getCatalogContent(bookModel.getId(), chapterId)
                .flatMap(new Function<BookCatalogContentResponse, ObservableSource<ResponseBody>>() {
                    @Override
                    public ObservableSource<ResponseBody> apply(BookCatalogContentResponse bookCatalogContentResponse) throws Exception {
                        if (bookCatalogContentResponse.getCode() == 1000) {
                            return ComicApi.getApi().downloadChapter(bookCatalogContentResponse.getData().getNow().getContent() + Constants.IDENTIFICATION_IGNORE);
                        } else {
                            return Observable.error(new NetError(bookCatalogContentResponse.getMessage(), bookCatalogContentResponse.getCode()));
                        }
                    }
                })
                .flatMap(new Function<ResponseBody, ObservableSource<File>>() {
                    @Override
                    public ObservableSource<File> apply(ResponseBody responseBody) throws Exception {
                        try {
                            File file = BookRepository.getInstance().saveChapterFile(bookModel.getId() + "", chapterId + "", responseBody.byteStream());
                            return Observable.just(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return Observable.error(new NetError("IOException", NetError.OtherError));
                        }
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiSubscriber2<File>() {
                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToastShort(error.getMessage());
                    }

                    @Override
                    public void onNext(File file) {
                        String content = "";
                        try {
                            InputStream instream = new FileInputStream(file);
                            if (instream != null) {
                                InputStreamReader inputreader
                                        = new InputStreamReader(instream, "UTF-8");
                                BufferedReader buffreader = new BufferedReader(inputreader);
                                String line = "";
                                //分行读取
                                while ((line = buffreader.readLine()) != null) {
                                    content += line + "\n";
                                }
                                instream.close();//关闭输入流
                            }
                            getV().shareImage(content);
                        } catch (java.io.FileNotFoundException e) {
                            Log.d("TestFile", "The File doesn't not exist.");
                        } catch (IOException e) {
                            Log.d("TestFile", e.getMessage());
                        }
                    }
                });
    }
    @Override
    public void getUserPayInfo() {
        UserRepository.getInstance().getUserPayInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<PayInfoResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<PayInfoResponse>() {

                    @Override
                    public void onNext(PayInfoResponse payInfoResponse) {
                        getV().onGetUserPayInfo(payInfoResponse.getData());
                    }

                    @Override
                    protected void onFail(NetError error) {
                    }
                });
    }

    @Override
    public void getShareRecommend() {
        UserRepository.getInstance().getShareRecommend()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(bindLifecycle())
                .subscribe(new ApiSubscriber2<ShareRecommendResponse>() {
                    @Override
                    protected void onFail(NetError error) {

                    }

                    @Override
                    public void onNext(ShareRecommendResponse shareRecommendResponse) {
                        List<ShareRecommendResponse.DataBean> data = shareRecommendResponse.getData();
                        if (data != null) {
                            getV().onGetShareRecommend(data);
                        }
                    }
                });
    }
}
