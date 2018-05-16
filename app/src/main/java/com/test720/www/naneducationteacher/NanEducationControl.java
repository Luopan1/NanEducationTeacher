package com.test720.www.naneducationteacher;

/**
 * 公共管理类
 * Created by wangshuai on 2017/8/25.
 */
public class NanEducationControl {

    /**
     * 接口地址
     */
    //    private static String parentUrl = "http://192.168.1.233/";//本地ip
    private static String parentUrl = "http://116.62.210.125/";//远程ip
    //    private static String parentUrl = "http://192.168.1.170/";//无数据

    public static String teacherlogin = parentUrl + "ncjy/index.php/Myapi/teacher/teacherlogin";//教师登陆接口
    public static String teacherFeedBack = parentUrl + "ncjy/index.php/Myapi/teacher/teacherFeedBack";//教师反馈接口
    public static String teacherSetup = parentUrl + "ncjy/index.php/Myapi/teacher/teacherSetup";//教师个人信息接口
    public static String editUserInfo = parentUrl + "ncjy/index.php/Myapi/teacher/editUserInfo";//教师个人信息设置接口
    public static String teacherWallet = parentUrl + "ncjy/index.php/Myapi/teacher/teacherWallet";//教师钱包接口
    public static String liveTypeList = parentUrl + "ncjy/index.php/Myapi/teacher/liveTypeList";//教师创建直播类型列表接口
    public static String createLive = parentUrl + "ncjy/index.php/Myapi/teacher/createLive";//教师创建直播接口
    public static String teacherLiveList = parentUrl + "ncjy/index.php/Myapi/teacher/teacherLiveList";//教师直播录播列表接口
    public static String liveDetail = parentUrl + "ncjy/index.php/Myapi/teacher/liveDetail";//教师直播录播详情接口
    public static String teacherMoney = parentUrl + "ncjy/index.php/Myapi/teacher/teacherMoney";//教师金额明细接口
    public static String teacherBankcard = parentUrl + "ncjy/index.php/Myapi/teacher/teacherBankcard";//教师银行卡信息接口
    public static String bindUserCard = parentUrl + "ncjy/index.php/Myapi/teacher/bindUserCard";//教师银行卡信息添加修改接口
    public static String userDeposit = parentUrl + "ncjy/index.php/Myapi/teacher/userDeposit";//教师提现信息接口
    public static String userMoneyCash = parentUrl + "ncjy/index.php/Myapi/teacher/userMoneyCash";//教师提现接口
    public static String tchDown = parentUrl + "ncjy/index.php/Myapi/index/tchDown";
    public static String tchBanner = parentUrl + "ncjy/index.php/Myapi/teacher/tchBanner";
    public static String teacherDetail = parentUrl + "ncjy/index.php/Myapi/index/teacherDetail";
    public static String agentInter = parentUrl + "ncjy/index.php/Myapi/teacher/agentInter";

    /**
     * 图片拼接地址
     */
    //    public static String parentimageUrl = "http://192.168.1.233/ncjy/";//本地ip
    public static String parentimageUrl = "http://116.62.210.125/ncjy/";//远程ip
    //    public static String parentimageUrl = "http://192.168.1.169/ncjy/";//无数据
}
