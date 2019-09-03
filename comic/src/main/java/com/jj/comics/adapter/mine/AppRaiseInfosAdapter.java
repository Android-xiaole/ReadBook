package com.jj.comics.adapter.mine;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 * 漫画详情里面的评论列表
 */
public class AppRaiseInfosAdapter extends BaseQuickAdapter<Object,BaseViewHolder> {

    public AppRaiseInfosAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, Object item) {
//        String createTime = item.getCreateTime();
//        Date parseTime = null;
//        String format = "";
//        try {
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//            parseTime = simpleDateFormat.parse(createTime);
//            format = simpleDateFormat.format(parseTime);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        helper.setText(R.id.tv_title_appraise,item.getContentListBean().getMainContent().getTitle1());
//
//        RequestOptions options = new RequestOptions()
//                .error(R.drawable.img_loading)
//                .placeholder(R.drawable.img_loading)
//                .transforms(new CenterCrop(), new RoundedCorners(10));
//        ILFactory.getLoader().loadNet(helper.<ImageView>getView(R.id.iv_cover_appraise),
//                item.getContentListBean().getMainContent().getImageUrl(false),
//                options);

//        ImageView btnDelete = helper.getView();
//        helper.addOnClickListener(R.id.btn_userinfo_mycomments_delete);
//        try{
//            helper.setText(R.id.tv_userinfo_mycomments_time, DateHelper.formatDate(createTime.substring(0, createTime.length() - 2)));
//        }catch (Exception e){//这里抓取一下异常，防止后台日期格式出错导致程序奔溃
//            helper.setText(R.id.tv_userinfo_mycomments_time,createTime.substring(0, createTime.length() - 2));
//        }

//        if (helper.getAdapterPosition() == getData().size()-1){//如果是最后一条数据，就将灰色的分割线隐藏掉
//            helper.getView(R.id.view_fgx).setVisibility(View.INVISIBLE);
//        }else{
//            helper.getView(R.id.view_fgx).setVisibility(View.VISIBLE);
//        }
    }

}
