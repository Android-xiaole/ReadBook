package com.jj.comics.ui.detail;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gyf.immersionbar.ImmersionBar;
import com.jj.base.BaseApplication;
import com.jj.base.imageloader.ILFactory;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.detail.CommonRecommendAdapter;
import com.jj.comics.adapter.detail.ReadComicCatalogAdapter;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.constants.RequestCode;
import com.jj.comics.common.constants.UmEventID;
import com.jj.comics.data.db.DaoHelper;
import com.jj.comics.data.model.BookCatalogModel;
import com.jj.comics.data.model.BookListDataResponse;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.ui.dialog.DialogUtilForComic;
import com.jj.comics.ui.dialog.NormalNotifyDialog;
import com.jj.comics.ui.dialog.ShareDialog;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.eventbus.events.BatchBuyEvent;
import com.jj.comics.util.eventbus.events.RefreshComicCollectionStatusEvent;
import com.jj.comics.util.eventbus.events.RefreshDetailActivityDataEvent;
import com.jj.comics.util.eventbus.events.UpdateReadHistoryEvent;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.COMIC_DETAIL_ACTIVITY)
public class ComicDetailActivity extends BaseActivity<ComicDetailPresenter> implements ComicDetailContract.IDetailView {

    @BindView(R2.id.rv_recommendList)
    RecyclerView rv_recommendList;//底部推荐小说列表
    @BindView(R2.id.tv_read)
    TextView tv_read;//去阅读按钮
    @BindView(R2.id.iv_bookIcon)
    ImageView iv_bookIcon;//书籍封面
    @BindView(R2.id.tv_bookStatus)
    TextView tv_bookStatus;//当前小说连载状态
    @BindView(R2.id.tv_readNum)
    TextView tv_readNum;//已阅读人数
    @BindView(R2.id.tv_shareNum)
    TextView tv_shareNum;//已分享次数
    @BindView(R2.id.tv_wordsNum)
    TextView tv_wordsNum;//总字数
    @BindView(R2.id.tv_author)
    TextView tv_author;//小说作者
    @BindView(R2.id.tv_type)
    TextView tv_type;//小说类型
    @BindView(R2.id.tv_info)
    TextView tv_info;//小说简介
    @BindView(R2.id.iv_moreInfo)
    ImageView iv_moreInfo;//查看更多详情的按钮
    @BindView(R2.id.tv_title)
    TextView tv_title;//小说名称
    @BindView(R2.id.iv_addBookBottom)
    ImageView iv_addBookBottom;//底部加入书架icon
    @BindView(R2.id.tv_addBookBottom)
    TextView tv_addBookBottom;//底部加入书架文字显示
    @BindView(R2.id.tv_catalogNum)
    TextView tv_catalogNum;//总章节数
    @BindView(R2.id.tv_catalogTitle)
    TextView tv_catalogTitle;//当前阅读的章节标题
    @BindView(R2.id.lin_catalogMenu)
    LinearLayout lin_catalogMenu;//打开章节目录的按钮
    @BindView(R2.id.rv_catalogList)
    RecyclerView rv_catalogList;//章节列表
    @BindView(R2.id.tv_sort)
    TextView tv_sort;//倒序按钮
    @BindView(R2.id.drawerLayout)
    DrawerLayout mCatalogMenu;//侧滑菜单根布局
    @BindView(R2.id.pb_loading)
    ProgressBar pb_loading;//加载章节的loading控件
    @BindView(R2.id.tv_share_money)
    TextView mTvShareMonry;

    private ReadComicCatalogAdapter catalogAdapter;//章节列表适配器
    private CommonRecommendAdapter recommendAdapter;//底部推荐小说适配器

    public BookModel model;//漫画详情model
    private boolean isCollect;//是否加入书籍（收藏）

    private DaoHelper daoHelper;

    private ShareDialog shareDialog;//分享弹窗
    private NormalNotifyDialog removeCollectDialog;//移除收藏提示弹窗

    private String mFrom;
    private boolean flag_info;//标记是否需要重新计算查看更多icon显示的标记

    private int pageNum = 1;//章节目录分页页码
    private int initPageNum = 1;//初始页码
    private String sort = Constants.RequestBodyKey.SORT_ASC;//默认正序

