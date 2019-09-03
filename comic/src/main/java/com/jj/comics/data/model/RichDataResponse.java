package com.jj.comics.data.model;

import java.util.List;

public class RichDataResponse extends ResponseModel {

    /**
     * data : {"total_num":52,"data":[{"username":"云峰","actvalue":520,"avatar":"http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTKuhorb6NfogzHMfn4OhaLOYOF9zib4cONWdMQ8ic0rUpV0JLlVrHrAiax1KxX4IwPGgBy0L57RsfMxw/132","id":20917},{"username":"云峰","actvalue":366,"avatar":"http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTKuhorb6NfogzHMfn4OhaLOYOF9zib4cONWdMQ8ic0rUpV0JLlVrHrAiax1KxX4IwPGgBy0L57RsfMxw/132","id":20916},{"username":"云峰","actvalue":366,"avatar":"http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTKuhorb6NfogzHMfn4OhaLOYOF9zib4cONWdMQ8ic0rUpV0JLlVrHrAiax1KxX4IwPGgBy0L57RsfMxw/132","id":20915},{"username":"云峰","actvalue":580,"avatar":"http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTKuhorb6NfogzHMfn4OhaLOYOF9zib4cONWdMQ8ic0rUpV0JLlVrHrAiax1KxX4IwPGgBy0L57RsfMxw/132","id":20914},{"username":"云峰","actvalue":520,"avatar":"http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTKuhorb6NfogzHMfn4OhaLOYOF9zib4cONWdMQ8ic0rUpV0JLlVrHrAiax1KxX4IwPGgBy0L57RsfMxw/132","id":20913},{"username":"张三","actvalue":520,"avatar":null,"id":20912},{"username":"张三","actvalue":366,"avatar":null,"id":20911},{"username":"张三","actvalue":520,"avatar":null,"id":20910},{"username":"张三","actvalue":520,"avatar":null,"id":20909},{"username":"张三","actvalue":520,"avatar":null,"id":20908},{"username":"张三","actvalue":520,"avatar":null,"id":20907},{"username":"张三","actvalue":520,"avatar":null,"id":20906},{"username":"张三","actvalue":520,"avatar":null,"id":20905},{"username":"张三","actvalue":520,"avatar":null,"id":20904},{"username":"张三","actvalue":520,"avatar":null,"id":20903},{"username":"张三","actvalue":520,"avatar":null,"id":20902},{"username":"张三","actvalue":520,"avatar":null,"id":20901},{"username":"张三","actvalue":366,"avatar":null,"id":20900},{"username":"张三","actvalue":366,"avatar":null,"id":20899},{"username":"张三","actvalue":366,"avatar":null,"id":20898}]}
     */

    private DataBeanX data;

    public DataBeanX getData() {
        return data;
    }

    public void setData(DataBeanX data) {
        this.data = data;
    }

    public static class DataBeanX {
        /**
         * total_num : 52
         * data : [{"username":"云峰","actvalue":520,"avatar":"http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTKuhorb6NfogzHMfn4OhaLOYOF9zib4cONWdMQ8ic0rUpV0JLlVrHrAiax1KxX4IwPGgBy0L57RsfMxw/132","id":20917},{"username":"云峰","actvalue":366,"avatar":"http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTKuhorb6NfogzHMfn4OhaLOYOF9zib4cONWdMQ8ic0rUpV0JLlVrHrAiax1KxX4IwPGgBy0L57RsfMxw/132","id":20916},{"username":"云峰","actvalue":366,"avatar":"http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTKuhorb6NfogzHMfn4OhaLOYOF9zib4cONWdMQ8ic0rUpV0JLlVrHrAiax1KxX4IwPGgBy0L57RsfMxw/132","id":20915},{"username":"云峰","actvalue":580,"avatar":"http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTKuhorb6NfogzHMfn4OhaLOYOF9zib4cONWdMQ8ic0rUpV0JLlVrHrAiax1KxX4IwPGgBy0L57RsfMxw/132","id":20914},{"username":"云峰","actvalue":520,"avatar":"http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTKuhorb6NfogzHMfn4OhaLOYOF9zib4cONWdMQ8ic0rUpV0JLlVrHrAiax1KxX4IwPGgBy0L57RsfMxw/132","id":20913},{"username":"张三","actvalue":520,"avatar":null,"id":20912},{"username":"张三","actvalue":366,"avatar":null,"id":20911},{"username":"张三","actvalue":520,"avatar":null,"id":20910},{"username":"张三","actvalue":520,"avatar":null,"id":20909},{"username":"张三","actvalue":520,"avatar":null,"id":20908},{"username":"张三","actvalue":520,"avatar":null,"id":20907},{"username":"张三","actvalue":520,"avatar":null,"id":20906},{"username":"张三","actvalue":520,"avatar":null,"id":20905},{"username":"张三","actvalue":520,"avatar":null,"id":20904},{"username":"张三","actvalue":520,"avatar":null,"id":20903},{"username":"张三","actvalue":520,"avatar":null,"id":20902},{"username":"张三","actvalue":520,"avatar":null,"id":20901},{"username":"张三","actvalue":366,"avatar":null,"id":20900},{"username":"张三","actvalue":366,"avatar":null,"id":20899},{"username":"张三","actvalue":366,"avatar":null,"id":20898}]
         */

        private int total_num;
        private List<RichManModel> data;

        public int getTotal_num() {
            return total_num;
        }

        public void setTotal_num(int total_num) {
            this.total_num = total_num;
        }

        public List<RichManModel> getData() {
            return data;
        }

        public void setData(List<RichManModel> data) {
            this.data = data;
        }

    }
}
