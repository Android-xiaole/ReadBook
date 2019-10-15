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
    private String lastvolume;//最新章节，暂时这个字段后台不给了，所以不要使用！
    private String lastvolume_name;
    private int reviewnum;
    private int goodnum;
    private long chapterid;//阅读历史章节id
    private String chaptername;//阅读历史章节名称
    @SerializedName(value = "order", alternate = {"read_chapter"})
    private int order;
    @Convert(converter = StringConverter.class, columnType = String.class)
    @SerializedName(value = "tag", alternate = {"category", "category_name"})
    private List<String> tag;

    private String keywords;//关键字
    private String author;//作者
    private String yesterday_share;
    private String allvisit;//阅读人数
    private String total_size;//总字数
    private String total_share;//总的分享次数
    private int batchbuy;//''购买方式：1-章节售卖 2-整本售卖
    private int batchprice;//整本购买的价格
    private int has_batch_buy;//是否已全本购买1是2否
    private int update_chapter_time;//章节目录更新时间（可以用来判断是否需要去刷新章节目录缓存）
    private String first_commission_rate;//徒弟上供率
    private String second_commission_rate;//徒孙上供率
    private String share_price;//每个字的单价

    public String getFirst_commission_rate() {
        return first_commission_rate;
    }

    public void setFirst_commission_rate(String first_commission_rate) {
        this.first_commission_rate = first_commission_rate;
    }

    public String getSecond_commission_rate() {
        return second_commission_rate;
    }

    public void setSecond_commission_rate(String second_commission_rate) {
        this.second_commission_rate = second_commission_rate;
    }

    public String getShare_price() {
        return share_price;
    }

    public void setShare_price(String share_price) {
        this.share_price = share_price;
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

    @Generated(hash = 2080059058)
    public BookModel(Long _id, long id, String cover, String title, String coverl, String coverl_index, String cover_index, String model_img_url, String hot_const, String intro, int fullflag, String lastvolume, String lastvolume_name,
            int reviewnum, int goodnum, long chapterid, String chaptername, int order, List<String> tag, String keywords, String author, String yesterday_share, String allvisit, String total_size, String total_share, int batchbuy,
            int batchprice, int has_batch_buy, int update_chapter_time, String first_commission_rate, String second_commission_rate, String share_price, long userId, long create_time, long update_time) {
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
        this.lastvolume_name = lastvolume_name;
        this.reviewnum = reviewnum;
        this.goodnum = goodnum;
        this.chapterid = chapterid;
        this.chaptername = chaptername;
        this.order = order;
        this.tag = tag;
        this.keywords = keywords;
        this.author = author;
        this.yesterday_share = yesterday_share;
        this.allvisit = allvisit;
        this.total_size = total_size;
        this.total_share = total_share;
        this.batchbuy = batchbuy;
        this.batchprice = batchprice;
        this.has_batch_buy = has_batch_buy;
        this.update_chapter_time = update_chapter_time;
        this.first_commission_rate = first_commission_rate;
        this.second_commission_rate = second_commission_rate;
        this.share_price = share_price;
        this.userId = userId;
        this.create_time = create_time;
        this.update_time = update_time;
    }

    @Generated(hash = 1421733684)
    public BookModel() {
    }



    

    public String getChaptername() {
        return chaptername;
    }

    public void setChaptername(String chaptername) {
        this.chaptername = chaptername;
    }

    public String getLastvolume_name() {
        return lastvolume_name;
    }

    public void setLastvolume_name(String lastvolume_name) {
        this.lastvolume_name = lastvolume_name;
    }

    public int getHas_batch_buy() {
        return has_batch_buy;
    }

    public void setHas_batch_buy(int has_batch_buy) {
        this.has_batch_buy = has_batch_buy;
    }

    public String getYesterday_share() {
        return yesterday_share;
    }

    public void setYesterday_share(String yesterday_share) {
        this.yesterday_share = yesterday_share;
    }

    public int getBatchbuy() {
        return batchbuy;
    }

    public void setBatchbuy(int batchbuy) {
        this.batchbuy = batchbuy;
    }

    public int getBatchprice() {
        return batchprice;
    }

    public void setBatchprice(int batchprice) {
        this.batchprice = batchprice;
    }

    public String getTotal_share() {
        return total_share;
    }

    public void setTotal_share(String total_share) {
        this.total_share = total_share;
    }

    public String getAllvisit() {
        return allvisit;
    }

    public void setAllvisit(String allvisit) {
        this.allvisit = allvisit;
    }

    public String getTotalSize() {
        return total_size;
    }

    public void setTotalSize(String size) {
        this.total_size = size;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public int getUpdate_chapter_time() {
        return this.update_chapter_time;
    }

    public void setUpdate_chapter_time(int update_chapter_time) {
        this.update_chapter_time = update_chapter_time;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getTotal_size() {
        return this.total_size;
    }

    public void setTotal_size(String total_size) {
        this.total_size = total_size;
    }
}
