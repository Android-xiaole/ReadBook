package com.jj.comics.adapter.detail;

import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.base.imageloader.ILFactory;
import com.jj.comics.R;
import com.jj.comics.data.model.BookModel;

/**
 * 公共的推荐列表
 * 比如：猜你喜欢、大家都在看、为你推荐等...
 */
public class CommonRecommendAdapter extends SimpleBaseAdapter<BookModel> {

    private RequestOptions options;

    public CommonRecommendAdapter(int layoutResId) {
        super(layoutResId);
        options = new RequestOptions().transforms(new CenterCrop())
                .error(R.drawable.img_loading)
                .placeholder(R.drawable.img_loading);
    }

    @Override
    protected void convert(BaseViewHolder helper, BookModel item) {
        ILFactory.getLoader().loadNet((ImageView) helper.getView(R.id.iv_icon),item.getCover(),options);
        helper.setText(R.id.tv_title,item.getTitle());
//        View view_space_left = helper.getView(R.id.view_space_left);
//        if ((helper.getLayoutPosition()+1-getHeaderLayoutCount())%3 == 1){
//            view_space_left.setVisibility(View.VISIBLE);
//        }else{
//            view_space_left.setVisibility(View.GONE);
//        }
//        View view_space_right = helper.getView(R.id.view_space_right);
//        ViewGroup.LayoutParams lp = view_space_right.getLayoutParams();
//        if ((helper.getLayoutPosition()+1-getHeaderLayoutCount())%3 == 0){
//            lp.width = Utils.dip2px(mContext,15);
//            view_space_right.setLayoutParams(lp);
//        }else{
//            lp.width = Utils.dip2px(mContext,21);
//            view_space_right.setLayoutParams(lp);
//        }
    }
}
