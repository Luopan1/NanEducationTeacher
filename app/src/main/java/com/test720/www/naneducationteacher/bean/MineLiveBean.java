package com.test720.www.naneducationteacher.bean;

import java.util.List;

/**
 * Created by wangshuai on 2017/11/9.
 */

public class MineLiveBean {


    /**
     * code : 1
     * data : {"list":[{"head":"Uploads/Img/2018-01-15/5a5c52c2a3050.jpg","name":"穆老师","live_logo":"Uploads/Img/2018-01-15/5a5c533a5ed78.jpg","live_title":"是是是","price":"5.00","is_free":"0","room_mun":"162223551","lid":"18","live_url":"","back_url":"","startime":"1516259243","endtime":"1516259246","livetype":"回放"},{"head":"Uploads/Img/2018-01-15/5a5c52c2a3050.jpg","name":"穆老师","live_logo":"Uploads/Img/2018-01-15/5a5c539a81078.jpg","live_title":"拉拉糊","price":"6.00","is_free":"1","room_mun":"396704230","lid":"19","live_url":"","back_url":"","startime":"1516345749","endtime":"1516691351","livetype":"免费"},{"head":"Uploads/Img/2018-01-15/5a5c52c2a3050.jpg","name":"穆老师","live_logo":"Uploads/Img/2018-01-15/5a5c69b9e624e.jpg","live_title":"价格测试","price":"1000.00","is_free":"0","room_mun":"1648076706","lid":"20","live_url":"","back_url":"","startime":"1517474602","endtime":"1517647411","livetype":"回放"}]}
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
             * head : Uploads/Img/2018-01-15/5a5c52c2a3050.jpg
             * name : 穆老师
             * live_logo : Uploads/Img/2018-01-15/5a5c533a5ed78.jpg
             * live_title : 是是是
             * price : 5.00
             * is_free : 0
             * room_mun : 162223551
             * lid : 18
             * live_url :
             * back_url :
             * startime : 1516259243
             * endtime : 1516259246
             * livetype : 回放
             */

            private String head;
            private String name;
            private String live_logo;
            private String live_title;
            private String price;
            private String is_free;
            private String room_mun;
            private String lid;
            private String live_url;
            private String back_url;
            private String startime;
            private String endtime;
            private String livetype;

            public String getHead() {
                return head;
            }

            public void setHead(String head) {
                this.head = head;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getLive_logo() {
                return live_logo;
            }

            public void setLive_logo(String live_logo) {
                this.live_logo = live_logo;
            }

            public String getLive_title() {
                return live_title;
            }

            public void setLive_title(String live_title) {
                this.live_title = live_title;
            }

            public String getPrice() {
                return price;
            }

            public void setPrice(String price) {
                this.price = price;
            }

            public String getIs_free() {
                return is_free;
            }

            public void setIs_free(String is_free) {
                this.is_free = is_free;
            }

            public String getRoom_mun() {
                return room_mun;
            }

            public void setRoom_mun(String room_mun) {
                this.room_mun = room_mun;
            }

            public String getLid() {
                return lid;
            }

            public void setLid(String lid) {
                this.lid = lid;
            }

            public String getLive_url() {
                return live_url;
            }

            public void setLive_url(String live_url) {
                this.live_url = live_url;
            }

            public String getBack_url() {
                return back_url;
            }

            public void setBack_url(String back_url) {
                this.back_url = back_url;
            }

            public String getStartime() {
                return startime;
            }

            public void setStartime(String startime) {
                this.startime = startime;
            }

            public String getEndtime() {
                return endtime;
            }

            public void setEndtime(String endtime) {
                this.endtime = endtime;
            }

            public String getLivetype() {
                return livetype;
            }

            public void setLivetype(String livetype) {
                this.livetype = livetype;
            }
        }
    }
}
