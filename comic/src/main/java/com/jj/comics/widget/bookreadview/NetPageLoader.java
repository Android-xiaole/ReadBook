package com.jj.comics.widget.bookreadview;



import com.jj.comics.widget.bookreadview.bean.BookChapterBean;
import com.jj.comics.widget.bookreadview.bean.CollBookBean;
import com.jj.comics.widget.bookreadview.utils.BookManager;
import com.jj.comics.widget.bookreadview.utils.BookRepository;
import com.jj.comics.widget.bookreadview.utils.Constant;
import com.jj.comics.widget.bookreadview.utils.FileUtils;
import com.jj.comics.widget.bookreadview.utils.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by newbiechen on 17-5-29.
 * 网络页面加载器
 */

public class NetPageLoader extends PageLoader {
    private static final String TAG = "PageFactory";

    public NetPageLoader(PageView pageView, CollBookBean collBook) {
        super(pageView, collBook);
    }

    private List<TxtChapter> convertTxtChapter(List<BookChapterBean> bookChapters) {
        List<TxtChapter> txtChapters = new ArrayList<>(bookChapters.size());
        for (BookChapterBean bean : bookChapters) {
            TxtChapter chapter = new TxtChapter();
            chapter.bookId = bean.getBookId();
            chapter.title = bean.getTitle();
            chapter.link = bean.getLink();
            chapter.chapterId = bean.getId();
            chapter.needLogin = bean.isNeedLogin();
            txtChapters.add(chapter);
        }
        return txtChapters;
    }

    @Override
    public void refreshChapterList() {
        if (mCollBook.getBookChapters() == null) return;

        // 将 BookChapter 转换成当前可用的 Chapter
        mChapterList = convertTxtChapter(mCollBook.getBookChapters());
        isChapterListPrepare = true;

        // 目录加载完成，执行回调操作。
        if (mPageChangeListener != null) {
            mPageChangeListener.onCategoryFinish(mChapterList);
        }

        /*
            下面这块代码先注释掉,源代码加这个是为了默认打开第一章，
            但在使用的时候我会调用skipToChapter方法，
            所以以下代码可以不加，加上会导致默认缓存前两个章节
         */
//        // 如果章节未打开
//        if (!isChapterOpen()) {
//            // 打开章节
//            openChapter();
//        }
    }

    @Override
    protected BufferedReader getChapterReader(TxtChapter chapter) throws Exception {
        File file = new File(Constant.BOOK_CACHE_PATH + mCollBook.get_id()
                + File.separator + chapter.title + FileUtils.SUFFIX_NB);
        if (!file.exists()) return null;

        Reader reader = new FileReader(file);
        BufferedReader br = new BufferedReader(reader);
        return br;
    }

    @Override
    protected boolean hasChapterData(TxtChapter chapter) {
        return BookManager.isChapterCached(mCollBook.get_id(), chapter.title);
    }

    // 装载上一章节的内容
    @Override
    boolean parsePrevChapter() {
        boolean isRight = super.parsePrevChapter();

        if (mStatus == STATUS_FINISH) {
            loadPrevChapter();
        } else if (mStatus == STATUS_LOADING) {
            loadCurrentChapter();
        }
        return isRight;
    }

    // 装载当前章内容。
    @Override
    boolean parseCurChapter() {
        boolean isRight = super.parseCurChapter();

        if (mStatus == STATUS_LOADING) {
            loadCurrentChapter();
        }
        return isRight;
    }

    // 装载下一章节的内容
    @Override
    boolean parseNextChapter() {
        boolean isRight = super.parseNextChapter();

        if (mStatus == STATUS_FINISH) {
            loadNextChapter();
        } else if (mStatus == STATUS_LOADING) {
            loadCurrentChapter();
        }

        return isRight;
    }

    /**
     * 加载当前页的前面两个章节
     */
    private void loadPrevChapter() {
        if (mPageChangeListener != null) {
            int end = mCurChapterPos;
            int begin = end - 2;
            if (begin < 0) {
                begin = 0;
            }

            requestChapters(begin, end);
        }
    }

    /**
     * 加载前一页，当前页，后一页。
     */
    private void loadCurrentChapter() {
        if (mPageChangeListener != null) {
            int begin = mCurChapterPos;
            int end = mCurChapterPos;

            // 是否当前不是最后一章
            if (end < mChapterList.size()) {
                end = end + 1;
                if (end >= mChapterList.size()) {
                    end = mChapterList.size() - 1;
                }
            }

            // 如果当前不是第一章
            if (begin != 0) {
                begin = begin - 1;
                if (begin < 0) {
                    begin = 0;
                }
            }

            requestChapters(begin, end);
        }
    }

    /**
     * 加载当前页的后两个章节
     */
    private void loadNextChapter() {
        if (mPageChangeListener != null) {

            // 提示加载后两章
            int begin = mCurChapterPos + 1;
            int end = begin + 1;

            // 判断是否大于最后一章
            if (begin >= mChapterList.size()) {
                // 如果下一章超出目录了，就没有必要加载了
                return;
            }

            if (end > mChapterList.size()) {
                end = mChapterList.size() - 1;
            }

            requestChapters(begin, end);
        }
    }

    /**
     * 这里是计算预加载的主要逻辑
     * @param start
     * @param end
     */
    private void requestChapters(int start, int end) {
        List<TxtChapter> chapters = new ArrayList<>();
        TxtChapter txtChapter = mChapterList.get(mCurChapterPos);
        if (!hasChapterData(txtChapter)){//如果缓存中不存在就去网络加载
            chapters.add(txtChapter);
            mPageChangeListener.requestChapters(chapters);
        }
//        // 检验输入值
//        if (start < 0) {
//            start = 0;
//        }
//
//        if (end >= mChapterList.size()) {
//            end = mChapterList.size() - 1;
//        }
//
//
//        List<TxtChapter> chapters = new ArrayList<>();
//
//        // 过滤，哪些数据已经加载了
//        for (int i = start; i <= end; ++i) {
//            TxtChapter txtChapter = mChapterList.get(i);
//            if (!hasChapterData(txtChapter)) {
//                /*
//                如果isPaid=false,表明当前章节已经请求过了，因为初始值设置为true
//                所以只够为true的时候才去网络请求
//                 */
//                if(txtChapter.isPaid()){
//                    chapters.add(txtChapter);
//                }
//            }
//        }

        if (!chapters.isEmpty()) {
            mPageChangeListener.requestChapters(chapters);
        }
    }

    @Override
    public void saveRecord() {
        super.saveRecord();
        if (mCollBook != null && isChapterListPrepare) {
            //表示当前CollBook已经阅读
            mCollBook.setIsUpdate(false);
            mCollBook.setLastRead(StringUtils.
                    dateConvert(System.currentTimeMillis(), Constant.FORMAT_BOOK_DATE));
            //直接更新
            BookRepository.getInstance()
                    .saveCollBook(mCollBook);
        }
    }
}

