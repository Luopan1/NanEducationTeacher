package com.test720.www.naneducationteacher;

import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.footer.BallPulseView;
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.test720.www.naneducationteacher.allclass.SharePreferencesUtil;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.common.QueuedWork;

import java.util.logging.Level;


/**
 * Created by hzxuwen on 2016/2/25.
 */
public class NimApplication extends MultiDexApplication {
    private static NimApplication mContext;
    public static String name = "";
    public static String head = "";
    public static String teacherId = "";
    public static String username = "";

    public static void clearCache() {
        name = "";
        head = "";
        teacherId = "";
        username = "";
    }

    {
        PlatformConfig.setWeixin("wx3d199df284ee638b", "b96f2a5e2c53f75ba2d7812901836254");
        PlatformConfig.setQQZone("1106674132", "rag7TwUcovevkaJW");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        mContext = this;
        super.onCreate();
        SharePreferencesUtil.getEvent(getApplicationContext());
        TwinklingRefreshLayout.setDefaultHeader(SinaRefreshView.class.getName());//设置默认的刷新头
        TwinklingRefreshLayout.setDefaultFooter(BallPulseView.class.getName());//设置默认的脚

        /**友盟*/
        Config.DEBUG = true;
        QueuedWork.isUseThreadPool = true;
        UMShareAPI.get(this);

        // crash handler

        //         解决7.0不能打开照相机的问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();
        }


        //初始化okgo的网络框架
        OkGo.init(this);
        try {
            OkGo.getInstance()
                    // 打开该调试开关,打印级别INFO,并不是异常,是为了显眼,不需要就不要加入该行
                    // 最后的true表示是否打印okgo的内部异常，一般打开方便调试错误
                    .debug("OkGo", Level.INFO, true)
                    //全局的连接超时时间
                    .setConnectTimeout(10000)
                    //可以全局统一设置缓存模式,默认是不使用缓存
                    .setCacheMode(CacheMode.NO_CACHE)
                    //可以全局统一设置缓存时间,默认永不过期
                    .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)
                    //可以全局统一设置超时重连次数,默认为三次,那么最差的情况会请求4次(一次原始请求,三次重连请求),不需要可以设置为0
                    .setRetryCount(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static NimApplication getContext() {
        return mContext;
    }


}
