package com.jj.comics.adapter.detail;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.jj.base.BaseApplication;
import com.jj.base.imageloader.ILFactory;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.CommonUtil;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.R;
import com.jj.comics.data.biz.content.ContentRepository;
import com.jj.comics.data.model.BookCatalogModel;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.CustomModel;
import com.jj.comics.data.model.RewardListResponse;
import com.jj.comics.ui.read.ReadComicActivity;
import com.jj.comics.widget.comic.ComicLinearLayoutManager;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

//漫画的详细信息
public class ReadComicAdapter extends BaseMultiItemQuickAdapter<CustomModel, BaseViewHolder> {
    private BookModel bookModel;
    private ArrayList<BookCatalogModel> catalogModels;
    private BaseActivity mBaseActivity;
    //加载图片配置
    private RequestOptions mOptions;
    private RewardRecordByCotentAdapter mRewardRecordAdapter;
    private List<RewardListResponse.DataBean.RewardRecordBean> mRewards;

    private BaseQuickAdapter.OnItemChildClickListener mFootClickListener;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public ReadComicAdapter(List<CustomModel> data) {
        super(data);
    }

    public ReadComicAdapter(BaseActivity baseActivity, @NonNull BookModel model) {
        this(new ArrayList<CustomModel>());
        addItemType(0, R.layout.comic_read_comic_item);//只有一张图片的布局
        addItemType(1, R.layout.comic_read_comic_item2);//每一章节末尾的打赏评论相关布局
        this.bookModel = model;
        mBaseActivity = baseActivity;
        catalogModels = new ArrayList<>(3);
        mOptions = new RequestOptions()
                .placeholder(R.drawable.img_loading)
                .error(R.drawable.img_loading);
//                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
//                .format(DecodeFormat.PREFER_ARGB_8888)
//                .priority(Priority.IMMEDIATE)
//                .override(Integer.MIN_VALUE)//手动设置大小 以获取图片的真实宽高  防止加载图片过大
//                .skipMemoryCache(true);

        ActivityManager am = (ActivityManager) BaseApplication.getApplication().getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(outInfo);
            if (outInfo.lowMemory) {
                mOptions.skipMemoryCache(true)
                        .encodeQuality(60);
            }
        }
    }

    /**
     * 展示漫画
     *
     * @param model           漫画展示model
     * @param isScrollByModel 是否需要滑动到展示model
     */
    public void addCatalog(BookCatalogModel model, boolean isScrollByModel) {
        //是同一本漫画并且漫画有效
//        if (model != null && model.getContent() != null) {
//            ArrayList<CustomModel> data = new ArrayList<>();
//            for (String imageUrl : model.getContent()) {
//                CustomModel customModel = new CustomModel(imageUrl, 0);
//                data.add(customModel);
//            }
            //设置尾部局
//            data.add(new CustomModel("", 1));

//            if (!CommonUtil.checkEmpty(catalogModels)) {
//                //获取第一本漫画
//                if (catalogModels.get(0).getChapterorder() - model.getChapterorder() == 1) {
//                    //此时model为前一章 删除最后一章 model添加到一章
//                    while (catalogModels.size() >= 3) {
//                        BookCatalogModel catalogModel = catalogModels.get(catalogModels.size() - 1);
//                        if (!CommonUtil.checkEmpty(catalogModel.getContent())) {
//                            int index = getData().indexOf(new CustomModel(catalogModel.getContent().get(0), 0));
//                            for (int i = 0; i < catalogModel.getContent().size(); i++) {
//                                getData().remove(new CustomModel(catalogModel.getContent().get(i), 0));
//                            }
//                            getData().remove(getData().size() - 1);
//                            notifyItemRangeRemoved(index, catalogModel.getContent().size() + 1);
//                        }
//                        catalogModels.remove(catalogModel);
//                    }
//                    catalogModels.add(0, model);
//                    addData(0, data);
//                } else if (catalogModels.get(catalogModels.size() - 1).getChapterorder() - model.getChapterorder() == -1) {
//                    //此时model为后一章 删除第一章 model添加到最后一章
//                    while (catalogModels.size() >= 3) {
//                        BookCatalogModel catalogModel = catalogModels.get(0);
//                        if (!CommonUtil.checkEmpty(catalogModel.getContent())) {
//                            int index = getData().indexOf(new CustomModel(catalogModel.getContent().get(0), 0));
//                            for (int i = 0; i < catalogModel.getContent().size(); i++) {
//                                getData().remove(new CustomModel(catalogModel.getContent().get(i), 0));
//                            }
//                            //删除尾部局
//                            getData().remove(0);
//                            notifyItemRangeRemoved(index, catalogModel.getContent().size() + 1);
//                        }
//                        catalogModels.remove(catalogModel);
//                    }
//                    catalogModels.add(model);
//                    addData(data);
//                } else if (catalogModels.contains(model)) {
//                    //相同不作处理  什么情况下才会相同？？ 当前展示漫画中含有待展示漫画才会相同 此时滑动到当前位置
//                    scrollToPosition(model);
//                } else {
//                    //此时model与catalogModel不连贯 清空集合 添加model
//                    catalogModels.clear();
//                    catalogModels.add(model);
//                    setNewData(data);
//                }
//            } else {
//                if (catalogModels == null) catalogModels = new ArrayList<>(3);
//                catalogModels.add(model);
//                setNewData(data);
//            }
////            if (isScrollByModel)
//                scrollToPosition(model);
//        }
    }

