package com.test720.www.naneducationteacher.bean;

import java.util.List;

/**
 * @author LuoPan on 2018/1/19 16:27.
 */

public class Banner {

    /**
     * code : 1
     * data : {"list":[{"ba_id":"25","ba_img":"Uploads/Img/2018-01-19/5a61abbb36faf.jpg","target_type":"1","target_url":""}]}
     * msg : 获取成功
     */

    private int code;
    private DataBean data;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataBean {
        private List<ListBean> list;

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * ba_id : 25
             * ba_img : Uploads/Img/2018-01-19/5a61abbb36faf.jpg
             * target_type : 1
             * target_url :
             */

            private String ba_id;
            private String ba_img;
            private String target_type;
            private String target_url;

            public String getBa_id() {
                return ba_id;
            }

            public void setBa_id(String ba_id) {
                this.ba_id = ba_id;
            }

            public String getBa_img() {
                return ba_img;
            }

            public void setBa_img(String ba_img) {
                this.ba_img = ba_img;
            }

            public String getTarget_type() {
                return target_type;
            }

            public void setTarget_type(String target_type) {
                this.target_type = target_type;
            }

            public String getTarget_url() {
                return target_url;
            }

            public void setTarget_url(String target_url) {
                this.target_url = target_url;
            }
        }
    }
}
