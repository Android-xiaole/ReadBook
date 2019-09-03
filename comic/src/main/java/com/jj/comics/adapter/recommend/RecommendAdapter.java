package com.jj.comics.adapter.recommend;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.base.imageloader.ILFactory;
import com.jj.base.utils.Utils;
import com.jj.comics.R;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.SectionModel;
import com.jj.comics.widget.RecycleViewDivider;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecommendAdapter extends SimpleBaseAdapter<SectionModel> {

    private OnClickListener mOnClick;
    private static final String TYPE_2_2 = "2_2";//默认布局   2*2
    private static final String TYPE_1_3 = "1_3";//1+3
    private static final String TYPE_3_0 = "3_0";//3+0
    private static final String TYPE_3_3 = "3_3";//3+3

    public RecommendAdapter(int layoutResId) {
        super(layoutResId);
    }

    //使用统一回调实现跳转
    public void setOnClick(OnClickListener onClick) {
        this.mOnClick = onClick;
    }

    @Override
    protected void convert(BaseViewHolder helper, SectionModel item) {
        final String value = item.getName();
        helper.setText(R.id.item_recommend_title, value).setTag(R.id.item_recommend_title, value);

        ILFactory.getLoader().loadNet(helper.<ImageView>getView(R.id.item_recommend_icon), item.getImage1()
                , new RequestOptions().error(R.drawable.icon_recommend_navigation_home).placeholder(R.drawable.icon_recommend_navigation_home));

        helper.addOnClickListener(R.id.tv_loadMore);

        final RecyclerView recyclerView = helper.getView(R.id.item_recommend_recycler);
        BaseQuickAdapter<BookModel, BaseViewHolder> adapter = null;
        GridLayoutManager manager = new GridLayoutManager(mContext, 2);
        String style = item.getStyle();
        if (style == null) style = "";
        if (TextUtils.equals(style, TYPE_1_3)) {
            manager.setSpanCount(3);
            recyclerView.setLayoutManager(manager);
            adapter = new RecommendChild2Adapter(Integer.MAX_VALUE, true, R.layout.comic_item_recommend_vertical_hight, value);
            ((RecommendChild2Adapter) adapter).setOnClick(mOnClick);
        } else if (TextUtils.equals(style, TYPE_3_0)) {
            manager.setSpanCount(3);
            recyclerView.setLayoutManager(manager);
            adapter = new RecommendChildAdapter(R.layout.comic_item_recommend_vertical_hight, Integer.MAX_VALUE, true,false);
        } else if (TextUtils.equals(style, TYPE_3_3)) {
            manager.setSpanCount(3);
            recyclerView.setLayoutManager(manager);
            adapter = new RecommendChildAdapter(R.layout.comic_item_recommend_vertical_hight,
                    Integer.MAX_VALUE, true,false);
        } else if (TextUtils.equals(style,TYPE_2_2)){
            manager.setSpanCount(2);
            recyclerView.setLayoutManager(manager);
            adapter = new RecommendChildAdapter(R.layout.comic_item_recommend_vertical,
                    Integer.MAX_VALUE, false,true);
        }
        if (!TextUtils.equals(style, TYPE_1_3)&&recyclerView.getItemDecorationCount() <= 0) {
            recyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.HORIZONTAL, Utils.dip2px(mContext, 6), Color.WHITE));
            recyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.VERTICAL, Utils.dip2px(mContext, 10), Color.WHITE));
        }

        adapter.bindToRecyclerView(recyclerView);
        //给每个子item设置监听  另外 由于RecommendChild2Adapter中是以头布局进行排版  所以也要把mOnClick传过去便于监听
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                BookModel mainContent = (BookModel) adapter.getData().get(position);
//                onEvent(value, mainContent);
                if (mOnClick != null) {
                    mOnClick.onItemClick(mainContent, value);
                }
            }
        });
        adapter.setNewData(item.getContentList());
    }

    @Override
    public void setNewData(@Nullable List<SectionModel> data) {
        if (data != null) setDataType(data);
        super.setNewData(data);
    }

    private void setDataType(List<SectionModel> data) {
        boolean needSetType = true;
        for (SectionModel datum : data) {
            if (!TextUtils.isEmpty(datum.getStyle())) needSetType = false;
            break;
        }
        if (needSetType) {
            for (int i = 0; i < data.size(); i++) {
                switch (i) {
                    case 1:
                        data.get(i).setStyle(TYPE_1_3);
                        break;
                    case 3:
                    case 5:
                        data.get(i).setStyle(TYPE_3_0);
                        break;
                    case 4:
                        data.get(i).setStyle(TYPE_3_3);
                        break;
                }
            }
        }
    }

    public interface OnClickListener {
        void onItemClick(BookModel model, String from);
    }
}
