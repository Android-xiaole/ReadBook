package com.jj.comics.data.model;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.jj.base.mvp.IModel;
import com.jj.base.net.NetError;

import java.util.List;

public class SignTaskResponse extends ResponseModel {

    /**
     * code : 200
     * message : 请求成功
     * data : {"qiandao_list":[{"title":"第一天","value":"10"},{"title":"第二天","value":"20"},{"title":"第三天","value":"30"},{"title":"第四天","value":"40"},{"title":"第五天","value":"50"},{"title":"第六天","value":"60"},{"title":"第七天","value":"70"}],"checkrecord":1,"today_qiandao_reward":20,"take":"0","task_list":[{"title":"VIP福利","type":"vip","list":[{"id":0,"type":"active_vip","title":"VIP/SVIP每天金币+50","reward":50,"is_take":0}]},{"title":"新手任务","type":"new","list":[{"id":13783013,"type":"good_cartoon","reward":"25.00","is_take":2,"title":"点赞漫画"},{"id":0,"type":"first_collect","title":"收藏漫画","reward":50,"is_take":0},{"id":0,"type":"first_comment","title":"第一次评论","reward":50,"is_take":0},{"id":0,"type":"first_bonus","title":"第一次打赏","reward":688,"is_take":0},{"id":0,"type":"finish_all_task","title":"完成所有新手任务","reward":500,"is_take":0}]},{"title":"每日任务","type":"everyday","list":[{"id":0,"type":"check","title":"签到有礼","reward":10,"is_take":0},{"id":0,"type":"share","title":"分享漫画","reward":50,"is_take":0},{"id":0,"type":"invite","title":"邀请新用户","reward":200,"is_take":0},{"id":0,"type":"today_comment","title":"每日评论","reward":30,"is_take":0},{"id":0,"type":"read","title":"阅读一本漫画","reward":20,"is_take":0}]}]}
     */
    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean implements IModel, MultiItemEntity {
        /**
         * qiandao_list : [{"title":"第一天","value":"10"},{"title":"第二天","value":"20"},{"title":"第三天","value":"30"},{"title":"第四天","value":"40"},{"title":"第五天","value":"50"},{"title":"第六天","value":"60"},{"title":"第七天","value":"70"}]
         * checkrecord : 1
         * today_qiandao_reward : 20
         * take : 0
         * task_list : [{"title":"VIP福利","type":"vip","list":[{"id":0,"type":"active_vip","title":"VIP/SVIP每天金币+50","reward":50,"is_take":0}]},{"title":"新手任务","type":"new","list":[{"id":13783013,"type":"good_cartoon","reward":"25.00","is_take":2,"title":"点赞漫画"},{"id":0,"type":"first_collect","title":"收藏漫画","reward":50,"is_take":0},{"id":0,"type":"first_comment","title":"第一次评论","reward":50,"is_take":0},{"id":0,"type":"first_bonus","title":"第一次打赏","reward":688,"is_take":0},{"id":0,"type":"finish_all_task","title":"完成所有新手任务","reward":500,"is_take":0}]},{"title":"每日任务","type":"everyday","list":[{"id":0,"type":"check","title":"签到有礼","reward":10,"is_take":0},{"id":0,"type":"share","title":"分享漫画","reward":50,"is_take":0},{"id":0,"type":"invite","title":"邀请新用户","reward":200,"is_take":0},{"id":0,"type":"today_comment","title":"每日评论","reward":30,"is_take":0},{"id":0,"type":"read","title":"阅读一本漫画","reward":20,"is_take":0}]}]
         */

        private int checkrecord;
        private int today_qiandao_reward;
        private String take;
        private List<QiandaoListBean> qiandao_list;
        private List<TaskListBean> task_list;

        public int getCheckrecord() {
            return checkrecord;
        }

        public void setCheckrecord(int checkrecord) {
            this.checkrecord = checkrecord;
        }

        public int getToday_qiandao_reward() {
            return today_qiandao_reward;
        }

        public void setToday_qiandao_reward(int today_qiandao_reward) {
            this.today_qiandao_reward = today_qiandao_reward;
        }

        public String getTake() {
            return take;
        }

        public void setTake(String take) {
            this.take = take;
        }

        public List<QiandaoListBean> getQiandao_list() {
            return qiandao_list;
        }

        public void setQiandao_list(List<QiandaoListBean> qiandao_list) {
            this.qiandao_list = qiandao_list;
        }

        public List<TaskListBean> getTask_list() {
            return task_list;
        }

        public void setTask_list(List<TaskListBean> task_list) {
            this.task_list = task_list;
        }

        @Override
        public int getItemType() {
            return 1;
        }

        @Override
        public NetError error() {
            return null;
        }

        public static class QiandaoListBean implements Comparable<QiandaoListBean>, MultiItemEntity {
            /**
             * title : 第一天
             * value : 10
             */

            private String title;
            private String value;
            private boolean isSign;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public boolean isSign() {
                return isSign;
            }

            public void setSign(boolean sign) {
                isSign = sign;
            }

            @Override
            public int getItemType() {
                return 0;
            }

            @Override
            public int compareTo(QiandaoListBean o) {
                return 0;
            }
        }

        public static class TaskListBean extends AbstractExpandableItem<TaskListBean.ListBean> implements MultiItemEntity {
            /**
             * title : VIP福利
             * type : vip
             * list : [{"id":0,"type":"active_vip","title":"VIP/SVIP每天金币+50","reward":50,"is_take":0}]
             */

            private String title;
            private String type;
            private List<ListBean> list;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public List<ListBean> getList() {
                return list;
            }

            public void setList(List<ListBean> list) {
                this.list = list;
            }

            @Override
            public int getLevel() {
                return 0;
            }

            @Override
            public int getItemType() {
                return 0;
            }

            public static class ListBean implements MultiItemEntity {
                /**
                 * id : 0
                 * type : active_vip
                 * title : VIP/SVIP每天金币+50
                 * reward : 50
                 * is_take : 0
                 */

                private long id;
                private String type;
                private String title;
                private int reward;
                private int is_take;
                private String p_type;

                public String getP_type() {
                    return p_type;
                }

                public void setP_type(String p_type) {
                    this.p_type = p_type;
                }

                public long getId() {
                    return id;
                }

                public void setId(long id) {
                    this.id = id;
                }

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public String getTitle() {
                    return title;
                }

                public void setTitle(String title) {
                    this.title = title;
                }

                public int getReward() {
                    return reward;
                }

                public void setReward(int reward) {
                    this.reward = reward;
                }

                public int getIs_take() {
                    return is_take;
                }

                public void setIs_take(int is_take) {
                    this.is_take = is_take;
                }

                @Override
                public int getItemType() {
                    return 1;
                }
            }
        }
    }
}
