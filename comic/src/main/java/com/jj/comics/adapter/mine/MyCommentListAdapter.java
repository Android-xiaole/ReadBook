package com.jj.comics.adapter.mine;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.comics.R;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.model.CommentListResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 漫画详情里面的评论列表
 */
public class MyCommentListAdapter extends BaseQuickAdapter<CommentListResponse.DataBeanX.DataBean, BaseViewHolder> {

    public MyCommentListAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, CommentListResponse.DataBeanX.DataBean item) {
//        String createTime = item.getCreateTime();
//        Date parseTime = null;
//        String format = "";
//        try {
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DateFormat.YMDHM);
//            parseTime = simpleDateFormat.parse(createTime);
//            format = simpleDateFormat.format(parseTime);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        String title = item.getContent().getTitle();
//        helper.setText(R.id.tv_userinfo_mycomments_desc, item.getContent())
//                .setText(R.id.tv_userinfo_mycomments_source, String.format(mContext.getString(R.string.comic_user_comment_source),
//                        TextUtils.isEmpty(title) ? mContext.getString(R.string.comic_unkown) : title))
//                .setText(R.id.tv_userinfo_mycomments_time, format);

//        ImageView btnDelete = helper.getView();
        helper.addOnClickListener(R.id.btn_userinfo_mycomments_delete);
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
