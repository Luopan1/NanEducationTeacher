package com.test720.www.naneducationteacher;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.test720.www.naneducationteacher.allclass.SharePreferencesUtil;
import com.test720.www.naneducationteacher.baseui.BaseToolbarActivity;
import com.test720.www.naneducationteacher.fragments.CreatLiveBroadCastFragment;
import com.test720.www.naneducationteacher.fragments.MineFragment;
import com.test720.www.naneducationteacher.login.LoginActivity;
import com.test720.www.naneducationteacher.utils.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

import static com.test720.www.naneducationteacher.NanEducationControl.teacherlogin;

public class MainActivity extends BaseToolbarActivity {

    @BindView(R.id.content_relative)
    RelativeLayout mContentRelative;
    @BindView(R.id.liveBroadCast)
    RadioButton mLiveBroadCast;
    @BindView(R.id.mine)
    RadioButton mMine;
    @BindView(R.id.radioGroup)
    RadioGroup mRadioGroup;
    private Fragment mFragment;
    private FragmentManager mMamager;
    private List<Fragment> mFragments;
    private RadioButton[] rb;
    private Drawable[] drawables;

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {

        mMamager = getSupportFragmentManager();
        FragmentTransaction transaction = mMamager.beginTransaction();

        mFragments = new ArrayList<>();
        mFragments.add(new CreatLiveBroadCastFragment());
        mFragments.add(new MineFragment());

        mFragment = mFragments.get(0);
        transaction.replace(R.id.content_relative, mFragments.get(0));
        transaction.commit();

    }


    private void LoginOkGo() {

        OkGo.post(teacherlogin)
                .tag(this)
                .cacheKey("login")
                .cacheMode(CacheMode.DEFAULT)
                .params("username", SharedPreferenceUtil.getUserName(MainActivity.this))
                .params("password", SharedPreferenceUtil.getPwd(MainActivity.this))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.e("==login", s);
                        JSONObject jsonObject = JSON.parseObject(s);
                        ShowToast(jsonObject.getString("msg"));
                        cancleLoadingDialog();
                        if (jsonObject.getIntValue("code") == 1) {
                            if (jsonObject.getJSONObject("data") != null) {
                                NimApplication.head = jsonObject.getJSONObject("data").getString("head");
                                NimApplication.name = jsonObject.getJSONObject("data").getString("name");
                                NimApplication.teacherId = jsonObject.getJSONObject("data").getString("teacherId");
                                NimApplication.username = jsonObject.getJSONObject("data").getString("username");
                                SharePreferencesUtil.saveEvent(MainActivity.this);
                                jumpToActivity(MainActivity.class, true);

                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        cancleLoadingDialog();
                        ShowToast(getString(R.string.timeout));
                        Log.e("==loginerror", e.getMessage());
                    }
                });
    }


    @Override
    protected void setListener() {
    }

    @Override
    protected void initBase() {
        isShowToolBar = true;
        isShowBackImage = false;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void initView() {
        initToobar(-1, "直播", R.mipmap.shezhi);

        rb = new RadioButton[2];
        rb[0] = mLiveBroadCast;
        rb[1] = mMine;
        for (int i = 0; i < rb.length; i++) {
            //挨着给每个RadioButton加入drawable限制边距以控制显示大小
            drawables = rb[i].getCompoundDrawables();
            //获取drawables，2/5表示图片要缩小的比例
            Rect r = new Rect(0, 0, drawables[1].getMinimumWidth() * 4 / 5, drawables[1].getMinimumHeight() * 4 / 5);
            //定义一个Rect边界
            drawables[1].setBounds(r);
            //给每一个RadioButton设置图片大小
            rb[i].setCompoundDrawables(null, drawables[1], null, null);
        }

    }

    @OnClick({R.id.liveBroadCast, R.id.mine})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.liveBroadCast:
                if (NimApplication.teacherId.equals("")) {
                    ShowToast("请先登录");
                    jumpToActivity(LoginActivity.class, true);
                } else {
                    initToobar(-1, "直播", R.mipmap.shezhi);
                    switchContent(mFragment, mFragments.get(0));
                }

                break;
            case R.id.mine:
                if (NimApplication.teacherId.equals("")) {
                    ShowToast("请先登录");
                    jumpToActivity(LoginActivity.class, true);
                } else {
                    initToobar(-1, "我的", R.mipmap.shezhi);
                    switchContent(mFragment, mFragments.get(1));
                }
                break;
        }
    }

    /**
     * 判断是否被add过
     * add过  隐藏当前的fragment，add下一个到Activity中
     * 否则   隐藏当前的fragment，显示下一个
     */
    public void switchContent(Fragment from, Fragment to) {
        if (mFragment != to) {
            mFragment = to;
            FragmentTransaction transaction = mMamager.beginTransaction();
            if (!to.isAdded()) {
                // 隐藏当前的fragment，add下一个到Activity中
                transaction.hide(from).add(R.id.content_relative, to).commitAllowingStateLoss();
            } else {
                // 隐藏当前的fragment，显示下一个
                transaction.hide(from).show(to).commitAllowingStateLoss();
            }
        }
    }

    @Override
    public void RightOnClick() {
        // TODO: 2017/11/6  goto setting class
        if (NimApplication.teacherId.equals("")){
            ShowToast("请先登录");
            jumpToActivity(LoginActivity.class,false);

        }else {
            jumpToActivity(SettingActivity.class, false);
        }
    }
}
