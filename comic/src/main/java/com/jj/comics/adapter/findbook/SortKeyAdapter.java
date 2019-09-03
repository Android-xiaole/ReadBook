package com.jj.comics.adapter.findbook;

import android.content.Context;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.comics.R;
import com.jj.comics.data.model.TypeSortKeyBean;

import java.util.ArrayList;
import java.util.List;

public class SortKeyAdapter extends SimpleBaseAdapter<TypeSortKeyBean> {
    private int selectIndex = 0;

    public SortKeyAdapter(Context context, int layoutResId) {
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
    public void setNewData(@Nullable List<TypeSortKeyBean> data) {
        if (data == null) data = new ArrayList<>();
        super.setNewData(data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TypeSortKeyBean item) {
        helper.setText(R.id.findBook_type, item.getName());
        if (getData().indexOf(item) == selectIndex) {
            helper.setTextColor(R.id.findBook_type, mContext.getResources().getColor(R.color.comic_ffffff));
            helper.getView(R.id.findBook_type).setBackgroundResource(R.drawable.comic_bg_tab_checked);
        } else {
            helper.setTextColor(R.id.findBook_type, mContext.getResources().getColor(R.color.comic_666666));
            helper.getView(R.id.findBook_type).setBackgroundResource(R.color.comic_ffffff);
        }
    }
}
