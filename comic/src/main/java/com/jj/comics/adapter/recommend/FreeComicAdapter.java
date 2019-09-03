package com.jj.comics.adapter.recommend;

import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.base.imageloader.ILFactory;
import com.jj.base.utils.Utils;
import com.jj.comics.R;
import com.jj.comics.data.model.BookModel;

import java.util.List;
import java.util.UnknownFormatConversionException;

public class FreeComicAdapter extends SimpleBaseAdapter<BookModel> {
    public FreeComicAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, BookModel item) {
        //加载图片封面
        ILFactory.getLoader().loadNet(helper.<ImageView>getView(R.id.item_recently_img), item.getCoverl(),
                new RequestOptions()
                        .error(R.drawable.img_loading)
                        .centerCrop()
                        .placeholder(R.drawable.img_loading));
        //设置标题
        helper.<TextView>getView(R.id.item_recently_name).setText(item.getTitle());
        //设置连载多少话
        if (item.getFullflag() == 1){//已完结
            helper.<TextView>getView(R.id.item_recently_update).setText("已完结");
        }else{
            helper.<TextView>getView(R.id.item_recently_update).setText(String.format(mContext.getString(R.string.comic_update), item.getLastvolume()));
        }
        //设置漫画描述
        TextView mTextDes = helper.getView(R.id.tv_des);
        try {
            mTextDes.setText(Html.fromHtml(item.getIntro().replace("%", "%%")));
        } catch (UnknownFormatConversionException e) {
            e.printStackTrace();
            mTextDes.setText(String.format(item.getIntro()));
        }
        //设置漫画标签
        LinearLayout lin_tag = helper.getView(R.id.lin_tag);
        List<String> tags = item.getTag();
        for (int i = 0; i < tags.size(); i++) {
            if (i == 2) {//控制最多显示两个标签
                break;
            }
            fillTagView(lin_tag,i, tags.get(i));
        }

        //设置人气
        helper.setText(R.id.tv_fireCount,item.getHot_const());
        //设置收藏状态
        if (item.isIs_collect()){
            helper.setText(R.id.tv_collectionStatus,"已收藏");
            helper.getView(R.id.iv_collectionIcon).setVisibility(View.GONE);
        }else{
            helper.setText(R.id.tv_collectionStatus,"收藏");
            helper.getView(R.id.iv_collectionIcon).setVisibility(View.VISIBLE);
        }
        //设置收藏按钮点击事件
        helper.addOnClickListener(R.id.lin_collection);
        //控制分割线的显示
        View view_fgx = helper.getView(R.id.view_fgx);
        if (helper.getLayoutPosition() == getData().size()-1){//最后一项
            view_fgx.setVisibility(View.GONE);
        }else{
            view_fgx.setVisibility(View.VISIBLE);
        }
    }

    public void fillTagView(LinearLayout mTagContainer,int i, String tag) {
        View view = mTagContainer.getChildAt(i);
        if (view != null && view instanceof TextView) {
            ((TextView) view).setText(tag);
        } else {
            TextView textView = new TextView(mContext);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.rightMargin = Utils.dip2px(mContext, 12);
            textView.setLayoutParams(layoutParams);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(mContext.getResources().getColor(R.color.comic_999999));
            textView.setBackgroundResource(R.drawable.comic_shape_freecomic_tag_bg);
            textView.setText(tag);
            mTagContainer.addView(textView);
        }
    }

}
