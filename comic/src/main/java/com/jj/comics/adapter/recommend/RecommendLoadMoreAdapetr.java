package com.jj.comics.adapter.recommend;

import android.text.Html;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.base.imageloader.ILFactory;
import com.jj.comics.R;
import com.jj.comics.data.model.BookModel;

import java.util.List;

public class RecommendLoadMoreAdapetr extends SimpleBaseAdapter<BookModel> {

    private RequestOptions options;

    public RecommendLoadMoreAdapetr(int layoutResId) {
        super(layoutResId);
        options = new RequestOptions()
                .placeholder(R.drawable.img_loading)
                .error(R.drawable.img_loading)
                .transforms(new CenterCrop(), new RoundedCorners(12));
    }

    @Override
    protected void convert(BaseViewHolder helper, BookModel mainContent) {
        helper.setText(R.id.tv_rank_title, mainContent.getTitle());
        try {
            helper.setText(R.id.tv_des, Html.fromHtml(mainContent.getIntro().replace("%", "%%")));
        } catch (Exception e) {
            helper.setText(R.id.tv_des, mainContent.getIntro());
        }
        List<String> tags = mainContent.getTag();
        if (tags.size() > 1) {
            helper.setText(R.id.tv_rank_type_2, tags.get(1));
        }
        helper.setText(R.id.tv_rank_type_1, tags.get(0));
        ILFactory.getLoader()
                .loadNet(helper.<ImageView>getView(R.id.iv_rank_cover),
                        mainContent.getCover(), options);

    }
}
