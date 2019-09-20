package com.jj.comics.ui.recommend;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jj.base.dialog.CustomFragmentDialog;
import com.jj.base.imageloader.ILFactory;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseActivity;
import com.jj.base.ui.BaseCommonFragment;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.recommend.RecentlyAdapter;
import com.jj.comics.adapter.recommend.RecommendAdapter;
import com.jj.comics.adapter.recommend.RecommendChildAdapter;
import com.jj.comics.common.callback.OnScrollListenerWithButton;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.net.download.DownInfo;
import com.jj.comics.data.model.BannerResponse;
import com.jj.comics.data.model.BookListRecommondResponse;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.PayActionResponse;
import com.jj.comics.data.model.Push;
import com.jj.comics.data.model.SectionModel;
import com.jj.comics.ui.detail.DetailActivityHelper;
import com.jj.comics.ui.dialog.DialogUtilForComic;
import com.jj.comics.ui.dialog.NormalNotifyDialog;
import com.jj.comics.util.IntentUtils;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.ScreenUtils;

@Route(path = RouterMap.COMIC_RECOMMEND_FRAGMENT)
public class RecommendFragment extends BaseCommonFragment<RecommendPresenter> implements RecommendContract.IRecommendView, SwipeRefreshLayout.OnRefreshListener,
        RecommendAdapter.OnClickListener {
    @BindView(R2.id.recommend_refresh)
    SwipeRefreshLayout mRefresh;
    @BindView(R2.id.recommend_recycler)
    RecyclerView rv_recently;//加载底部的最近更新,这是主recyclerview
    @BindView(R2.id.recommend_float_btn)
    ImageView mToTop;
    @BindView(R2.id.recommend_bar)
    RelativeLayout mAppBarLayout;

    @BindView(R2.id.recommend_featured)
    TextView mTvFeatured;
    @BindView(R2.id.recommend_man)
    TextView mTvMan;
    @BindView(R2.id.recommend_woman)
    TextView mTvWoman;

    private RecentlyAdapter adapter_recently;//最近更新adapter
    private RecyclerView rv_content;//头部内容加载recyclerview
    private RecyclerView rv_popShare;//头部内容加载recyclerview
    private RecommendAdapter adapter_content;//头部专区内容adapter
    private RecommendChildAdapter adapterPopShare;//头部专区内容adapter
    private int recentlyPage = 1;//记录最近更新分页请求页数
    private int recentChannelFlag = Constants.RequestBodyKey.CONTENT_CHANNEL_FLAG_ALL;//记录最近选择的首页频道
    private Banner mBanner;
    private int moveY = 0;//记录Y轴滑动距离

    @Override
    public void initData(Bundle savedInstanceState) {

        int statusBarHeight = ScreenUtils.getStatusBarHeight();
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mAppBarLayout.getLayoutParams();
        lp.topMargin = statusBarHeight;
        mAppBarLayout.setLayoutParams(lp);

        Fragment parentFragment = getParentFragment();
        ((HomeFragment) parentFragment).setToolBarBgAlpha(1);

        mRefresh.setColorSchemeColors(getResources().getColor(R.color.comic_yellow_ffd850));
        mRefresh.setOnRefreshListener(this);

        rv_recently.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_recently.setHasFixedSize(true);
        adapter_recently = new RecentlyAdapter(R.layout.comic_item_recommend_recentlyupdate, 2);
        adapter_recently.bindToRecyclerView(rv_recently,true);
        adapter_recently.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                recentlyPage++;
                getP().loadRecentlyComic(recentlyPage,recentChannelFlag,false);
            }
        }, rv_recently);
        adapter_recently.setEmptyClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getP().loadRecentlyComic(recentlyPage,recentChannelFlag,false);
            }
        });
        rv_recently.addOnScrollListener(new OnScrollListenerWithButton(mToTop));
        rv_recently.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                LogUtil.e("Y轴移动距离："+dy);
//                moveY = moveY + dy;
//                float imageHei = Utils.dip2px(getActivity(), 290);
//                Fragment parentFragment = getParentFragment();
//                if (parentFragment instanceof HomeFragment) {
//                    if (moveY >= imageHei) {
//                        ((HomeFragment) parentFragment).setToolBarBgAlpha(1);
//                    } else {
//                        ((HomeFragment) parentFragment).setToolBarBgAlpha(moveY / imageHei);
//                    }
//                }
            }
        });
        adapter_recently.addHeaderView(getBannerHeadView());
