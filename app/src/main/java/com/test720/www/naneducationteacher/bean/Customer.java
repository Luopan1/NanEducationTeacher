package com.test720.www.naneducationteacher.bean;

import java.util.List;

/**
 * @author LuoPan on 2018/1/22 15:13.
 */

public class Customer {

    /**
     * code : 1
     * data : {"agentphone":"13300000001","visephone":"18990899162","quesList":[{"question":"报名缴费后是否可以退款？","answer":"开课前1小时可以退"},{"question":"发票如何获取？","answer":"公司获取"},{"question":"客服工作时间?","answer":"每周一到周六10:00-22:00"},{"question":"问答测试大家好大家好大就的哈吉顺达时间快递假的很骄傲开始大健康和大家卡的哈克斯净化大师鸡块好大时间快递啊哈","answer":"答案杀菌的挥洒建华大街啊阿迪和吉安市大开奖号爱好打击实打实鸡块好大建华大街按开始回答教师客户达几十块和大家都寒暑假啊客户"},{"question":"粉底刷三的","answer":"递四方速递"},{"question":"非师范生","answer":"水电费水电费是的"},{"question":"发顺丰算得上是","answer":"递四方速递防守打法是"}],"agentName":"邹榆江","bossName":"杨总"}
     * msg : 查询成功
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
        /**
         * agentphone : 13300000001
         * visephone : 18990899162
         * quesList : [{"question":"报名缴费后是否可以退款？","answer":"开课前1小时可以退"},{"question":"发票如何获取？","answer":"公司获取"},{"question":"客服工作时间?","answer":"每周一到周六10:00-22:00"},{"question":"问答测试大家好大家好大就的哈吉顺达时间快递假的很骄傲开始大健康和大家卡的哈克斯净化大师鸡块好大时间快递啊哈","answer":"答案杀菌的挥洒建华大街啊阿迪和吉安市大开奖号爱好打击实打实鸡块好大建华大街按开始回答教师客户达几十块和大家都寒暑假啊客户"},{"question":"粉底刷三的","answer":"递四方速递"},{"question":"非师范生","answer":"水电费水电费是的"},{"question":"发顺丰算得上是","answer":"递四方速递防守打法是"}]
         * agentName : 邹榆江
         * bossName : 杨总
         */

        private String agentphone;
        private String visephone;
        private String agentName;
        private String bossName;
        private List<QuesListBean> quesList;

        public String getAgentphone() {
            return agentphone;
        }

        public void setAgentphone(String agentphone) {
            this.agentphone = agentphone;
        }

        public String getVisephone() {
            return visephone;
        }

        public void setVisephone(String visephone) {
            this.visephone = visephone;
        }

        public String getAgentName() {
            return agentName;
        }

        public void setAgentName(String agentName) {
            this.agentName = agentName;
        }

        public String getBossName() {
            return bossName;
        }

        public void setBossName(String bossName) {
            this.bossName = bossName;
        }

        public List<QuesListBean> getQuesList() {
            return quesList;
        }

        public void setQuesList(List<QuesListBean> quesList) {
            this.quesList = quesList;
        }

        public static class QuesListBean {
            /**
             * question : 报名缴费后是否可以退款？
             * answer : 开课前1小时可以退
             */

            private String question;
            private String answer;

            public String getQuestion() {
                return question;
            }

            public void setQuestion(String question) {
                this.question = question;
            }

            public String getAnswer() {
                return answer;
            }

            public void setAnswer(String answer) {
                this.answer = answer;
            }
        }
    }
}
