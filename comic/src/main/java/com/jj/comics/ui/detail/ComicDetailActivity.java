package com.jj.comics.ui.detail;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.gyf.barlibrary.ImmersionBar;
import com.jj.base.imageloader.ILFactory;
import com.jj.base.ui.BaseActivity;
import com.jj.base.ui.BaseFragment;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.SharedPref;
import com.jj.base.utils.Utils;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.ViewPagerAdapter;
import com.jj.comics.common.callback.AppBarStateChangeListener;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.constants.RequestCode;
import com.jj.comics.data.db.DaoHelper;
import com.jj.comics.data.model.BookCatalogModel;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.CommonStatusResponse;
import com.jj.comics.data.model.ShareMessageModel;
import com.jj.comics.ui.detail.subdetail.CatalogFragment;
import com.jj.comics.ui.detail.subdetail.SubComicDetailFragment;
import com.jj.comics.ui.dialog.DialogUtilForComic;
import com.jj.comics.ui.dialog.NormalNotifyDialog;
import com.jj.comics.ui.dialog.RewardDialog;
import com.jj.comics.ui.dialog.ShareDialog;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.SignUtil;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.eventbus.events.RefreshComicCollectionStatusEvent;
import com.jj.comics.util.eventbus.events.RefreshComicFavorStatusEvent;
import com.jj.comics.util.eventbus.events.UpdateReadHistoryEvent;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.ScreenUtils;

@Route(path = RouterMap.COMIC_DETAIL_ACTIVITY)
public class ComicDetailActivity extends BaseActivity<ComicDetailPresenter> implements ComicDetailContract.IDetailView, Toolbar.OnMenuItemClickListener {

    @BindView(R2.id.rl_toolbar)
    RelativeLayout rl_toolbar;
    @BindView(R2.id.comic_detail_toolbar)
    Toolbar mToolBar;
    @BindView(R2.id.comic_detail_img)
    ImageView mImg;
    @BindView(R2.id.comic_detail_collapsing)
    CollapsingToolbarLayout mToolbarLayout;
    @BindView(R2.id.comic_detail_tag)
    LinearLayout mTagContainer;
    @BindView(R2.id.comic_detail_count)
    TextView mComicCount;
    @BindView(R2.id.comic_detail_title)
    TextView mComicTitle;
    @BindView(R2.id.comic_detail_read)
    TextView mComicRead;
    @BindView(R2.id.comic_detail_star_tv)
    TextView mComicStarTv;
    @BindView(R2.id.comic_detail_star)
    ImageView mComicStar;
    @BindView(R2.id.comic_detail_appbarLayout)
    AppBarLayout mBarLayout;
    @BindView(R2.id.comic_detail_tab)
    TabLayout mTabLayout;
    @BindView(R2.id.comic_detail_pager)
    ViewPager mViewPager;
    @BindView(R2.id.iv_comic_detail_favor)
    ImageView iv_comic_detail_favor;

    private List<BaseFragment> mPaths;
    private ViewPagerAdapter viewPagerAdapter;
    private AppBarStateChangeListener.State mState = AppBarStateChangeListener.State.IDLE;
    public BookModel model;//漫画详情model

    private NormalNotifyDialog normalNotifyDialog;
    private BookCatalogModel preparRead;//准备阅读的漫画章节，相当于缓存的漫画章节，如果没有缓存那就默认第一章
    private RewardDialog rewardDialog;//打赏弹窗
    private boolean isCollect, isFavor;//用户是否收藏或者点赞当前漫画
    private DaoHelper daoHelper;

