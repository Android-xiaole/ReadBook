package com.jj.comics.adapter.bookshelf;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.base.imageloader.ILFactory;
import com.jj.comics.R;
import com.jj.comics.data.model.BookModel;

import java.util.ArrayList;
import java.util.List;

public class BookShelfAdapter extends SimpleBaseAdapter<BookModel> {
    private boolean isEditMode = false;
    private List<BookModel> mDelete;

    public BookShelfAdapter(int layoutResId) {
        super(layoutResId);
        mDelete = new ArrayList<>();
    }

    @Override
    protected void convert(BaseViewHolder helper, BookModel item) {
        helper.<CheckBox>getView(R.id.item_bookshelf_select).setChecked(false);
        helper.<CheckBox>getView(R.id.item_bookshelf_select).setClickable(false);
        helper.getView(R.id.item_bookshelf_select).setVisibility(View.GONE);
        helper.getView(R.id.item_bookShelf_continue).setVisibility(View.VISIBLE);
        helper.setText(R.id.item_bookshelf_name, item.getTitle());
        if (item.getTag() != null && item.getTag().size() > 0) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < item.getTag().size(); i++) {
                if (i > 1) {
                    break;
                }
                builder.append(item.getTag().get(i)).append(",");
            }
            helper.setText(R.id.item_bookshelf_tag, builder.toString().substring(0, builder.length() - 1));
        }
        if (item.getFullflag() == 1){
            helper.setText(R.id.item_bookshelf_record, "阅读至"+item.getOrder()+"话/已完结");
        }else{
            helper.setText(R.id.item_bookshelf_record, String.format(mContext.getString(R.string.comic_bookshelf_record_format), item.getOrder(), item.getLastvolume()));
        }
        ILFactory.getLoader().loadNet(helper.<ImageView>getView(R.id.item_bookshelf_img), item.getCover(),
                new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(10))
                        .error(R.drawable.img_loading)
                        .placeholder(R.drawable.img_loading));
        helper.getView(R.id.item_bookshelf_select).setVisibility(isEditMode ? View.VISIBLE : View.GONE);
        helper.getView(R.id.item_bookShelf_continue).setVisibility(isEditMode ? View.GONE : View.VISIBLE);
        helper.<CheckBox>getView(R.id.item_bookshelf_select).setChecked(mDelete.contains(item));
        helper.addOnClickListener(R.id.item_bookShelf_continue);
    }

    public void autoSelect(int position) {
        BookModel bookModel = getItem(position);
        if (mDelete.contains(bookModel)) {
            mDelete.remove(bookModel);
        } else mDelete.add(bookModel);
        refreshNotifyItemChanged(position);
    }

    public boolean isSelectAll() {
        if (getData().size() == 0) return false;
        return getDelete().size() == getData().size();
    }

    public void setEditMode(boolean editMode) {
        if (editMode != isEditMode) {
            isEditMode = editMode;
            notifyItemRangeChanged(getHeaderLayoutCount(), getData().size());
        }
    }

    public boolean isEditMode() {
        return isEditMode;
    }

    public List<BookModel> getDelete() {
        return mDelete;
    }

    public void autoSelectAllOrNot() {
        if (isSelectAll()) {
            mDelete.clear();
            notifyItemRangeChanged(getHeaderLayoutCount(), getData().size());
        } else {//全选
            for (int i = 0; i < getData().size(); i++) {
                BookModel bookModel = getItem(i);
                if (!mDelete.contains(bookModel)) {
                    mDelete.add(bookModel);
                    refreshNotifyItemChanged(i);
                }
            }
        }
    }
}