//        adapter_recently.addHeaderView(getHeaderViewBtns());
        adapter_recently.addHeaderView(getContentHeadView());
        adapter_recently.addHeaderView(getPopShareHeadView());
        adapter_recently.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String from = "最近更新";
                BookModel model = adapter_recently.getData().get(position);
                //点击事件友盟统计
                getP().umengOnEvent(from, model);
                DetailActivityHelper.toDetail(getActivity(), model.getId(), from);
            }
        });

        mRefresh.setRefreshing(true);
        getP().getBanner();
        getP().loadData(recentChannelFlag,1, false,false);
        getP().loadPopShare(recentChannelFlag,false);
        getP().loadRecentlyComic(recentlyPage, recentChannelFlag, false);

        switchTvs(0);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
//        if (!hidden) {
//            //当前页面可见的时候需要重新设置状态栏属性
//            Fragment parentFragment = getParentFragment();
//            if (parentFragment instanceof HomeFragment) {
//                float imageHei = Utils.dip2px(getActivity(), 290);
//                if (moveY >= imageHei) {
//                    ((HomeFragment) parentFragment).setToolBarBgAlpha(1);
//                } else {
//                    ((HomeFragment) parentFragment).setToolBarBgAlpha(moveY / imageHei);
//                }
//            }
//        }
    }

    /**
     * 横幅广告轮播头布局
     *
     * @return
     */
    private View getBannerHeadView() {
        View headView = getLayoutInflater().inflate(R.layout.comic_recommend_banner, (ViewGroup) rv_recently.getParent(), false);
        mBanner = headView.findViewById(R.id.recommend_banner);
        //设置banner样式
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器
        mBanner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {

                String imageUrl = ((BannerResponse.DataBean) path).getBanner_img_url();
                if (!TextUtils.isEmpty(imageUrl)) {
                    ILFactory.getLoader().loadNet(imageView, imageUrl, new RequestOptions().error(R.drawable.img_loading)
                            .placeholder(R.drawable.img_loading));
                } else {
                    ILFactory.getLoader().loadResource(imageView,
                            R.drawable.img_loading,
                            new RequestOptions().transforms(new CenterCrop()/*, new RoundedCorners(12)*/));
                }
            }
        });
        //设置自动轮播，默认为true
        mBanner.isAutoPlay(true);

        //设置轮播时间
        mBanner.setDelayTime(3000);
        //设置指示器位置（当banner模式中有指示器时）
        mBanner.setIndicatorGravity(BannerConfig.CENTER);
        return headView;
    }


    /**
     * 获取专区内容头布局
     */
    private View getContentHeadView() {
        View headView = getLayoutInflater().inflate(R.layout.comic_header_recommond_content,
                (ViewGroup) rv_recently.getParent(), false);
        rv_content = headView.findViewById(R.id.rv_content);
        adapter_content = new RecommendAdapter(R.layout.comic_item_recommend);
        rv_content.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter_content.bindToRecyclerView(rv_content, false);
        adapter_content.setOnClick(this);
        adapter_content.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                //点击加载更多跳转到页面
                if (view.getId() == R.id.tv_loadMore) {
                    List<SectionModel> datas = adapter_content.getData();
                    SectionModel sectionModel = datas.get(position);
                    long sectionId = sectionModel.getSectionId();
                    ARouter.getInstance().build(RouterMap.COMIC_RECOMMEND_LOADMORE)
                            .withLong(Constants.IntentKey.SECTION_ID, sectionId)
                            .withString(Constants.IntentKey.TITLE, sectionModel.getName())
                            .navigation(getActivity());
                }

            }
        });
        return headView;
    }

    /**
     * 获取热门分享
     */
    private View getPopShareHeadView() {
        View headView = getLayoutInflater().inflate(R.layout.comic_header_pop_share,
                (ViewGroup) rv_recently.getParent(), false);
        rv_popShare = headView.findViewById(R.id.rv_pop_share);
        adapterPopShare = new RecommendChildAdapter(R.layout.comic_item_recommend_vertical,
                Integer.MAX_VALUE, false,true);
        rv_popShare.setLayoutManager(new GridLayoutManager(getContext(),2));
        adapterPopShare.bindToRecyclerView(rv_popShare, false);
        adapterPopShare.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //点击事件友盟统计
                List data = adapter.getData();
                if (data != null) {
                    BookModel model = (BookModel) data.get(position);
                    if (model != null) {
                        getP().umengOnEvent("popShare", model);
                        DetailActivityHelper.toDetail(getActivity(), model.getId(), "popShare");
                    }
                }

            }
        });
        return headView;
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_fragment_recommend;
    }

    @Override
    public RecommendPresenter setPresenter() {
        return new RecommendPresenter();
    }

    @Override
    public void fillData(boolean changeChannel,List<BookListRecommondResponse.DataBean> data) {
        ArrayList<SectionModel> sectionModels = new ArrayList<>();

        for (BookListRecommondResponse.DataBean dataBean : data) {
            //先判断数据集是否有数据，没有数据则不显示这个专区
            if (dataBean.getList() != null && dataBean.getList().size() > 0) {
                SectionModel sectionModel = new SectionModel();
                sectionModel.setName(dataBean.getModel_name());
                sectionModel.setStyle(dataBean.getStyle());
                sectionModel.setSectionId(dataBean.getId());
                sectionModel.setContentList(dataBean.getList());
                sectionModels.add(sectionModel);
            }
        }
        adapter_content.setNewData(sectionModels);
        if (mRefresh.isRefreshing())
            mRefresh.setRefreshing(false);
    }

    @OnClick({R2.id.recommend_float_btn,R2.id.recommend_search,R2.id.recommend_featured,
            R2.id.recommend_man,R2.id.recommend_woman})
    void onClick(View view) {
        if (view.getId() == R.id.recommend_float_btn) {
            rv_recently.smoothScrollToPosition(0);
        }else if (view.getId()  == R.id.recommend_search) {
            ARouter.getInstance().build(RouterMap.COMIC_SEARCH_ACTIVITY).navigation();
        }else if (view.getId()  == R.id.recommend_featured) {
            recentChannelFlag = Constants.RequestBodyKey.CONTENT_CHANNEL_FLAG_ALL;
            switchTvs(0);
            getP().loadRecentlyComic(1,Constants.RequestBodyKey.CONTENT_CHANNEL_FLAG_ALL,true);
            getP().loadPopShare(Constants.RequestBodyKey.CONTENT_CHANNEL_FLAG_ALL,true);
            getP().loadData(Constants.RequestBodyKey.CONTENT_CHANNEL_FLAG_ALL,1, false,true);
        }else if (view.getId()  == R.id.recommend_man) {
            recentChannelFlag = Constants.RequestBodyKey.CONTENT_CHANNEL_FLAG_MAN;
            switchTvs(1);
            getP().loadRecentlyComic(1,Constants.RequestBodyKey.CONTENT_CHANNEL_FLAG_MAN,true);
            getP().loadPopShare(Constants.RequestBodyKey.CONTENT_CHANNEL_FLAG_MAN,true);
            getP().loadData(Constants.RequestBodyKey.CONTENT_CHANNEL_FLAG_MAN,1, false,true);
        }else if (view.getId()  == R.id.recommend_woman) {
            recentChannelFlag = Constants.RequestBodyKey.CONTENT_CHANNEL_FLAG_WOMAN;
            switchTvs(2);
            getP().loadRecentlyComic(1,Constants.RequestBodyKey.CONTENT_CHANNEL_FLAG_WOMAN,true);
            getP().loadPopShare(Constants.RequestBodyKey.CONTENT_CHANNEL_FLAG_WOMAN,true);
            getP().loadData(Constants.RequestBodyKey.CONTENT_CHANNEL_FLAG_WOMAN,1, false,true);
        }
    }

    private void switchTvs(int index) {
        switch (index) {
            case 0:
                mTvFeatured.setTextColor(getResources().getColor(R.color.comic_ffad70));
                mTvFeatured.getPaint().setFakeBoldText(true);
                mTvFeatured.setTextSize(TypedValue.COMPLEX_UNIT_DIP,18);
                mTvMan.setTextColor(getResources().getColor(R.color.comic_a8adb3));
                mTvMan.getPaint().setFakeBoldText(false);
                mTvMan.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
                mTvWoman.setTextColor(getResources().getColor(R.color.comic_a8adb3));
                mTvWoman.getPaint().setFakeBoldText(false);
                mTvWoman.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
                break;
            case 1:
                mTvFeatured.setTextColor(getResources().getColor(R.color.comic_a8adb3));
                mTvFeatured.getPaint().setFakeBoldText(false);
                mTvFeatured.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
                mTvMan.setTextColor(getResources().getColor(R.color.comic_ffad70));
                mTvMan.getPaint().setFakeBoldText(true);
                mTvMan.setTextSize(TypedValue.COMPLEX_UNIT_DIP,18);
                mTvWoman.setTextColor(getResources().getColor(R.color.comic_a8adb3));
                mTvWoman.getPaint().setFakeBoldText(false);
                mTvWoman.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
                break;
            case 2:
                mTvFeatured.setTextColor(getResources().getColor(R.color.comic_a8adb3));
                mTvFeatured.getPaint().setFakeBoldText(false);
                mTvFeatured.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
                mTvMan.setTextColor(getResources().getColor(R.color.comic_a8adb3));
                mTvMan.getPaint().setFakeBoldText(false);
                mTvMan.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
                mTvWoman.setTextColor(getResources().getColor(R.color.comic_ffad70));
                mTvWoman.getPaint().setFakeBoldText(true);
                mTvWoman.setTextSize(TypedValue.COMPLEX_UNIT_DIP,18);
                break;
        }
    }

    @Override
    public void onRefresh() {
        getP().loadData(recentChannelFlag,1, false,false);
        recentlyPage = 1;
        getP().loadRecentlyComic(recentlyPage,recentChannelFlag,false);
        getP().loadPopShare(recentChannelFlag,false);
        getP().getBanner();
    }

    @Override
    public void getDataFail(NetError error) {
        if (mRefresh.isRefreshing())
            mRefresh.setRefreshing(false);
        //设置不显示无数据的布局
//        adapter_content.setEmptyText(error.getMessage());
        adapter_content.setNewData(null);
//        hideProgress();
    }

    @Override
    public void onLoadRecentlyComicSuccess(boolean changeChannel,List<BookModel> bookModelList) {
        hideProgress();
        if (changeChannel || recentlyPage == 1) {
            adapter_recently.setNewData(bookModelList);
        } else {
            adapter_recently.addData(bookModelList);
        }
        adapter_recently.loadMoreComplete();
    }

    @Override
    public void onLoadRecentlyComicFail(NetError error) {
        hideProgress();
        if (error.getType() == NetError.NoDataError) {
            adapter_recently.loadMoreEnd(false);
        } else {
            adapter_recently.loadMoreFail();
        }
        adapter_recently.setEmptyText(error.getMessage());
        if (recentlyPage == 1) {
            adapter_recently.setNewData(null);
        } else {
            adapter_recently.addData(new ArrayList<BookModel>());
        }
        recentlyPage--;
    }

    @Override
    public void onLoadPopShareSucc(boolean changeChannel,List<BookModel> bookModelList) {
        adapterPopShare.setNewData(bookModelList);
    }

    @Override
    public void onLoadPopShareFail(NetError error) {

    }

    @Override
    public void onItemClick(BookModel model, String from) {
        //点击事件友盟统计
        getP().umengOnEvent(from, model);
        DetailActivityHelper.toDetail(getActivity(), model.getId(), from);
    }

    @Override
    public void adsPush(final Push push) {
        final CustomFragmentDialog dialog = new CustomFragmentDialog();
        dialog.show(getActivity(), getChildFragmentManager(), R.layout.comic_dialog_push_book, R.style.comic_Dialog_push_bg);
        ImageView close = dialog.getDialog().findViewById(R.id.close);
        ImageView bookDetail = dialog.getDialog().findViewById(R.id.push);
        if (push.getImg() != null && !"".equals(push.getImg())) {
            ILFactory.getLoader().loadNet(bookDetail, push.getImg()
                    , new RequestOptions().error(R.drawable.push_book));
        } else {
            bookDetail.setImageResource(R.drawable.push_book);
        }
        dialog.getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        bookDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailActivityHelper.toDetail(getActivity(), push.getText1(), "首页推送");
                dialog.dismiss();
            }
        });
    }

    private NormalNotifyDialog normalNotifyDialog;

    @Override
    public void onAdsPush_133(final Push push) {
        if (push != null && push.getId() != null && push.getId().equals(Constants.AD_ID_133)) {
            if (normalNotifyDialog == null) {
                normalNotifyDialog = new NormalNotifyDialog();
            }
            normalNotifyDialog.show(getChildFragmentManager(), push.getText2(), push.getText3(),
                    new DialogUtilForComic.OnDialogClick() {
                @Override
                public void onConfirm() {
                    String text1 = push.getText1();
                    if (!TextUtils.isEmpty(text1)) {
                        switch (push.getMode()) {
                            case "1"://手动下载，调用系统浏览器加载
                                Uri uri = Uri.parse(text1);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                                break;
                            case "2"://自动下载，调用应用内下载并在下载完成后安装
                                int lastIndexOf = text1.lastIndexOf(".apk");
                                if (lastIndexOf == -1) {
                                    onAdsPush_128_fail(new NetError("下载链接错误", NetError.OtherError));
                                    return;
                                }
                                AndPermission.with(getActivity())
                                        .runtime()
                                        .permission(Permission.WRITE_EXTERNAL_STORAGE)
                                        .onGranted(new Action<List<String>>() {
                                            @Override
                                            public void onAction(List<String> data) {
                                                getP().goDown(text1);
                                            }
                                        })
                                        .onDenied(new Action<List<String>>() {
                                            @Override
                                            public void onAction(List<String> data) {
                                                ToastUtil.showToastShort("存储权限获取失败，无法下载");
                                            }
                                        });
                                break;
                            case "3"://无需下载，调用应用内webview加载网页
                                ARouter.getInstance().build(RouterMap.COMIC_WEBVIEW_ACTIVITY)
                                        .withString("url", text1)
                                        .navigation(getActivity());
                                break;
                        }
                    }
                    getP().getAdsPush_Comic();
                }

                @Override
                public void onRefused() {
                    getP().getAdsPush_Comic();
                }
            });
        } else {
            getP().getAdsPush_Comic();
        }
    }

    public static final int START_DOWNLOAD = 1;
    public static final int DOWNING = START_DOWNLOAD + 1;
    public static final int DONE = DOWNING + 1;
    public static final int DOWN_FAIL = DONE + 1;
    private ProgressDialog mDialog;
    private Handler mDownHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_DOWNLOAD:
                    if (mDialog == null) {
                        mDialog = new ProgressDialog(getActivity(),
                                AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                        mDialog.setCancelable(false);
                        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    }
                    mDialog.show();
                    break;
                case DOWNING:
                    DownInfo info = (DownInfo) msg.obj;
                    int max = info.getContentLength() + info.getStartPos();
                    mDialog.setMax(max);
                    int progress = info.getBytesRead() + info.getStartPos();
                    mDialog.setProgress(progress);
                    float all = max / 1024f / 1024f;
                    float percent = progress / 1024f / 1024f;
                    mDialog.setProgressNumberFormat(String.format(getString(R.string.comic_download_progress_format),
                            percent, all));
                    break;
                case DONE:
                    if (mDialog.isShowing()) mDialog.dismiss();
                    IntentUtils.installApk(((File) msg.obj), (BaseActivity) getActivity());
                    break;
                case DOWN_FAIL:
                    if (mDialog.isShowing()) mDialog.dismiss();
                    showToastShort(getString(R.string.comic_download_fail));
                    break;
            }
        }
    };

    @Override
    public void sendMessage(int what, Object info) {
        Message message = info == null ? mDownHandler.obtainMessage(what) : mDownHandler.obtainMessage(what, info);
        message.sendToTarget();
    }

    @Override
    public void refreshBanner(BannerResponse bannerResponse) {
        final List<BannerResponse.DataBean> bannerResponseData = bannerResponse.getData();
        if (bannerResponseData != null) {
            mBanner.setOnBannerListener(new OnBannerListener() {
                @Override
                public void OnBannerClick(int position) {
                    BannerResponse.DataBean dataBean = bannerResponseData.get(position);
                    if (dataBean != null) {
                        if (dataBean.getBanner_type() == 1) {
                            BookModel bookModel = new BookModel();
                            bookModel.setId(dataBean.getBanner_articleid());
                            bookModel.setCover(dataBean.getBanner_img_url());
                            bookModel.setTitle(dataBean.getBanner_title());
                            onItemClick(bookModel, "banner");

                        } else {
                            String banner_url = dataBean.getBanner_url();
                            if (TextUtils.isEmpty(banner_url)) return;
                            if (banner_url.startsWith("www") || banner_url.startsWith("WWW"))
                                banner_url = "http://" + banner_url;
                            ARouter.getInstance().build(RouterMap.COMIC_WEBVIEW_ACTIVITY)
                                    .withString("url", banner_url)
                                    .navigation(getActivity());
                        }

                    }
                }
            });
            mBanner.update(bannerResponseData);
        }
        mRefresh.setRefreshing(false);
    }

    @Override
    public void getBannerFail() {
        mRefresh.setRefreshing(false);
    }

    @Override
    public void onGetPayAction(PayActionResponse.DataBean.PayinfoBean payinfoBean,int close_time) {
    }

    @Override
    public void onAdsPush_128_fail(NetError netError) {
        getP().getAdsPush_Comic();
    }

}