//    public boolean hasFrist() {
//        if (!CommonUtil.checkEmpty(catalogModels)) {
//            return catalogModels.get(0).getChapterorder() == 1;
//        }
//        return false;
//    }

//    public boolean hasLast() {
//        // TODO: 2019-07-24 need help!!!!!
//        if (!CommonUtil.checkEmpty(catalogModels)) {
//            return catalogModels.get(catalogModels.size() - 1).getChapterorder() == bookModel.getSubCount();
//        }
//        return false;
//    }

    public BookCatalogModel getCatalog(int position) {
        if (CommonUtil.checkValid(getData().size(), position)) {
            CustomModel customModel = getData().get(position);
            //如果是末布局 则取上一个customModel
            while (customModel.getItemType() == 1 && position > 1) {
                position = position - 1;
                customModel = getData().get(position);
            }
//            for (BookCatalogModel catalogModel : catalogModels) {
//                if (catalogModel.getContent().contains(customModel.getUrl())) {
//                    return catalogModel;
//                }
//            }
        }
        return null;
    }

    public void setFootClickListener(BaseQuickAdapter.OnItemChildClickListener footClickListener) {
        this.mFootClickListener = footClickListener;
    }

    @Override
    protected void convert(BaseViewHolder helper, CustomModel item) {
        switch (item.getItemType()) {
            case 1:
                convertFooter(helper, item);
                break;
            default:
                convertDefault(helper, item);
                break;
        }
    }

    //    private RelativeLayout mLoading;
    private void convertFooter(BaseViewHolder helper, CustomModel item) {
        RecyclerView rewardRecordRecycler = helper.getView(R.id.rv_rewardList);
        rewardRecordRecycler.setLayoutManager(new ComicLinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mRewardRecordAdapter = new RewardRecordByCotentAdapter(R.layout.comic_detail_rewardlist_item);
        View emptyView = View.inflate(mContext, R.layout.comic_detail_reward_emptyview, null);
        mRewardRecordAdapter.setEmptyView(emptyView);
        //这里要隐藏这个空布局的打赏按钮
        emptyView.findViewById(R.id.btn_reward).setVisibility(View.GONE);
        if (mRewards == null) getRewardRecordList(mBaseActivity);
        else mRewardRecordAdapter.setNewData(mRewards);
        rewardRecordRecycler.setAdapter(mRewardRecordAdapter);

        ImageView collection = helper.getView(R.id.comic_read_collection);
        ImageView favor = helper.getView(R.id.comic_read_favor);
        View readUp = helper.getView(R.id.lin_comic_read_up);
        View readDown = helper.getView(R.id.lin_comic_read_down);
//        mLoading = helper.getView(R.id.pb_read_loading);

        //设置子view点击事件
        int position = getData().indexOf(item);
        helper.getView(R.id.lin_comic_read_collection).setOnClickListener(new OnFootClickListener(position));
        helper.getView(R.id.lin_comic_read_favor).setOnClickListener(new OnFootClickListener(position));
        helper.getView(R.id.lin_comic_read_comment).setOnClickListener(new OnFootClickListener(position));
        helper.getView(R.id.iv_reward).setOnClickListener(new OnFootClickListener(position));
        readUp.setOnClickListener(new OnFootClickListener(position));
        readDown.setOnClickListener(new OnFootClickListener(position));

        ILFactory.getLoader().loadResource(collection, R.drawable.icon_comic_read_uncollection_black, null);
        ILFactory.getLoader().loadResource(favor, R.drawable.img_comic_read_reward_dianzan_false, null);
        readUp.setSelected(false);
        readDown.setSelected(false);
        if (mContext != null && mContext instanceof ReadComicActivity) {
            ReadComicActivity readComicActivity = ((ReadComicActivity) mContext);
            ILFactory.getLoader().loadResource(collection,
                    readComicActivity.isCollect() ? R.drawable.icon_comic_read_bottombar_collection : R.drawable.icon_comic_read_uncollection_black, null);
//            ILFactory.getLoader().loadResource(favor,
//                    readComicActivity.isFavor() ? R.drawable.img_comic_read_reward_dianzan_true : R.drawable.img_comic_read_reward_dianzan_false, null);
            BookCatalogModel catalogModel = getCatalog(position);
            if (catalogModel != null) {
                if (!catalogModel.isHasLast() && catalogModel.isHasNext()) {
                    readUp.setSelected(false);
                    readDown.setSelected(true);
                } else if (catalogModel.isHasLast() && !catalogModel.isHasNext()) {
                    readUp.setSelected(true);
                    readDown.setSelected(false);
                } else if (catalogModel.isHasLast() && catalogModel.isHasNext()) {
                    readUp.setSelected(true);
                    readDown.setSelected(true);
                }
            }
        }
    }

    private void convertDefault(BaseViewHolder helper, CustomModel item) {
        SubsamplingScaleImageView view = helper.getView(R.id.comic_read_comic_img);
//        view.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM);
//        final ComicCustomViewTarget comicCustomViewTarget = new ComicCustomViewTarget(view);
//        ILFactory.getLoader().loadDrawable(mContext, item.getUrl(), mOptions, comicCustomViewTarget);
        final ComicCustomViewTarget comicCustomViewTarget = new ComicCustomViewTarget(view);
        ILFactory.getLoader().loadFile(mContext, item.getUrl(), mOptions, comicCustomViewTarget);
//        view.setTag(R.id.tag_id, comicCustomViewTarget);
        view.setTag(R.id.tag_url, item.getUrl());
    }

    @Override
    public void onViewRecycled(@NonNull BaseViewHolder holder) {
        if (mContext == null) {
            return;
        }
        /*
            为了解决偶尔存在的异常
            Glide Exception:You cannot start a load for a destroyed activity
         */
        if (mContext instanceof Activity) {
            if (((Activity) mContext).isDestroyed()) {
                return;
            }
        }
        //当控件被回收时  清除缓存
        if (holder.itemView != null && holder.itemView instanceof SubsamplingScaleImageView) {
            Glide.with(mContext).clear(holder.itemView);
        }
        super.onViewRecycled(holder);
    }

    public void getRewardRecordList(BaseActivity baseActivity) {
        ContentRepository.getInstance().getRewardRecordByContent(bookModel.getId(), 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.<RewardListResponse>autoDisposable(AndroidLifecycleScopeProvider.from(baseActivity, Lifecycle.Event.ON_DESTROY)))
                .subscribe(new ApiSubscriber2<RewardListResponse>() {
                    @Override
                    public void onNext(RewardListResponse response) {
                        if (response.getData() != null && response.getData().getData() != null) {
                            mRewards = response.getData().getData();
                            mRewardRecordAdapter.setNewData(mRewards);
                        } else {
                            ToastUtil.showToastShort(response.getMessage());
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToastShort(error.getMessage());
                    }
                });
    }

    public void setComicDetail() {
        List<CustomModel> data = getData();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getItemType() == 1) {
                refreshNotifyItemChanged(i);
            }
        }
    }

    public void scrollToPosition(BookCatalogModel model) {
//        List<String> imageUrls = model.getContent();
//        if (!CommonUtil.checkEmpty(imageUrls) && getRecyclerView() != null) {
//            final int position = getData().indexOf(new CustomModel(imageUrls.get(0), 0)) + getHeaderLayoutCount();
//            if (getRecyclerView() != null && getRecyclerView().getLayoutManager() instanceof LinearLayoutManager) {
//                final LinearLayoutManager layoutManager = (LinearLayoutManager) getRecyclerView().getLayoutManager();
//                //这种方式是定位到指定项如果该项可以置顶就将其置顶显示
//                layoutManager.scrollToPositionWithOffset(position, 0);
//                layoutManager.scrollToPosition(position);
//                TopSmoothScroller mScroller = new TopSmoothScroller(mContext);
//                mScroller.setTargetPosition(position);
//                layoutManager.startSmoothScroll(mScroller);

//                getRecyclerView().post(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (layoutManager.findFirstVisibleItemPosition() != position) {
//                            View view = layoutManager.findViewByPosition(position);
//                            if (view != null)
//                                getRecyclerView().scrollBy(0, Utils.getScreenHeight(mContext) - view.getHeight());
//                        }
//                    }
//                });
//            }

//        }
    }

    public class TopSmoothScroller extends LinearSmoothScroller {
        TopSmoothScroller(Context context) {
            super(context);
        }

        @Override
        protected int getHorizontalSnapPreference() {
            return SNAP_TO_START;//具体见源码注释
        }

        @Override
        protected int getVerticalSnapPreference() {
            return SNAP_TO_START;//具体见源码注释
        }
    }

    private class OnFootClickListener implements View.OnClickListener {
        private int position;

        public OnFootClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if (mFootClickListener != null)
                mFootClickListener.onItemChildClick(ReadComicAdapter.this, v, position);
        }
    }

    /**
     * 自定义控件加载器
     */

    private class ComicCustomViewTarget extends CustomViewTarget<SubsamplingScaleImageView, File> {

        public ComicCustomViewTarget(@NonNull SubsamplingScaleImageView view) {
            super(view);
        }

        @Override
        protected void onResourceLoading(@Nullable Drawable placeholder) {
            super.onResourceLoading(placeholder);
            view.setImage(ImageSource.resource(R.drawable.img_loading));
        }

        @Override
        protected void onResourceCleared(@Nullable Drawable placeholder) {
            view.setImage(ImageSource.resource(R.drawable.img_loading));
        }

        @Override
        public void onLoadFailed(@Nullable Drawable errorDrawable) {
            view.setImage(ImageSource.resource(R.drawable.img_loading));
        }

        @Override
        public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
            view.setImage(ImageSource.uri(Uri.fromFile(resource)));
            DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
            view.setMaxScale(metrics.widthPixels / metrics.density);
        }
    }
}