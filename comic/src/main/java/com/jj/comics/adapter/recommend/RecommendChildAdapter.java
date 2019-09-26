package com.jj.comics.adapter.recommend;

import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.base.imageloader.ILFactory;
import com.jj.base.utils.Utils;
import com.jj.comics.R;
import com.jj.comics.data.model.BookModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class RecommendChildAdapter extends SimpleBaseAdapter<BookModel> {
    protected int mSize;
    protected boolean imageVertical = true;
    protected boolean useCustom = false;

    public RecommendChildAdapter(int layoutResId, int size, boolean imageVertical,
                                 boolean useCustom) {
        this(layoutResId);
        this.mSize = size;
        this.imageVertical = imageVertical;
        this.useCustom = useCustom;
    }

    @Override
    public void setNewData(@Nullable List<BookModel> data) {
        //2019-9-26 不对输入数据长度做处理
//        ArrayList<BookModel> realData = new ArrayList<>();
//        for (int i = 0; i < Math.min(data.size(), mSize); i++) {
//            realData.add(data.get(i));
//        }
        super.setNewData(data);
    }

    public RecommendChildAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, BookModel mainContent) {
        String imageUrl = getImageUrl(imageVertical, useCustom,mainContent);
        if (!TextUtils.isEmpty(imageUrl)) {
            ILFactory.getLoader().loadNet(helper.<ImageView>getView(R.id.item_recommend_img),
                    imageUrl,
                    new RequestOptions().transforms(new CenterCrop())
                            .error(R.drawable.img_loading)
                            .placeholder(R.drawable.img_loading));

        } else {
            ILFactory.getLoader().loadResource(helper.<ImageView>getView(R.id.item_recommend_img),
                    R.drawable.img_loading,
                    new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(Utils.dip2px(mContext,5))));
        }
        helper.setText(R.id.item_recommend_name, mainContent.getTitle());
//        helper.setText(R.id.item_recommend_share_times, "100万次");
//        try {
//            helper.setText(R.id.item_recommend_des, Html.fromHtml(mainContent.getIntro().replace("%",
//                    "%%")));
//        }catch (Exception e){
//            helper.setText(R.id.item_recommend_des, mainContent.getIntro());
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            helper.setText(R.id.item_recommend_des, Html.fromHtml(mainContent.getDetail(), FROM_HTML_MODE_LEGACY));
//        } else {
//            helper.setText(R.id.item_recommend_des, Html.fromHtml(mainContent.getDetail()));
//        }
    }

    private String getImageUrl(boolean imageVertical, boolean useCostom,BookModel mainContent) {
        String imageUrl = "";
        if (useCostom) {
            imageUrl = mainContent.getModel_img_url();
            if (TextUtils.isEmpty(imageUrl) || !imageUrl.startsWith("http")) {
                imageUrl = mainContent.getCover();
            }
        }else {
            if (imageVertical) {//竖图
                imageUrl = mainContent.getCover();
            } else {
                imageUrl = mainContent.getCoverl();
            }
        }
        return imageUrl;
    }

}
