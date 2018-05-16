package com.test720.www.naneducationteacher.bean;

import java.util.List;

/**
 * Created by wangshuai on 2017/11/9.
 */

public class CreateBroadBean {

    /**
     * code : 1
     * data : {"list":[{"tid":"37","name":"语文","zid":[{"tid":"40","name":"一年级"},{"tid":"41","name":"二年级"},{"tid":"42","name":"三年级"},{"tid":"43","name":"四年级"},{"tid":"44","name":"五年级"},{"tid":"45","name":"六年级"},{"tid":"46","name":"七年级"},{"tid":"47","name":"八年级"},{"tid":"48","name":"九年级"}]},{"tid":"38","name":"数学","zid":[{"tid":"49","name":"一年级"},{"tid":"50","name":"二年级"}]},{"tid":"39","name":"英语","zid":[]},{"tid":"51","name":"钢琴","zid":[{"tid":"53","name":"初级"},{"tid":"54","name":"中级"},{"tid":"55","name":"高级"}]},{"tid":"52","name":"小提琴","zid":[{"tid":"56","name":"初级"}]},{"tid":"58","name":"测试NBJS-1","zid":[{"tid":"59","name":"测试NBJS-1-1"}]}]}
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
             * tid : 37
             * name : 语文
             * zid : [{"tid":"40","name":"一年级"},{"tid":"41","name":"二年级"},{"tid":"42","name":"三年级"},{"tid":"43","name":"四年级"},{"tid":"44","name":"五年级"},{"tid":"45","name":"六年级"},{"tid":"46","name":"七年级"},{"tid":"47","name":"八年级"},{"tid":"48","name":"九年级"}]
             */

            private String tid;
            private String name;
            private List<ZidBean> zid;

            public String getTid() {
                return tid;
            }

            public void setTid(String tid) {
                this.tid = tid;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public List<ZidBean> getZid() {
                return zid;
            }

            public void setZid(List<ZidBean> zid) {
                this.zid = zid;
            }

            public static class ZidBean {
                /**
                 * tid : 40
                 * name : 一年级
                 */

                private String tid;
                private String name;

                public String getTid() {
                    return tid;
                }

                public void setTid(String tid) {
                    this.tid = tid;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }
            }
        }
    }
}
