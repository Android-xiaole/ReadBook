package com.jj.comics.adapter.recommend;

import android.os.Build;
import android.text.Html;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.base.imageloader.ILFactory;
import com.jj.comics.R;
import com.jj.comics.data.model.BookModel;

import java.util.List;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

public class FreeListAdapter extends SimpleBaseAdapter<BookModel> {
    public FreeListAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, BookModel mainContent) {
        ILFactory.getLoader().loadNet(helper.<ImageView>getView(R.id.item_free_img),
                mainContent.getCover(),
                new RequestOptions().error(R.drawable.img_loading).placeholder(R.drawable.img_loading));
        helper.setText(R.id.item_free_name, mainContent.getTitle());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            helper.setText(R.id.item_free_detail, Html.fromHtml(mainContent.getIntro(),
                    FROM_HTML_MODE_LEGACY));
        } else {
            helper.setText(R.id.item_free_detail, Html.fromHtml(mainContent.getIntro()));
        }
        List<String> tags = mainContent.getTag();
        if (tags.size() > 0)
        helper.setText(R.id.item_free_tag, tags.get(0));
    }
}
