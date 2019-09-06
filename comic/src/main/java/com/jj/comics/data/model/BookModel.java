package com.jj.comics.data.model;

import com.google.gson.annotations.SerializedName;
import com.jj.comics.common.converter.StringConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;
import java.util.List;

/**
 * 一本书的通用模型
 */
@Entity
public class BookModel implements Serializable {
    private static final long serialVersionUID = -5079543512443325418L;

    /**
     * id : 漫画id
     * cover : 封面竖图
     * title : 漫画名
     * coverl : 详情页封面大图
     * coverl_index : 自定义封面大图
     * cover_index : 自定义封面小图
     * intro : 漫画简介                                                                                                                        和我谈婚论嫁的女友紫儿是个完美的人，她是所有男人梦寐以求的对象，但我卻无法信任她。这时，徬徨的我身边出现了一位同样名为紫儿的女子，她跟我的女友有著相同的名字，就好像二十岁时的紫儿回來了一样…
     * fullflag : 是否完结 0:未完结;1:已完结
     * lastvolume : 最新章节
     * allvisit : 访问量
     * hot_const : 热度
     * reviewnum : 评论数
     * goodnum : 点赞数
     * tag : ["恋爱","都市"]
     * category_id : [25,35] (所属类别，获取猜你喜欢的数据)
     * chapterid : 阅读历史章节id
     * order:阅读历史章节数
     * author:作者
     * yesterday_share:昨日分享
     */

    @Id(autoincrement = true)
    private Long _id;//数据库主键（为了缓存本地阅读记录而新增的字段）

    @SerializedName(value = "id", alternate = {"articleid", "book_id"})
    private long id;
    private String cover;
    @SerializedName(value = "title", alternate = {"name"})
    private String title;
    private String coverl;
    private String coverl_index;
    private String cover_index;
    private String model_img_url;
    private String hot_const;
    private String intro;
    private int fullflag;
    private String lastvolume;
    private int allvisit;
    private int reviewnum;
    private int goodnum;
    private long chapterid;
    @SerializedName(value = "order", alternate = {"read_chapter"})
    private int order;
    @Convert(converter = StringConverter.class, columnType = String.class)
    @SerializedName(value = "tag", alternate = {"category", "category_name"})
    private List<String> tag;

    private String author;
    private long yesterday_share;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getYesterday_share() {
        return yesterday_share;
    }

    public void setYesterday_share(long yesterday_share) {
        this.yesterday_share = yesterday_share;
    }

    //设置以下两个属性不写入表中
    @Transient
    private List<Integer> category_id;
    @Transient
    private boolean is_collect;//该字段只有在本周限免和下周预告页面服务端才会有返回值，别的地方不允许使用

    //以下三个字段为数据库新增字段，临时插入的
    private long userId;//用户id(如果userId=0代表阅读记录没有上传，不等于0表示上传成功)
    private long create_time;//入库时间
    private long update_time;//更新时间

    @Generated(hash = 147605189)
    public BookModel(Long _id, long id, String cover, String title, String coverl, String coverl_index, String cover_index, String model_img_url, String hot_const, String intro, int fullflag, String lastvolume, int allvisit,
            int reviewnum, int goodnum, long chapterid, int order, List<String> tag, String author, long yesterday_share, long userId, long create_time, long update_time) {
        this._id = _id;
        this.id = id;
        this.cover = cover;
        this.title = title;
        this.coverl = coverl;
        this.coverl_index = coverl_index;
        this.cover_index = cover_index;
        this.model_img_url = model_img_url;
        this.hot_const = hot_const;
        this.intro = intro;
        this.fullflag = fullflag;
        this.lastvolume = lastvolume;
        this.allvisit = allvisit;
        this.reviewnum = reviewnum;
        this.goodnum = goodnum;
        this.chapterid = chapterid;
        this.order = order;
        this.tag = tag;
        this.author = author;
        this.yesterday_share = yesterday_share;
        this.userId = userId;
        this.create_time = create_time;
        this.update_time = update_time;
    }

    @Generated(hash = 1421733684)
    public BookModel() {
    }


    public String getModel_img_url() {
        return model_img_url;
    }

    public void setModel_img_url(String model_img_url) {
        this.model_img_url = model_img_url;
    }

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverl() {
        return coverl;
    }

    public void setCoverl(String coverl) {
        this.coverl = coverl;
    }

    public String getCoverl_index() {
        return coverl_index;
    }

    public void setCoverl_index(String coverl_index) {
        this.coverl_index = coverl_index;
    }

    public String getCover_index() {
        return cover_index;
    }

    public void setCover_index(String cover_index) {
        this.cover_index = cover_index;
    }

    public String getHot_const() {
        return hot_const;
    }

    public void setHot_const(String hot_const) {
        this.hot_const = hot_const;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public int getFullflag() {
        return fullflag;
    }

    public void setFullflag(int fullflag) {
        this.fullflag = fullflag;
    }

    public String getLastvolume() {
        return lastvolume;
    }

    public void setLastvolume(String lastvolume) {
        this.lastvolume = lastvolume;
    }

    public int getAllvisit() {
        return allvisit;
    }

    public void setAllvisit(int allvisit) {
        this.allvisit = allvisit;
    }

    public int getReviewnum() {
        return reviewnum;
    }

    public void setReviewnum(int reviewnum) {
        this.reviewnum = reviewnum;
    }

    public int getGoodnum() {
        return goodnum;
    }

    public void setGoodnum(int goodnum) {
        this.goodnum = goodnum;
    }

    public long getChapterid() {
        return chapterid;
    }

    public void setChapterid(long chapterid) {
        this.chapterid = chapterid;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }

    public List<Integer> getCategory_id() {
        return category_id;
    }

    public void setCategory_id(List<Integer> category_id) {
        this.category_id = category_id;
    }

    public boolean isIs_collect() {
        return is_collect;
    }

    public void setIs_collect(boolean is_collect) {
        this.is_collect = is_collect;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(long update_time) {
        this.update_time = update_time;
    }
}