    @Override
    public void initData(Bundle savedInstanceState) {
        //获取状态栏高度
        int statusBarHeight = ScreenUtils.getStatusBarHeight();
        //设置单独标题布局离屏幕上方的高度
        FrameLayout.LayoutParams lp_rltoolBar = (FrameLayout.LayoutParams) rl_toolbar.getLayoutParams();
        lp_rltoolBar.topMargin = statusBarHeight;
        rl_toolbar.setLayoutParams(lp_rltoolBar);
        //设置toolbar离屏幕上方的高度
        FrameLayout.LayoutParams lp_toolBar = (FrameLayout.LayoutParams) mToolBar.getLayoutParams();
        lp_toolBar.topMargin = statusBarHeight;
        mToolBar.setLayoutParams(lp_toolBar);

        daoHelper = new DaoHelper();

        //暂时不会有入口会传model过来，现在都是传id
//        model = (BookModel) getIntent().getSerializableExtra("catalogModel");
        setSupportActionBar(mToolBar);
        mToolBar.setOnMenuItemClickListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                mState = state;
                setTitle(state);
            }
        });


        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        mPaths = new ArrayList<>();
        /*
        这里要先看系统缓存里面存不存在两个fragment实例，存在就不用重新去创建了，直接拿出来添加到容器里面
        因为有一种情况就是当前界面处于后台状态被系统回收的时候，数据会被缓存，重新进入的时候会重走生命周期
         */
        if (fragments == null||fragments.isEmpty()){
            SubComicDetailFragment detailFragment = new SubComicDetailFragment();
            mPaths.add(detailFragment);

            CatalogFragment catalogFragment = new CatalogFragment();
            mPaths.add(catalogFragment);
        }else{
            for (Fragment fragment : fragments) {
                if (fragment instanceof  SubComicDetailFragment){
                    mPaths.add((SubComicDetailFragment)fragment);
                }
                if (fragment instanceof  CatalogFragment){
                    mPaths.add((CatalogFragment)fragment);
                }
            }
        }

        long id = getId();
        if (id > 0) {
            showProgress();
            getP().getComicDetail(id, true);
            if (LoginHelper.getOnLineUser() != null) {
                //这两个接口用户登录状态下再去调用
                getP().getCollectStatus(id);
                getP().getFavorStatus(id);
            }
        }

    }

    /**
     * 收藏、点赞、打赏、立即阅读 点击事件
     */
    @OnClick({R2.id.comic_detail_read, R2.id.comic_detail_add_shelf, R2.id.lin_comic_detail_favor, R2.id.comic_detail_reward})
    public void onClick_Detail(View view) {
        int viewId = view.getId();
        if (viewId == R.id.comic_detail_read) {//立即阅读
            if (model != null) {
                toRead(model, model.getChapterid());
            }
        } else {
            if (LoginHelper.getOnLineUser() == null) {
                ARouter.getInstance().build(RouterMap.COMIC_LOGIN_ACTIVITY).navigation(ComicDetailActivity.this, RequestCode.LOGIN_REQUEST_CODE);
                return;
            } else if (model == null) {
                return;
            } else if (viewId == R.id.comic_detail_add_shelf) {//收藏
                MobclickAgent.onEvent(this, Constants.UMEventId.COLLECT_DETAIL, model.getId() + " : " + model.getTitle());
                if (LoginHelper.interruptLogin(this, null)) {
                    if (isCollect) {
                        if (normalNotifyDialog == null)
                            normalNotifyDialog = new NormalNotifyDialog();
                        normalNotifyDialog.show(getSupportFragmentManager(), getString(R.string.base_delete_title), String.format(getString(R.string.comic_confirm_delete), model.getTitle()), new DialogUtilForComic.OnDialogClick() {
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
            } else if (viewId == R.id.lin_comic_detail_favor) {//点赞
                if (!isFavor) {
                    getP().favorContent(model.getId());
                }
            } else if (viewId == R.id.comic_detail_reward) {//打赏
                if (LoginHelper.getOnLineUser() == null) {
                    ARouter.getInstance().build(RouterMap.COMIC_LOGIN_ACTIVITY).navigation(ComicDetailActivity.this);
                    return;
                }
                MobclickAgent.onEvent(ComicDetailActivity.this, Constants.UMEventId.REWARD_DETAIL, model.getId() + " : " + model.getTitle());
                if (rewardDialog == null)
                    rewardDialog = new RewardDialog(ComicDetailActivity.this, null, model.getId());
                rewardDialog.show();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            if (model != null) {
                loadData(model, true);
            } else {
                long id = getId();
                if (id > 0) getP().getComicDetail(id, true);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (rewardDialog != null) rewardDialog.release();
    }

    public void loadData(BookModel model, boolean umengUpload) {
        this.model = model;
        onLoadComicDetail(model, false);
        showProgress();
        getP().getComicDetail(model.getId(), umengUpload);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (BaseFragment mPath : mPaths) {
            mPath.onActivityResult(requestCode, resultCode, data);
        }
        if (resultCode == RESULT_OK) {
            if(model == null){
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
                    getP().getComicDetail(model.getId(), false);
                    break;
                case RequestCode.SUBSCRIBE_REQUEST_CODE:
                    long chapterId = data.getLongExtra(Constants.IntentKey.ID, 0);
                    if (chapterId != 0) {
                        toRead(model, chapterId);
                    }
                    break;
                case RequestCode.READ_REQUEST_CODE:
                    //滑动到目录
//                    int index = data.getIntExtra("index", -1);
//                    if (index >= 0 && index < mViewPager.getAdapter().getCount())
//                        mViewPager.setCurrentItem(index, true);
                    break;
            }
        }
    }

    /**
     * 设置标题
     *
     * @param state
     */
    private void setTitle(AppBarStateChangeListener.State state) {
        if (mToolbarLayout == null) return;
        mToolbarLayout.setTitle(model == null ? "" : model.getTitle());
        switch (state) {
            case EXPANDED:
                mToolbarLayout.setTitleEnabled(false);
                mComicTitle.setText(model == null ? "" : model.getTitle());
                break;
            case COLLAPSED:
            default:
                mToolbarLayout.setTitle(model == null ? "" : model.getTitle());
                mToolbarLayout.setTitleEnabled(true);
                mComicTitle.setText("");
                break;
        }
    }

    /**
     * 填充数据
     *
     * @param model       漫画model
     * @param umengUpload 是否需要友盟上传
     */
    @Override
    public void onLoadComicDetail(final BookModel model, boolean umengUpload) {
        //先查询本地阅读记录是否存在，存在优先显示本地记录
        BookModel localBookModel = daoHelper.queryBookModelByBookidAndUid(model.getId(), 0);
        if (localBookModel != null) {
            model.setChapterid(localBookModel.getChapterid());
            model.setOrder(localBookModel.getOrder());
        }
        //本地记录处理完之后再给全局变量赋值
        this.model = model;
        if (model.getChapterid() != 0) {
            mComicRead.setText(String.format(getString(R.string.comic_continue_read), model.getOrder()));
        } else {
            mComicRead.setText("立即阅读");
        }
        if (umengUpload) {
            getP().umengOnEvent(this, model);
        }
        ILFactory.getLoader().loadNet(mImg, model.getCoverl(), new RequestOptions().error(R.drawable.img_loading)
                .placeholder(R.drawable.img_loading));
        setTitle(mState);
        if (model.getFullflag() == 1) {//已完结
            mComicCount.setText("已完结");
        } else {
            mComicCount.setText(String.format(getString(R.string.comic_update), model.getLastvolume()));
        }
        List<String> tags = model.getTag();
        removeNotNeed(tags.size());
        for (int i = 0; i < tags.size(); i++) {
            if (i == 2) {//控制最多显示两个标签
                break;
            }
            fillTagView(i, tags.get(i));
        }

        if (viewPagerAdapter == null || mTabLayout.getTabCount() == 0) {
            for (BaseFragment mPath : mPaths) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.IntentKey.BUNDLE, model);
//                bundle.putInt(Constants.IntentKey.SUB_COUNT, catalogModel.getLastvolume());
                mPath.setArguments(bundle);
            }
            viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), mPaths, new String[]{getString(R.string.comic_detail_text), getString(R.string.comic_catalog_text)});
            mViewPager.setAdapter(viewPagerAdapter);
            mTabLayout.setupWithViewPager(mViewPager);
        } else {
            //更新fragment
//            viewPagerAdapter.getItem(1)
            int count = viewPagerAdapter.getCount();
            for (int i = 0; i < count; i++) {
                Fragment fragment = viewPagerAdapter.getItem(i);
                if (fragment instanceof SubComicDetailFragment) {
                    ((SubComicDetailFragment) fragment).refresh(model);
                } else if (fragment instanceof CatalogFragment) {
                    ((CatalogFragment) fragment).refresh(model);
                }
            }
        }
        hideProgress();
    }

    public void toRead(BookModel bookModel, long chapterId) {
        getP().toRead(bookModel, chapterId);
    }

    @Override
    public void fillTagView(int i, String tag) {
        View view = mTagContainer.getChildAt(i);
        if (view != null && view instanceof TextView) {
            ((TextView) view).setText(tag);
        } else {
            TextView textView = new TextView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.rightMargin = Utils.dip2px(this, 12);
            textView.setLayoutParams(layoutParams);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(getResources().getColor(R.color.comic_363636));
            textView.setBackgroundResource(R.drawable.comic_tag_bg);
            textView.setText(tag);
            mTagContainer.addView(textView);
        }
    }

    private void removeNotNeed(int tagCount) {
        int size = mTagContainer.getChildCount();
        for (int i = 0; i < size; i++) {
            if (!(mTagContainer.getChildAt(i) instanceof TextView)) {
                mTagContainer.removeViewAt(i);
                i--;
                size--;
            }
        }
        int min = Math.min(tagCount, size);
        for (int i = min; i < size; i++) {
            mTagContainer.removeViewAt(i);
            i--;
            size--;
        }

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

    private ShareDialog shareDialog;
    private ShareMessageModel shareMessageModel;

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if (TextUtils.equals(getString(R.string.comic_share_text), menuItem.getTitle())) {
            if (model != null) {
                if (shareDialog == null) {
                    shareDialog = new ShareDialog(this);
                }

                if (shareMessageModel == null) shareMessageModel = new ShareMessageModel();
                shareMessageModel.setShareTitle(String.format(getString(R.string.comic_share_title), model.getTitle()));
                String shareContent = model.getIntro().trim().length() == 0 ? getString(R.string.comic_null_abstract) : String.format(getString(R.string.comic_comic_desc) + Html.fromHtml(model.getIntro()));
                shareMessageModel.setShareContent(shareContent);
                shareMessageModel.setShareImgUrl(model.getCover());
                String channel_name = Constants.CHANNEL_ID;
                String signCode = "";
                if (channel_name.contains("-")) {
                    String[] code = channel_name.split("-");
                    signCode = code[code.length - 1];
                } else {
                    signCode = channel_name;
                }
                String sign = SignUtil.sign(Constants.PRODUCT_CODE + signCode);
//                String sign = SignUtil.sign(Constants.PRODUCT_CODE + matcher.replaceAll(" ").trim());
                shareMessageModel.setShareUrl(String.format(getString(R.string.comic_share_url), SharedPref.getInstance().getString(Constants.SharedPrefKey.SHARE_HOST_KEY, Constants.SharedPrefKey.SHARE_HOST), model.getId(), channel_name, sign) + "&pid=" + Constants.PRODUCT_CODE);
                shareMessageModel.setUmengPrarms(model.getId() + " : " + model.getTitle());
                shareDialog.show(shareMessageModel);
            }
        }
        return true;
    }

    /**
     * 处理漫画内容收藏操作的通知
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void dealCollection(RefreshComicCollectionStatusEvent refreshComicCollectionStatusEvent) {
        boolean collectByCurrUser = refreshComicCollectionStatusEvent.collectByCurrUser;
        isCollect = collectByCurrUser;
        mComicStar.setImageResource(collectByCurrUser ? R.drawable.icon_comic_read_bottombar_collection : R.drawable.icon_comic_read_uncollection_black);
    }

    /**
     * 处理漫画内容点赞操作的通知
     *
     * @param refreshComicFavorStatusEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateBookFavorStatus(RefreshComicFavorStatusEvent refreshComicFavorStatusEvent) {
        ILFactory.getLoader().loadResource(iv_comic_detail_favor, R.drawable.img_comic_read_reward_dianzan_true, null);
    }

    /**
     * 来自阅读页面产生了历史记录刷新当前立即阅读按钮的文字显示信息
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateReadHistory(UpdateReadHistoryEvent event) {
        if (mComicRead != null) {
            model.setChapterid(event.getChapterid());
            model.setOrder(event.getChapterorder());
            mComicRead.setText(String.format(getString(R.string.comic_continue_read), event.getChapterorder()));
        }
    }

    @Override
    public void dealCollection(boolean collectByCurrUser) {
        isCollect = collectByCurrUser;
        mComicStar.setImageResource(collectByCurrUser ? R.drawable.icon_comic_read_bottombar_collection : R.drawable.icon_comic_read_uncollection_black);
        EventBusManager.sendComicCollectionStatus(new RefreshComicCollectionStatusEvent(collectByCurrUser));
    }

    @Override
    public void onFavorContentSuccess() {
        isFavor = true;
        //点赞成功重新获取漫画详情数据
        getP().getComicDetail(model.getId(), false);
//        EventBusManager.sendComicFavorStatus(new RefreshComicFavorStatusEvent(null));
        ILFactory.getLoader().loadResource(iv_comic_detail_favor, R.drawable.img_comic_read_reward_dianzan_true, null);
    }

    @Override
    public void fillCollectStatus(CommonStatusResponse response) {
        isCollect = response.getData().getStatus();
        mComicStar.setImageResource(response.getData().getStatus() ? R.drawable.icon_comic_read_bottombar_collection : R.drawable.icon_comic_read_uncollection_black);
    }

    @Override
    public void fillFavorStatus(CommonStatusResponse response) {
        isFavor = response.getData().getStatus();
        ILFactory.getLoader().loadResource(iv_comic_detail_favor,
                response.getData().getStatus() ? R.drawable.img_comic_read_reward_dianzan_true : R.drawable.img_comic_read_reward_dianzan_false, null);
    }

    @Override
    public boolean hasFragment() {
        return true;
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
        ImmersionBar.with(this).init();
    }


}
