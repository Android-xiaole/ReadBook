package com.jj.comics.adapter.bookshelf;

import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.base.imageloader.ILFactory;
import com.jj.comics.R;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.util.DateHelper;

import java.util.List;

/**
 * 通用的书籍列表适配器
 */
public class CommonBookListAdapter extends SimpleBaseAdapter<BookModel> {

    public static final String NAME_COLLECTION = "CollectionFragment";
    public static final String NAME_HISTORY = "HistoryFragment";

    private String pageName;//来自于哪个页面的名称

    public CommonBookListAdapter(int layoutResId, String pageName) {
        super(layoutResId);
        this.pageName = pageName;
    }


    @Override
    protected void convert(BaseViewHolder helper, BookModel item) {
        helper.setText(R.id.tv_item_history_title, item.getTitle());
        helper.setText(R.id.tv_item_history_time, DateHelper.formatSecLong(item.getUpdate_time()));
        if (pageName.equals(NAME_COLLECTION)){//来自于书架页面
            if (item.getFullflag() == 1){//已完结
                helper.setText(R.id.tv_item_history_progress, "已完结");
            }else{
                helper.setText(R.id.tv_item_history_progress, "更新至："+item.getLastvolume_name());
            }
        }else if (pageName.equals(NAME_HISTORY)){//来自于阅读历史页面
            helper.setText(R.id.tv_item_history_progress,"阅读至："+item.getChaptername());
        }
        ILFactory.getLoader().loadNet(helper.<ImageView>getView(R.id.iv_item_history_cover), item.getCover(),
                new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(10))
                        .error(R.drawable.img_loading)
                        .placeholder(R.drawable.img_loading));
        helper.addOnClickListener(R.id.right);
        helper.addOnClickListener(R.id.content);
    }

}
