package com.jj.comics.adapter.bookshelf;

import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.imageloader.ILFactory;
import com.jj.comics.R;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.util.DateHelper;

import java.util.List;

public class BookShelfAdapter extends BaseItemDraggableAdapter<BookModel,BaseViewHolder> {

    public BookShelfAdapter(int layoutResId, List<BookModel> data) {
        super(layoutResId, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, BookModel item) {
        helper.setText(R.id.tv_item_history_title, item.getTitle());
        helper.setText(R.id.tv_item_history_time, DateHelper.formatSecLong(item.getUpdate_time()));
        if (item.getFullflag() == 1){
            helper.setText(R.id.tv_item_history_progress, "阅读到第"+item.getOrder()+"章");
        }else{
            helper.setText(R.id.tv_item_history_progress, String.format(mContext.getString(R.string.comic_bookshelf_record_format), item.getOrder()));
        }
        ILFactory.getLoader().loadNet(helper.<ImageView>getView(R.id.iv_item_history_cover), item.getCover(),
                new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(10))
                        .error(R.drawable.img_loading)
                        .placeholder(R.drawable.img_loading));
        helper.addOnClickListener(R.id.right);
        helper.addOnClickListener(R.id.content);
    }

}
