package com.test720.www.naneducationteacher.allclass;

import android.content.Context;
import android.content.SharedPreferences;

import com.test720.www.naneducationteacher.NimApplication;


/**
 * Created by wangshuai on 2017/7/17.
 */

public class SharePreferencesUtil {

    /**
     * 保存一些事件,让后面的点击事件可以通过以此来区别
     */
    public static void saveEvent(Context context) {
        //获取sharepreferences对象
        SharedPreferences sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        //获取edtior
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //设置参数
        editor.putString("teacherId", NimApplication.teacherId);
        //提交
        editor.apply();
    }

    /**
     * 得到一些事件，根据此进行判断
     */
    public static void getEvent(Context context) {
        //获取sharedpreference对象
        SharedPreferences sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        NimApplication.teacherId = sharedPreferences.getString("teacherId", "");
    }

    /**
     * 退出登录时清空
     */
    public static void logout(Context context) {
        SharedPreferences sharedPre = context.getSharedPreferences("config",
                context.MODE_PRIVATE);
        // 获取Editor对象
        SharedPreferences.Editor editor = sharedPre.edit();
        // 设置参数
        editor.putString("teacherId", "");
        // 提交
        editor.commit();
    }


    public static void saveIsRemind(Context context,boolean b) {
        //获取sharepreferences对象
        SharedPreferences sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        //获取edtior
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //设置参数
        editor.putBoolean("isRemind", b);
        //提交
        editor.apply();
    }

    public static boolean getIsRemid(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("isRemind", false);
    }

    public static void clear(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

}
