package com.jj.comics.adapter.findbook;

import android.content.Context;

import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.comics.R;
import com.jj.comics.data.model.CategoryResponse;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class FindBookTypeAdapter extends SimpleBaseAdapter<CategoryResponse.DataBean> {
    private int selectIndex = 0;

    public FindBookTypeAdapter(Context context, int layoutResId) {
        super(layoutResId);
        this.mContext = context;
    }

    public void setSelectIndex(int selectIndex) {
        if (selectIndex != this.selectIndex) {
            int oldIndex = this.selectIndex;
            this.selectIndex = selectIndex;
            notifyItemChanged(this.selectIndex);
            notifyItemChanged(oldIndex);
        }
    }

    @Override
    public void setNewData(@Nullable List<CategoryResponse.DataBean> data) {
        if (data == null) data = new ArrayList<>();
        CategoryResponse.DataBean comicBookType = new CategoryResponse.DataBean();
        comicBookType.setTitle(mContext.getString(R.string.comic_all_text));
        data.add(0, comicBookType);
        super.setNewData(data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CategoryResponse.DataBean item) {
        helper.setText(R.id.findBook_type, item.getTitle());
        if (getData().indexOf(item) == selectIndex) {
//            helper.getView(R.id.findBook_type).setBackgroundResource(R.drawable.comic_find_book_type);
            helper.setTextColor(R.id.findBook_type, mContext.getResources().getColor(R.color.comic_ffffff));
            helper.getView(R.id.findBook_type).setBackgroundResource(R.drawable.comic_bg_tab_checked);
        } else {
//            helper.getView(R.id.findBook_type).setBackgroundColor(mContext.getResources().getColor(android.R.color.white));
            helper.setTextColor(R.id.findBook_type, mContext.getResources().getColor(R.color.comic_666666));
            helper.getView(R.id.findBook_type).setBackgroundResource(R.color.comic_ffffff);
        }
    }
}
