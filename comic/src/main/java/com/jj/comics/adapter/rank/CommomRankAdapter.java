package com.jj.comics.adapter.rank;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.base.imageloader.ILFactory;
import com.jj.comics.R;
import com.jj.comics.data.model.BookModel;

import java.util.List;

public class CommomRankAdapter extends SimpleBaseAdapter<BookModel> {

    public CommomRankAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, BookModel mainContent) {
        helper.<ImageView>getView(R.id.iv_rank_icon).setVisibility(View.VISIBLE);
        helper.<RelativeLayout>getView(R.id.rl_rank_iv_holder).setVisibility(View.INVISIBLE);
        helper.setText(R.id.tv_rank_title, mainContent.getTitle());
        List<String> tags = mainContent.getTag();
        if (tags.size() > 1) {
            helper.getView(R.id.tv_rank_type_2).setVisibility(View.VISIBLE);
            helper.setText(R.id.tv_rank_type_2, tags.get(1));
        }else {
            helper.getView(R.id.tv_rank_type_2).setVisibility(View.INVISIBLE);
        }
        helper.setText(R.id.tv_rank_type_1, tags.get(0));

        String hot_const = mainContent.getHot_const();
        if (TextUtils.isEmpty(hot_const)) {
            hot_const = "未知";
        }else if (!hot_const.startsWith("热度")) {
            hot_const = "热度" + hot_const;
        }
        helper.setText(R.id.tv_rank_hot, hot_const);

        helper.setText(R.id.tv_rank_update,
                String.format(mContext.getString(R.string.comic_update_sub), mainContent.getLastvolume()));
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.img_loading)
                .error(R.drawable.img_loading)
                .transforms(new CenterCrop(), new RoundedCorners(12));
        ILFactory.getLoader()
                .loadNet(helper.<ImageView>getView(R.id.iv_rank_cover),
                        mainContent.getCover(), options);

        int index = getData().indexOf(mainContent);
        if (index == 0) {

            helper.<ImageView>getView(R.id.iv_rank_icon).setVisibility(View.VISIBLE);
            helper.<RelativeLayout>getView(R.id.rl_rank_iv_holder).setVisibility(View.INVISIBLE);
            ILFactory.getLoader().loadResource(helper.<ImageView>getView(R.id.iv_rank_icon),
                    R.drawable.icon_first, null);
        } else if (index == 1) {
            helper.<ImageView>getView(R.id.iv_rank_icon).setVisibility(View.VISIBLE);
            helper.<RelativeLayout>getView(R.id.rl_rank_iv_holder).setVisibility(View.INVISIBLE);
            ILFactory.getLoader().loadResource(helper.<ImageView>getView(R.id.iv_rank_icon),
                    R.drawable.icon_second, null);
        } else if (index == 2) {
            helper.<ImageView>getView(R.id.iv_rank_icon).setVisibility(View.VISIBLE);
            helper.<RelativeLayout>getView(R.id.rl_rank_iv_holder).setVisibility(View.INVISIBLE);
            ILFactory.getLoader().loadResource(helper.<ImageView>getView(R.id.iv_rank_icon),
                    R.drawable.icon_third, null);
        } else {
            helper.<ImageView>getView(R.id.iv_rank_icon).setVisibility(View.INVISIBLE);
            helper.<RelativeLayout>getView(R.id.rl_rank_iv_holder).setVisibility(View.VISIBLE);
            ILFactory.getLoader().loadResource(helper.<ImageView>getView(R.id.iv_rank_icon_common),
                    R.drawable.img_comic_commomrank_xunzhang, null);
            helper.setText(R.id.tv_rank_num, index + 1 + "");
        }
    }
}