    /**
     * 进入详情页
     *
     * @param activity
     * @param id       漫画id
     * @param from     来源
     */
    public static void toDetail(Activity activity, long id, String from) {
        ARouter.getInstance().build(RouterMap.COMIC_DETAIL_ACTIVITY)
                .withLong("id", id)
                .withString("from", from)
                .navigation(activity);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        daoHelper = new DaoHelper();

        mFrom = getIntent().getStringExtra("from");

        rv_recommendList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recommendAdapter = new CommonRecommendAdapter(R.layout.comic_item_search_watchingcomicdata);
        recommendAdapter.bindToRecyclerView(rv_recommendList, true);
        recommendAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (model.getId() != recommendAdapter.getData().get(position).getId()) {
                    ComicDetailActivity.toDetail(ComicDetailActivity.this,
                            recommendAdapter.getData().get(position).getId(), "详情页_推荐");
                    finish();
                }
            }
        });

        catalogAdapter = new ReadComicCatalogAdapter(R.layout.comic_readcomic_cataloglist_item);
        rv_catalogList.setLayoutManager(new LinearLayoutManager(this));
        catalogAdapter.bindToRecyclerView(rv_catalogList);
        //侧滑目录控件的item点击事件
        catalogAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (model != null) {
                    long chapterId = catalogAdapter.getData().get(position).getId();
                    catalogAdapter.notifyItem(chapterId);
                    getP().toRead(model, chapterId);
                    mCatalogMenu.closeDrawers();
                }
            }
        });
        catalogAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (model == null)return;
                pageNum++;
                getP().getCatalogList(model,pageNum,sort);
            }
        },rv_catalogList);

        lin_catalogMenu.setClickable(false);

        //监听简介textview的绘制，控制是否显示查看更多的icon
        tv_info.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (tv_info.getText().length()>0&&!flag_info){
                    flag_info = true;
                    Layout layout = tv_info.getLayout();
                    int lineCount = tv_info.getLineCount();
                    if (lineCount>= 3){
                        int ellipsisCount = layout.getEllipsisCount(lineCount - 1);
                        if (ellipsisCount>0){
                            iv_moreInfo.setVisibility(View.VISIBLE);
                        }else{
                            iv_moreInfo.setVisibility(View.GONE);
                        }
                    }else{
                        iv_moreInfo.setVisibility(View.GONE);
                    }
                }
            }
        });

        long id = getId();
        if (id > 0) {
            getP().getComicDetail(id);
            getP().getCollectStatus(id);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            if (model != null) {
                onLoadComicDetail(model);
            } else {
                long id = getId();
                if (id > 0) getP().getComicDetail(id);
            }
        }
    }

    @OnClick({R2.id.lin_catalogMenu, R2.id.iv_back_chapter, R2.id.tv_sort, R2.id.iv_back,  R2.id.iv_share, R2.id.lin_share,
            R2.id.lin_addBook, R2.id.tv_read, R2.id.iv_moreInfo,R2.id.tv_share_money})
    public void onClick_detail(View view) {
        int id = view.getId();
        if (id == R.id.lin_catalogMenu) {
            if (model == null){
                return;
            }
            if (IS_SUCCESS_GETCATALOG){
                if (!mCatalogMenu.isDrawerOpen(GravityCompat.START)) {
                    mCatalogMenu.openDrawer(GravityCompat.START);
                }
            }else{
                tv_catalogNum.setVisibility(View.GONE);
                pb_loading.setVisibility(View.VISIBLE);
                getP().getCatalogList(model,initPageNum,sort);
            }
        } else if (id == R.id.tv_sort) {
            if (model == null||catalogAdapter.getData().isEmpty()) return;
            tv_sort.setSelected(!tv_sort.isSelected());
            if (tv_sort.isSelected()) {
                sort = Constants.RequestBodyKey.SORT_DESC;
                tv_sort.setText("正序");
                Drawable drawable = getResources().getDrawable(R.drawable.icon_read_catalog_asc);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tv_sort.setCompoundDrawables(null, null, drawable, null);
            } else {
                sort = Constants.RequestBodyKey.SORT_ASC;
                tv_sort.setText("倒序");
                Drawable drawable = getResources().getDrawable(R.drawable.icon_read_catalog_desc);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tv_sort.setCompoundDrawables(null, null, drawable, null);
            }
//            List<BookCatalogModel> data = catalogAdapter.getData();
//            Collections.reverse(data);
//            catalogAdapter.notifyDataSetChanged();
            catalogAdapter.getData().clear();
            pageNum = 1;
            getP().getCatalogList(model,pageNum,sort);
        } else if (id == R.id.iv_back_chapter) {//目录的返回键
            mCatalogMenu.closeDrawers();
        } else if (id == R.id.iv_back) {
            finish();
        } else if (id == R.id.lin_addBook) {//加入书架
            if (model == null) return;
            if (LoginHelper.interruptLogin(this, null)) {
                if (isCollect) {
                    if (removeCollectDialog == null)
                        removeCollectDialog = new NormalNotifyDialog();
                    removeCollectDialog.show(getSupportFragmentManager(), getString(R.string.base_delete_title), String.format(getString(R.string.comic_confirm_delete), model.getTitle()), new DialogUtilForComic.OnDialogClick() {
                        @Override
                        public void onConfirm() {
                            getP().addOrRemoveCollect(model, isCollect);
                        }

                        @Override
                        public void onRefused() {

                        }
                    });
                } else {
                    getP().addOrRemoveCollect(model, isCollect);
                }
            }
        } else if (id == R.id.iv_share || id == R.id.lin_share || id == R.id.tv_share_money) {//分享
            if (model != null) {
                if (shareDialog == null) {
                    shareDialog = new ShareDialog(ComicDetailActivity.this, "详情", model.getTitle());
                }
                shareDialog.show(model);
            } else {
                ToastUtil.showToastShort("书籍异常");
            }
        } else if (id == R.id.tv_read) {//去阅读
            if (model == null) return;
            getP().toRead(model, model.getChapterid());
        } else if (id == R.id.iv_moreInfo) {//查看更多
            if (model == null) return;
            iv_moreInfo.setSelected(!iv_moreInfo.isSelected());
            if (iv_moreInfo.isSelected()){
                tv_info.setMaxLines(Integer.MAX_VALUE);
            }else{
                tv_info.setMaxLines(3);
            }
//            ARouter.getInstance().build(RouterMap.COMIC_DETAIL_BOOKINFO_ACTIVITY)
//                    .withString(Constants.IntentKey.BOOK_INFO, model.getIntro())
//                    .navigation(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (model == null) {
                /*
                这里必须得判空，因为如果当app长时间置于后台，该页面会被回收，当重新启动的时候
                由于model是接口获取的，会有一定延时，但此时onActivityResult会执行，拿到的model却是null,
                因此保险起见必须做非空判断
                 */
                return;
            }
            switch (requestCode) {
                case RequestCode.LOGIN_REQUEST_CODE:
                    showProgress();
                    getP().getComicDetail(model.getId());
                    getP().getCollectStatus(model.getId());
                    break;
                case RequestCode.SUBSCRIBE_REQUEST_CODE:
                    long chapterId = data.getLongExtra(Constants.IntentKey.ID, 0);
                    if (chapterId != 0) {
                        getP().toRead(model, chapterId);
                    }
                    break;
            }
        }
    }


    /**
     * 填充数据
     *
     * @param model 漫画model
     */
    @Override
    public void onLoadComicDetail(BookModel model) {
        Map<String, Object> page_detail = new HashMap<String, Object>();
        page_detail.put("from", "" + mFrom);
        page_detail.put("to", "" + model.getTitle());
        page_detail.put("to_id", "" + model.getId());
        MobclickAgent.onEventObject(BaseApplication.getApplication(), UmEventID.PAGE_DETAIL, page_detail);

        ILFactory.getLoader().loadNet(iv_bookIcon, model.getCover(), new RequestOptions().error(R.drawable.img_loading)
                .placeholder(R.drawable.img_loading));

        tv_title.setText(model.getTitle());
        tv_author.setText(model.getAuthor());
        tv_readNum.setText(model.getAllvisit() + "人");
        tv_shareNum.setText(model.getTotal_share() + "次");
        tv_wordsNum.setText(model.getSize() + "字");
        tv_info.setText(model.getIntro());
        tv_catalogTitle.setText("更新至：" + model.getLastvolume_name());
        mTvShareMonry.setText("分享预计赚￥" + model.getShare_will_earnings());

        if (model.getTag() != null && model.getTag().size() > 0) {
            tv_type.setText(model.getTag().get(0));
        } else {
            tv_type.setText("未知");
        }
        if (model.getFullflag() == 1) {//已完结
            tv_bookStatus.setText("已完结");
        } else {
            tv_bookStatus.setText("连载中");
        }

        //先查询本地阅读记录是否存在，存在优先显示本地记录
        BookModel localBookModel = daoHelper.queryBookModelByBookidAndUid(model.getId(), 0);
        if (localBookModel != null) {
            model.setChapterid(localBookModel.getChapterid());
            model.setOrder(localBookModel.getOrder());
        }
        //本地记录处理完之后再给全局变量赋值，顺便计算当前阅读的章节是第几页
        this.model = model;
        if (model.getChapterid() != 0) {
            tv_read.setText(String.format(getString(R.string.comic_continue_read), model.getOrder()));
            //有阅读记录的情况
            if (model.getOrder()% 20 == 0){
                pageNum = model.getOrder()/ 20;
            }else{
                pageNum = model.getOrder()/20+1;
            }
        } else {
            tv_read.setText("免费试读");
            //没有阅读记录的情况
            pageNum = 1;
        }
        initPageNum = pageNum;
        //计算完当前阅读的章节是第几页之后请求章节列表，默认正序请求
        getP().getCatalogList(model,pageNum,sort);

        //加载完详情后再去加载推荐小说列表
        if (model.getCategory_id() != null && model.getCategory_id().size() > 0) {
            getP().loadCommendList(model.getId(), 1, model.getCategory_id().get(0));
        } else {
            recommendAdapter.setNewData(null);
        }
    }

    @Override
    public void onCollectionSuccess(boolean collectByCurrUser) {
        EventBusManager.sendComicCollectionStatus(new RefreshComicCollectionStatusEvent(collectByCurrUser));
        if (collectByCurrUser) {
            ToastUtil.showToastShort("已成功加入书架");
        }
        fillCollectStatus(collectByCurrUser);
    }

    /**
     * 获取小说收藏状态的回调
     *
     * @param collectByCurrUser
     */
    @Override
    public void fillCollectStatus(boolean collectByCurrUser) {
        isCollect = collectByCurrUser;
        if (collectByCurrUser) {//已加入书架
            iv_addBookBottom.setImageResource(R.drawable.icon_detail_add_book_pre);
            tv_addBookBottom.setText("已在书架");
        } else {//为加入书架
            iv_addBookBottom.setImageResource(R.drawable.icon_detail_add_book);
            tv_addBookBottom.setText("加入书架");
        }
    }

    @Override
    public void onLoadRecommendList(BookListDataResponse response) {
        recommendAdapter.setNewData(response.getData().getData());
    }

    @Override
    public void onGetCatalogList(List<BookCatalogModel> catalogModels, int totalNum) {
        IS_SUCCESS_GETCATALOG = true;
        tv_catalogNum.setText("共" + totalNum + "章");

        if (!catalogModels.isEmpty()){
            catalogAdapter.addData(catalogModels);
            catalogAdapter.loadMoreComplete();
        }else{
            catalogAdapter.loadMoreEnd();
        }

        if (model != null) {
            catalogAdapter.notifyItem(model.getChapterid());
        }
    }

    private boolean IS_SUCCESS_GETCATALOG = false;//标记获取章节目录是否成功过
    @Override
    public void onGetCatalogListFail() {
        if (pageNum != initPageNum){
            pageNum --;
        }
        tv_catalogNum.setText("加载失败，点击重试");
        catalogAdapter.loadMoreFail();
    }

    @Override
    public void onGetCatalogListEnd() {
        lin_catalogMenu.setClickable(true);
        tv_catalogNum.setVisibility(View.VISIBLE);
        pb_loading.setVisibility(View.GONE);
    }

    public void toRead(BookModel bookModel, long chapterId) {
        getP().toRead(bookModel, chapterId);
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_activity_comic_detail;
    }

    @Override
    public ComicDetailPresenter setPresenter() {
        return new ComicDetailPresenter();
    }

    @Override
    public int getOptionsMenuId() {
        return R.menu.comic_detail_menu;
    }

    /**
     * 刷新别的页面修改小说收藏状态的通知
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshCollectStatus(RefreshComicCollectionStatusEvent event) {
        fillCollectStatus(event.collectByCurrUser);
    }

    /**
     * 来自阅读页面产生了历史记录刷新当前立即阅读按钮的文字显示信息
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateReadHistory(UpdateReadHistoryEvent event) {
        if (tv_read != null) {
            model.setChapterid(event.getChapterid());
            model.setOrder(event.getChapterorder());
            tv_read.setText(String.format(getString(R.string.comic_continue_read), event.getChapterorder()));
            catalogAdapter.notifyItem(model.getChapterid());
        }
    }

    /**
     * 来自全本购买成功的通知，刷新全本购买的icon的显示状态
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshBatchIcon(BatchBuyEvent event) {
        this.model = event.getBookModel();
    }

    /**
     * 来自loading界面登录成功的通知，需要去刷新改页面数据
     *
     * @return
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshDetailActivityDataEvent(RefreshDetailActivityDataEvent event) {
        if (model == null) return;
        showProgress();
        getP().getComicDetail(model.getId());
        getP().getCollectStatus(model.getId());
    }


    @Override
    public boolean useEventBus() {
        return true;
    }

    long getId() {
        long id = 0;
        if (getIntent().getLongExtra(Constants.IntentKey.ID, 0) != 0) {
            id = getIntent().getLongExtra(Constants.IntentKey.ID, 0);
        } else {
            String longExtra = getIntent().getStringExtra(Constants.IntentKey.ID);
            try {
                if (Long.parseLong(longExtra) > 0) {
                    id = Long.parseLong(longExtra);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return id;
    }

    @Override
    protected void initImmersionBar() {
        ImmersionBar.with(context)
                .reset()
                .keyboardEnable(true)
                .fitsSystemWindows(true)
                .statusBarDarkFont(true, 0.2f)
                .statusBarColor(com.jj.base.R.color.base_color_ffffff)
                .init();
    }

    @Override
    public void onBackPressed() {
        if (mCatalogMenu.isDrawerOpen(GravityCompat.START)) {
            mCatalogMenu.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }

}
