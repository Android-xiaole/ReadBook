package com.jj.comics.adapter.detail;


import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.base.imageloader.ILFactory;
import com.jj.comics.R;
import com.jj.comics.data.model.BookCatalogModel;

public class CatalogAdapter extends SimpleBaseAdapter<BookCatalogModel> {

    private String defaultImgUrl;
    private RequestOptions requestOptions;

    public CatalogAdapter(int layoutResId, String defaultImgUrl) {
        super(layoutResId);
        this.defaultImgUrl = defaultImgUrl;
        requestOptions = new RequestOptions()
                .transforms(new CenterCrop(), new RoundedCorners(10))
                .placeholder(R.drawable.img_loading)
                .error(R.drawable.img_loading);
    }

    @Override
    protected void convert(BaseViewHolder helper, BookCatalogModel item) {
        if (item.getChaptercover() == null || item.getChaptercover().trim().length() == 0) {
            ILFactory.getLoader().loadNet((ImageView) helper.getView(R.id.iv_icon), defaultImgUrl,requestOptions);
        } else {
            ILFactory.getLoader().loadNet((ImageView) helper.getView(R.id.iv_icon), item.getChaptercover(),requestOptions);
        }
        helper.setText(R.id.tv_num, "第"+item.getChapterorder()+"话")
                .setText(R.id.tv_title, item.getChaptername());
        //设置更新时间显示
        if(item.getCreate_time()!=null&&item.getCreate_time().length()>=10){
            try {
                helper.setText(R.id.tv_updateTime, item.getCreate_time().substring(0,10));
            }catch (Exception e){
                helper.setText(R.id.tv_updateTime, item.getCreate_time());
            }
        }else{
            helper.setText(R.id.tv_updateTime, item.getCreate_time());
        }
        helper.setVisible(R.id.iv_isPaid, item.getIsvip()==1);
    }

//    public void setReverseLayout() {
//        Collections.reverse(getData());
//        notifyDataSetChanged();
//    }
}
