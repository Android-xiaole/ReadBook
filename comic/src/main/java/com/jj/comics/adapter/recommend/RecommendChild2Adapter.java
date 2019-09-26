package com.jj.comics.adapter.recommend;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.base.imageloader.ILFactory;
import com.jj.comics.R;
import com.jj.comics.data.model.BookModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class RecommendChild2Adapter extends SimpleBaseAdapter<BookModel> {
    private RecommendAdapter.OnClickListener mOnClick;
    protected int mSize;
    protected boolean imageVertical = true;
    private View header;
    private String name;

    public RecommendChild2Adapter(int layoutResId) {
        super(layoutResId);
    }

    public RecommendChild2Adapter(int size, boolean imageVertical, int normalLayoutId, String name) {
        this(normalLayoutId);
        mSize = size;
        this.imageVertical = imageVertical;
        this.name = name;
    }

    public void setOnClick(RecommendAdapter.OnClickListener onClick) {
        this.mOnClick = onClick;
    }

    @Override
    public void setNewData(@Nullable final List<BookModel> data) {
        ArrayList<BookModel> realData = new ArrayList<>();
        for (int i = 0; i < Math.min(data.size(), mSize); i++) {
            BookModel contentListBean = data.get(i);
            if (i == 0) {
                final BookModel mainContent = contentListBean;
                if (header == null) {
                    header = LayoutInflater.from(getRecyclerView().getContext()).inflate(R.layout.comic_item_recommend_head, (ViewGroup) getRecyclerView().getParent(), false);
                    header.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            onEvent(mainContent);
                            if (mOnClick != null)
                                mOnClick.onItemClick(mainContent, name);
                        }
                    });
                    addHeaderView(header);
                }
//                String imageUrl = getImageUrl(false, mainContent);
                String imageUrl = mainContent.getModel_img_url();
                if (!TextUtils.isEmpty(imageUrl)) {
                    ILFactory.getLoader().loadNet(header.<ImageView>findViewById(R.id.item_recommend_img),
                            imageUrl,
                            new RequestOptions().transforms(new CenterCrop()/*, new RoundedCorners(12)*/)
                                    .error(R.drawable.img_loading)
                                    .transforms(new CenterCrop())
                                    .placeholder(R.drawable.img_loading));

                } else {
                    ILFactory.getLoader().loadNet(header.<ImageView>findViewById(R.id.item_recommend_img),
                            mainContent.getCover(),
                            new RequestOptions().transforms(new CenterCrop()/*, new RoundedCorners(12)*/)
                                    .error(R.drawable.img_loading)
                                    .transforms(new CenterCrop())
                                    .placeholder(R.drawable.img_loading));
                }
                header.<TextView>findViewById(R.id.item_recommend_name).setText(mainContent.getTitle());
                header.<TextView>findViewById(R.id.item_recommend_desc).setText(mainContent.getIntro());
                header.<TextView>findViewById(R.id.item_recommend_author).setText(mainContent.getAuthor());
                header.<TextView>findViewById(R.id.item_recommend_status).setText(mainContent.getAuthor());
                if (contentListBean.getFullflag() == 0) {
                    header.<TextView>findViewById(R.id.item_recommend_status).setText("连载中");
                } else {
                    header.<TextView>findViewById(R.id.item_recommend_status).setText("已完结");
                }
                //旧接口返回int数字的格式化代码
//                header.<TextView>findViewById(R.id.comic_recommend_hot).setText(Utils.convertUnit(Integer.parseInt(mainContent.getHot_const())));
            } else {
                realData.add(contentListBean);
            }
        }
        super.setNewData(realData);
    }

    @Override
    protected void convert(BaseViewHolder helper, BookModel mainContent) {
        String imageUrl = getImageUrl(imageVertical, mainContent);
        if (!TextUtils.isEmpty(imageUrl)) {
            ILFactory.getLoader().loadNet(helper.<ImageView>getView(R.id.item_recommend_img),
                    imageUrl,
                    new RequestOptions().transforms(new CenterCrop())
                            .error(R.drawable.img_loading)
                            .placeholder(R.drawable.img_loading));

        } else {
            ILFactory.getLoader().loadResource(helper.<ImageView>getView(R.id.item_recommend_img),
                    R.drawable.img_loading,
                    new RequestOptions().transforms(new CenterCrop()));
        }
        helper.setText(R.id.item_recommend_name, mainContent.getTitle());
        if (helper.getLayoutPosition() == getData().size()) {
            helper.getView(R.id.view_fgt).setVisibility(View.GONE);
        } else {
            helper.getView(R.id.view_fgt).setVisibility(View.VISIBLE);
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            helper.setText(R.id.item_recommend_des, Html.fromHtml(mainContent.getDetail(), FROM_HTML_MODE_LEGACY));
//        } else {
//            helper.setText(R.id.item_recommend_des, Html.fromHtml(mainContent.getDetail()));
//        }
    }

    private String getImageUrl(boolean imageVertical, BookModel mainContent) {
        String imageUrl = "";
        if (imageVertical) {//竖图
            imageUrl = mainContent.getModel_img_url();
            if (TextUtils.isEmpty(imageUrl)) {
                imageUrl = mainContent.getCover();
            }
        } else {
            imageUrl = mainContent.getCoverl();
        }
        return imageUrl;
    }
}
