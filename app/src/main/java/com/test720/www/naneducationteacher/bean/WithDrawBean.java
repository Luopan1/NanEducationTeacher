package com.test720.www.naneducationteacher.bean;

import java.util.List;

/**
 * Created by wangshuai on 2017/11/10.
 */

public class WithDrawBean {
    /**
     * code : 1
     * data : {"list":[{"money":"200","time":"2017.11.08 11:22:46","cash_type":"冻结中"},{"money":"300","time":"2017.11.08 11:23:22","cash_type":"冻结中"}]}
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
             * money : 200
             * time : 2017.11.08 11:22:46
             * cash_type : 冻结中
             */

            private String money;
            private String time;
            private String cash_type;

            public String getMoney() {
                return money;
            }

            public void setMoney(String money) {
                this.money = money;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public String getCash_type() {
                return cash_type;
            }

            public void setCash_type(String cash_type) {
                this.cash_type = cash_type;
            }
        }
    }
}
