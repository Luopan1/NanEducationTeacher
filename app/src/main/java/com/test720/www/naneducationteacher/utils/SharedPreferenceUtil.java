package com.test720.www.naneducationteacher.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by liu on 2017/12/11.
 */

public class SharedPreferenceUtil {

    private final static String NAME = "user_name";
    private final static String PWD = "user_password";
    private final static String APP_KEY = "key";
    private final static String ISCHILDCOUNT = "isChildCount";

    /* *
    * ** NimApplication.head = jsonObject.getJSONObject("data").getString("head");
         NimApplication.name = jsonObject.getJSONObject("data").getString("name");
         NimApplication.teacherId = jsonObject.getJSONObject("data").getString("teacherId");
         NimApplication.username = jsonObject.getJSONObject("data").getString("username");*/

    public SharedPreferenceUtil() {
    }

    public static void saveHuanxin(String name, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString("username", name).commit();
    }

    public static String getHuanxin(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("username", null);
    }

    public static void saveName(String name, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString(NAME, name).commit();
    }

    public static void savePwd(String password, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString(PWD, password).commit();
    }

    public static String getUserName(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(NAME, null);
    }

    public static String getPwd(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PWD, null);
    }

    public static void saveKey(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString(APP_KEY, key).commit();
    }

    public static String getKey(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(APP_KEY, null);
    }

    public static void clearInfo(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().clear().commit();
    }

    public static void setIsChildCount(boolean key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putBoolean(ISCHILDCOUNT, key).commit();
    }

    public static boolean getIsChildCount(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(ISCHILDCOUNT, false);
    }

}
