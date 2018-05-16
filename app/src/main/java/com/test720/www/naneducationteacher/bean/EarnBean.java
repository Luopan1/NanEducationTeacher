package com.test720.www.naneducationteacher.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wangshuai on 2017/11/10.
 */

public class EarnBean {
    /**
     * code : 1
     * data : {"list":[{"count":2,"money":161.76,"name":"阿莎(直播)","time":"2017.11.08 11:26:23","userlist":[{"head":"Uploads/Img/2017-10-30/59f6dacd5d31e.jpg","name":"爸爸","price":"80.88","time":"2017.11.08 11:26:23"},{"head":"Uploads/Img/2017-10-30/59f6dacd5d31e.jpg","name":"2147483647","price":"80.88","time":"2017.11.08 11:26:23"}]},{"count":1,"money":80.88,"name":"阿莎(录播)","time":"2017.11.08 11:26:23","userlist":[{"head":"Uploads/Img/2017-10-30/59f6dacd5d31e.jpg","name":"2147483647","price":"80.88","time":"2017.11.08 11:26:23"}]}]}
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
             * count : 2
             * money : 161.76
             * name : 阿莎(直播)
             * time : 2017.11.08 11:26:23
             * userlist : [{"head":"Uploads/Img/2017-10-30/59f6dacd5d31e.jpg","name":"爸爸","price":"80.88","time":"2017.11.08 11:26:23"},{"head":"Uploads/Img/2017-10-30/59f6dacd5d31e.jpg","name":"2147483647","price":"80.88","time":"2017.11.08 11:26:23"}]
             */

            private String count;

            public String getCount() {
                return count;
            }

            public void setCount(String count) {
                this.count = count;
            }

            private double money;
            private String name;
            private String time;
            private List<UserlistBean> userlist;


            public double getMoney() {
                return money;
            }

            public void setMoney(double money) {
                this.money = money;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public List<UserlistBean> getUserlist() {
                return userlist;
            }

            public void setUserlist(List<UserlistBean> userlist) {
                this.userlist = userlist;
            }

            public static class UserlistBean implements Serializable{
                /**
                 * head : Uploads/Img/2017-10-30/59f6dacd5d31e.jpg
                 * name : 爸爸
                 * price : 80.88
                 * time : 2017.11.08 11:26:23
                 */

                private String head;
                private String name;
                private String price;
                private String time;

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

                public String getPrice() {
                    return price;
                }

                public void setPrice(String price) {
                    this.price = price;
                }

                public String getTime() {
                    return time;
                }

                public void setTime(String time) {
                    this.time = time;
                }
            }
        }
    }
}
