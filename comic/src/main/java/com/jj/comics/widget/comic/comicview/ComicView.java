package com.jj.comics.widget.comic.comicview;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jj.base.log.LogUtil;
import com.jj.base.utils.CommonUtil;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.Utils;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.detail.ReadComicAdapter;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.constants.RequestCode;
import com.jj.comics.data.model.BookCatalogModel;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.widget.comic.ComicLinearLayoutManager;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public class ComicView extends CustomView implements BaseQuickAdapter.OnItemChildClickListener, View.OnTouchListener, GestureDetector.OnGestureListener, BaseQuickAdapter.OnItemClickListener {

    @BindView(R2.id.comic_read_recycler)
    public RecyclerView mRecyclerView;
    @BindView(R2.id.comic_read_top)
    TopBar mTopBar;
    @BindView(R2.id.comic_read_bottom)
    public BottomBar mBottomBar;
    @BindView(R2.id.comic_read_title)
    TextView mTitleView;
    @BindView(R2.id.comic_read_remind)
    RemindView mRemindView;
    private ReadComicAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    //自定义常量  漫画缓存长度
    private static final int COMIC_CACHE_SIZE = 3;
    /**
     * 漫画map缓存 只缓存{@link #COMIC_CACHE_SIZE}章
     * key：漫画章节
     * value：漫画内容
     */
    private SparseArray<BookCatalogModel> mCache;

    private BookModel mComicDetailModel;//漫画的详细信息

    private TextView mHeadView, mFooterView;
    private boolean isLoading = true;
    private boolean mEnableLoadMore = true, mEnableUpFetch = true;

    private static int SCROLL_HEIGHT;//音量键翻页高度 默认滑动屏幕一半
    private static final int FLING_HEIGHT = 50;//手指滑动加载漫画 高度阈值

    public GestureDetector mGestureDetector;
    private boolean isScrollToModel = false;//加载漫画时是否定位到展示model

    private ComicViewChildViewOnclickListener comicViewChildViewOnclickListener;

    public interface ComicViewChildViewOnclickListener {
        void onCollectiClick();//收藏操作的点击事件

        void onCommentClick();//评论操作的点击事件

        void onFavorClick();//点赞操作的点击事件

        void onCatalogClick();//目录列表的点击事件

        void onLoadNextOrLastCatalogClick(BookCatalogModel catalogModel, boolean next);//上一话或者下一话的点击事件

        void onRewardClick();//打赏的点击事件

        void onShareClick();//分享的点击事件

        void onBack();//返回按钮时间
    }

    public void addComicViewChildViewOnclickListener(ComicViewChildViewOnclickListener comicViewChildViewOnclickListener) {
        if (comicViewChildViewOnclickListener != null) {
            this.comicViewChildViewOnclickListener = comicViewChildViewOnclickListener;
            mBottomBar.setComicViewChildViewOnclickListener(comicViewChildViewOnclickListener);
            mTopBar.setComicViewChildViewOnclickListener(comicViewChildViewOnclickListener);
        }
    }

    public void removeComicViewChildViewOnclickListener() {
        if (comicViewChildViewOnclickListener != null) {
            this.comicViewChildViewOnclickListener = null;
            if (mBottomBar != null) {
                mBottomBar.setComicViewChildViewOnclickListener(null);
            }
            if (mTopBar != null) {
                mTopBar.setComicViewChildViewOnclickListener(null);
            }
        }
    }

    public ComicView(Context context) {
        super(context);
    }


    public ComicView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ComicView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initData(Context context, AttributeSet attrs) {
        mCache = new SparseArray<>();
        int screenHeight = Utils.getScreenHeight(getContext());
        SCROLL_HEIGHT = screenHeight / 2;
//        FLING_HEIGHT = screenHeight / 10;
        mComicDetailModel = (BookModel) getActivity().getIntent().getSerializableExtra(Constants.IntentKey.BOOK_MODEL);

        // TODO: 2019-07-24 已经在ReadComicActivity网络请求返回成功之后处理了
//        mBottomBar.initView(mComicDetailModel.isCollectByCurrUser());
//        mBottomBar.initView(false);
        mGestureDetector = new GestureDetector(context, this);
        initRecyclerView(context);
        show();
//        changeReadMode(SharedPref.getInstance(context).getInt(Constants.READ_MODE_KEY, 2)
//                , SharedPref.getInstance(context).getInt(Constants.SCREEN_MODE_KEY, 1));
    }

    private void initRecyclerView(Context context) {
        mLinearLayoutManager = new ComicLinearLayoutManager(context);
        mRecyclerView.setOnTouchListener(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                updateMsg();
                if (dx != 0 || dy != 0)
                    hide();
            }
        });

        mAdapter = new ReadComicAdapter(getActivity(), mComicDetailModel);
        mAdapter.bindToRecyclerView(mRecyclerView);
        mAdapter.closeLoadAnimation();
        mAdapter.setOnItemChildClickListener(this);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setFootClickListener(this);
    }

    private void updateMsg() {
//        int firstPosition = findFirstVisiblePosition();
//        BookCatalogModel catalog = mAdapter.getCatalog(firstPosition);
//        if (catalog == null)return;
//        String nowTitle = getNowTitle(catalog);
//        if (!TextUtils.equals(nowTitle, mTitleView.getText()))
//            mTitleView.setText(nowTitle);
//        if (catalog != null) {
//            List<String> imageUrls = catalog.getContent();
//            String url = mAdapter.getData().get(firstPosition).getUrl();
//            if (TextUtils.isEmpty(url) && firstPosition > 1) {
//                //如果是尾部局 取上一个
//                url = mAdapter.getData().get(firstPosition - 1).getUrl();
//            }
//            mRemindView.setProgress(catalog.getChapterorder(), imageUrls.indexOf(url) + 1, imageUrls.size());
//        }
    }

    public int findFirstVisiblePosition() {
        int itemPosition = -1;
        if (mLinearLayoutManager != null)
            itemPosition = mLinearLayoutManager.findFirstVisibleItemPosition();
        if (itemPosition == 0) return 0;
        return itemPosition - (mAdapter == null ? 0 : mAdapter.getHeaderLayoutCount());
    }

    /**
     * 根据当前图片显示标题
     *
     * @return
     */
    private String getNowTitle(BookCatalogModel model) {
        String title = "";
        if (mComicDetailModel != null)
            title += mComicDetailModel.getTitle().trim();
        if (mAdapter != null && model != null) {
            if (model.getChaptername() != null) {
                if (model != null) title += ":" + model.getChaptername().trim();
            }
        }
        return title;
    }

    public void refreshReward() {
        mAdapter.getRewardRecordList(getActivity());
    }

    public void refreshComicDetail() {
        mAdapter.setComicDetail();
    }

    /**
     * 展示漫画并处理头尾布局
     *
     * @param model 待展示的漫画
     */
    public void addCatalog(BookCatalogModel model) {
//        if (model != null) {
//            int subSeq = model.getChapterorder();
//            // TODO: 2019-07-24 need help  :getSubCount?
//            if (subSeq > 0 /*&& subSeq <= mComicDetailModel.getSubCount()*/ && !CommonUtil.checkEmpty(model.getContent())) {
//                //添加数据到缓存
//                if (mCache.size() >= COMIC_CACHE_SIZE) {
//                    //删除最不可能加载到的catalog 即距离待展示model距离最远的缓存 SparseArray的key值升序
//                    if (Math.abs(subSeq - mCache.keyAt(0)) >= Math.abs(subSeq - mCache.keyAt(mCache.size() - 1))) {
//                        //如果待展示漫画距离第一个元素较远  则删除第一个元素
//                        mCache.removeAt(0);
//                    } else {
//                        mCache.removeAt(mCache.size() - 1);
//                    }
//                }
//                mCache.put(subSeq, model);
//            }
            //更新topBar标题 以及remind文本 mAdapter添加数据之后可能造成获取第一条数据有问题 故需post调用
//            post(new Runnable() {
//                @Override
//                public void run() {
//                    updateMsg();
//                }
//            });
            //处理头尾布局
//            mAdapter.removeAllHeaderView();
//            mAdapter.removeAllFooterView();
//            if (model.isHasNext() && !model.isHasLast()) {
//                //有下一话，没有上一话
//                enableUpFetch(false);
//                enableLoadMore(true);
//                if (mHeadView == null)
//                    mHeadView = createView(getContext().getString(R.string.comic_front_no_more));
//                if (mAdapter.getHeaderLayoutCount() == 0) mAdapter.addHeaderView(mHeadView, 0);
//                if (mFooterView != null && mAdapter.getFooterLayoutCount() == 1)
//                    mAdapter.removeFooterView(mFooterView);
//            } else if (model.isHasLast() && model.isHasNext()) {
//                //有上一话也有在下一话
//                enableUpFetch(true);
//                enableLoadMore(true);
//                if (mHeadView != null && mAdapter.getHeaderLayoutCount() == 1)
//                    mAdapter.removeHeaderView(mHeadView);
//                if (mFooterView != null && mAdapter.getFooterLayoutCount() == 1)
//                    mAdapter.removeFooterView(mFooterView);
//            } else if (!model.isHasNext() && model.isHasLast()) {
//                //没有下一话，有上一话
//                enableUpFetch(true);
//                enableLoadMore(false);
//                if (mHeadView != null && !model.isHasLast() && mAdapter.getHeaderLayoutCount() == 1)
//                    mAdapter.removeHeaderView(mHeadView);
//                if (mFooterView == null)
//                    mFooterView = createView(getContext().getString(R.string.comic_behind_no_more));
//                if (mAdapter.getFooterLayoutCount() == 0) mAdapter.addFooterView(mFooterView);
//            } else {
//                //上一话下一话都没有
//                enableUpFetch(false);
//                enableLoadMore(false);
//                if (mHeadView == null)
//                    mHeadView = createView(getContext().getString(R.string.comic_front_no_more));
//                if (mAdapter.getHeaderLayoutCount() == 0) mAdapter.addHeaderView(mHeadView, 0);
//                if (mFooterView == null)
//                    mFooterView = createView(getContext().getString(R.string.comic_behind_no_more));
//                if (mAdapter.getFooterLayoutCount() == 0) mAdapter.addFooterView(mFooterView);
//            }
//        }
//        //展示漫画,一定要处理完头尾布局之后再加载漫画
//        mAdapter.addCatalog(model, isScrollToModel);
//        //重置ScrollToModel
//        isScrollToModel = false;
//        //每次添加数据完成后重置加载状态
//        setLoading(false);
    }

    private void logCache() {
        if (Constants.DEBUG && mCache != null) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < mCache.size(); i++) {
                stringBuilder.append(mCache.keyAt(i) + "--");
            }
            stringBuilder.append("  size:" + mCache.size());
            LogUtil.e(stringBuilder);
        }
    }

    private void enableUpFetch(boolean enable) {
        if (enable != mEnableUpFetch) mEnableUpFetch = enable;
    }

    private void enableLoadMore(boolean enable) {
        if (enable != mEnableLoadMore) mEnableLoadMore = enable;
    }

    private TextView createView(String text) {
        TextView view = new TextView(getContext());
        view.setTextColor(getResources().getColor(R.color.comic_646464));
        view.setText(text);
        view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        int padding = (int) getResources().getDimension(R.dimen.dp_15);
        view.setGravity(Gravity.CENTER);
        view.setPadding(padding, padding, padding, padding);
        view.setLayoutParams(layoutParams);
        return view;
    }

    public void turnPage(boolean up) {
        if (!mLinearLayoutManager.isSmoothScrolling() && mLinearLayoutManager.canScrollVertically()) {
            mRecyclerView.smoothScrollBy(0, up ? -SCROLL_HEIGHT : SCROLL_HEIGHT);
        }
    }

    /**
     * 加载漫画
     *
     * @param next true 加载下一章 false加载上一章
     */
    private void loadComic(boolean next) {
        BookCatalogModel catalogModel = mAdapter.getCatalog(findFirstVisiblePosition());
        if (catalogModel != null) {
            int sub = next ? catalogModel.getChapterorder() + 1 : catalogModel.getChapterorder() - 1;
            // TODO: 2019-07-24  getSubCount?
            if (sub > 0 /*&& sub <= mComicDetailModel.getSubCount()*/) {
                setLoading(true);
                BookCatalogModel expectModel = mCache.get(sub);
                if (expectModel != null) {
                    addCatalog(expectModel);
                    setLoading(false);
                } else {
//                    getLastOrNext(catalogModel, next);
                    comicViewChildViewOnclickListener.onLoadNextOrLastCatalogClick(catalogModel, next);
                }
            }
        }
    }

    /**
     * 点击目录调用
     *
     * @param model 返回值：true-需要重新加载 false-自动从缓存中获取
     */
    public boolean getComic(BookCatalogModel model) {
        setLoading(true);
        isScrollToModel = true;
        BookCatalogModel expectModel = mCache.get(model.getChapterorder());
        if (expectModel != null) {
            addCatalog(expectModel);
            setLoading(false);
            if (getActivity() != null) getActivity().hideProgress();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (activityIsFinished()) {
            LogUtil.e("-------->activityIsFinished");
            return;
        }
        if (comicViewChildViewOnclickListener == null) {
            LogUtil.e("-------->ComicView没有设置监听");
            return;
        }
        int id = view.getId();
        if (id == R.id.lin_comic_read_up) {//上一话
            //  加载上一话
            if (mEnableUpFetch && !isLoading) {
                isScrollToModel = true;
                loadComic(false);
            }
        } else if (id == R.id.lin_comic_read_down) {//下一话
            if (mEnableLoadMore && !isLoading) {
                // 加载下一话
                isScrollToModel = true;
                loadComic(true);
            }
        } else {
            UserInfo loginUser = LoginHelper.getOnLineUser();
            if (loginUser == null) {
                ARouter.getInstance().build(RouterMap.COMIC_LOGIN_ACTIVITY).navigation(getActivity(), RequestCode.LOGIN_REQUEST_CODE);
            } else {
                if (id == R.id.iv_reward) {//打赏
                    comicViewChildViewOnclickListener.onRewardClick();
                } else if (id == R.id.lin_comic_read_collection) {//收藏
                    comicViewChildViewOnclickListener.onCollectiClick();
                } else if (id == R.id.lin_comic_read_favor) {//点赞
                    comicViewChildViewOnclickListener.onFavorClick();
                } else if (id == R.id.lin_comic_read_comment) {//评论
                    comicViewChildViewOnclickListener.onCommentClick();
                }
            }
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (activityIsFinished()) {
            LogUtil.e("-------->activityIsFinished");
            return;
        }
        if (view != null && view.getContext() instanceof Activity) {
            Activity activity = (Activity) view.getContext();
            if (activity.isFinishing() || activity.isDestroyed()) {
                return;
            }
        }
        showOrHideBar();
        if (view.getId() == R.id.comic_read_comic_img && view.getTag(R.id.tag_id) instanceof CustomViewTarget) {
            //如果图片加载失败 点击重新加载
            CustomViewTarget tag = (CustomViewTarget) view.getTag(R.id.tag_id);
            Request request = tag.getRequest();
            if (request != null && request.isFailed()) request.begin();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mGestureDetector != null && mGestureDetector.onTouchEvent(ev)) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //RecyclerView的触摸事件交由手势监听
        mGestureDetector.onTouchEvent(event);
        return false;
    }

    /**
     * 用户执行抛操作之后的回调，MOVE事件之后手松开（UP事件）那一瞬间的x或者y方向速度，如果达到一定数值（源码默认是每秒50px），
     * 就是抛操作（也就是快速滑动的时候松手会有这个回调，因此基本上有onFling必然有onScroll）
     *
     * @param e1        之前DOWN事件
     * @param e2        当前的MOVE事件
     * @param velocityX 当前MOVE事件和上一个MOVE事件的X方向速度
     * @param velocityY 当前MOVE事件和上一个MOVE事件的Y方向速度
     * @return
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//        int firstPosition = findFirstVisiblePosition();
//        if (e1 != null && e2 != null && !isLoading && mOnComicListener != null && mAdapter != null) {
//            int lastVisibleItemPosition = mLinearLayoutManager.findLastVisibleItemPosition();
//            if (e1.getY() - e2.getY() >= FLING_HEIGHT && mEnableLoadMore && !isLoading
//                    && lastVisibleItemPosition >= mAdapter.getData().size() - 1 + mAdapter.getHeaderLayoutCount()) {
//                // 加载下一话
//                isScrollToModel = false;
//                loadComic(true);
//            } else if (e2.getY() - e1.getY() >= FLING_HEIGHT && mEnableUpFetch && firstPosition <= 0) {
//                // 往上滑  加载上一话
//                isScrollToModel = false;
//                loadComic(false);
//            }
//        }
//        return false;
        return false;
    }

    //用户按下屏幕的时候的回调
    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    //用户按下按键后100ms 没有松开或者移动
    @Override
    public void onShowPress(MotionEvent e) {

    }

    //用户手指松开（UP事件）的时候如果没有执行onScroll()和onLongPress()这两个回调的话，就会回调这个，说明这是一个点击抬起事件，但是不能区分是否双击事件的抬起。
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    /**
     * 手指滑动的时候执行的回调（接收到MOVE事件，且位移大于一定距离）
     *
     * @param e1        之前DOWN事件
     * @param e2        当前的MOVE事件
     * @param distanceX 当前MOVE事件和上一个MOVE事件的X位移量
     * @param distanceY 当前MOVE事件和上一个MOVE事件的Y位移量
     * @return
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        int firstPosition = findFirstVisiblePosition();
        if (!isLoading && mAdapter != null) {
            int lastVisibleItemPosition = mLinearLayoutManager.findLastVisibleItemPosition();
            if (distanceY >= FLING_HEIGHT && mEnableLoadMore && !isLoading
                    && lastVisibleItemPosition >= mAdapter.getData().size() - 1 + mAdapter.getHeaderLayoutCount()) {
                // 加载下一话
                isScrollToModel = false;
                loadComic(true);
            } else if (distanceY < 0 && Math.abs(distanceY) >= FLING_HEIGHT && mEnableUpFetch && firstPosition <= 0) {
                // 往上滑  加载上一话
                isScrollToModel = false;
                loadComic(false);
            }
        }
        return false;
    }

    //用户长按后>100ms，触发之后不会触发其他回调，直至松开
    @Override
    public void onLongPress(MotionEvent e) {

    }

    public void showOrHideBar() {
        if (mTopBar.getState() == Control.State.SHOWED) {
            hide();
        } else if (mTopBar.getState() == Control.State.HIDE) {
            show();
        }
    }

    public boolean hide() {
        boolean show = false;
        if (mTopBar != null && mTopBar.getState() == Control.State.SHOWED) {
            mTopBar.hide();
            show = true;
        }
        if (mBottomBar != null && mBottomBar.getState() == Control.State.SHOWED) {
            mBottomBar.hide();
            show = true;
        }
        return show;
    }

    public void show() {
        if (mTopBar != null && mTopBar.getState() == Control.State.HIDE) {
            mTopBar.show();
        }
        if (mBottomBar != null && mBottomBar.getState() == Control.State.HIDE) {
            mBottomBar.show();
        }
    }

    public void addOnScrollListener(@NonNull RecyclerView.OnScrollListener onScrollListener) {
        if (onScrollListener != null) mRecyclerView.addOnScrollListener(onScrollListener);
    }

    public void setScrollToModel(boolean scrollToModel) {
        isScrollToModel = scrollToModel;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public BookCatalogModel getFirstCatalog() {
        return mAdapter.getCatalog(findFirstVisiblePosition());
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_read_comic;
    }

    @Override
    public void release() {
        if (mTopBar != null) mTopBar.release();
        if (mBottomBar != null) mBottomBar.release();
        if (mRemindView != null) mRemindView.release();
        removeComicViewChildViewOnclickListener();
        super.release();
    }

    private void changeReadMode(int readMode, int screenMode) {
        switch (screenMode) {
            case 0:
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                if (mAdapter != null && !CommonUtil.checkEmpty(mAdapter.getData())) {
                    final int position = mLinearLayoutManager.findFirstVisibleItemPosition() + mAdapter.getHeaderLayoutCount();
                    mRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                            mRecyclerView.post(new Runnable() {
                                @Override
                                public void run() {
                                    mRecyclerView.scrollToPosition(position);
                                }
                            });
                        }
                    });
                }
                break;
            case 1:
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                if (mAdapter != null && !CommonUtil.checkEmpty(mAdapter.getData())) {
                    final int position = mLinearLayoutManager.findFirstVisibleItemPosition() + mAdapter.getHeaderLayoutCount();
                    mRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                            mRecyclerView.post(new Runnable() {
                                @Override
                                public void run() {
                                    mRecyclerView.scrollToPosition(position);
                                }
                            });
                        }
                    });

                }
                break;
        }
        show();
    }

    /**
     * 可能存在用户点击返回键时立即点击item 点击事件响应 导致报空指针的异常
     * <p>
     * 判断view所在activity是否已经finish
     *
     * @return true 已经finish了
     */
    private boolean activityIsFinished() {
        if (getContext() != null && getContext() instanceof Activity) {
            Activity activity = (Activity) getContext();
            return activity.isFinishing() || activity.isDestroyed();
        }
        return true;
    }

}
