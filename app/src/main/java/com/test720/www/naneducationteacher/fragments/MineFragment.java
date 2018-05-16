package com.test720.www.naneducationteacher.fragments;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.test720.www.naneducationteacher.NimApplication;
import com.test720.www.naneducationteacher.R;
import com.test720.www.naneducationteacher.allclass.SharePreferencesUtil;
import com.test720.www.naneducationteacher.baseui.BaseFragment;
import com.test720.www.naneducationteacher.login.LoginActivity;
import com.test720.www.naneducationteacher.mineactivity.CustomerServiceActivity;
import com.test720.www.naneducationteacher.mineactivity.MIneVideoCacheActivity;
import com.test720.www.naneducationteacher.mineactivity.MineLiveBroadCastActivty;
import com.test720.www.naneducationteacher.mineactivity.MineWalletActivity;
import com.test720.www.naneducationteacher.mineactivity.MyAdvicesFeedBack;
import com.test720.www.naneducationteacher.view.CircleImageView;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

import static com.test720.www.naneducationteacher.NanEducationControl.parentimageUrl;
import static com.test720.www.naneducationteacher.NanEducationControl.teacherSetup;

/**
 * Created by LuoPan on 2017/11/6.
 */

public class MineFragment extends BaseFragment {
    @BindView(R.id.user_head)
    CircleImageView mUserHead;
    @BindView(R.id.userNickName)
    TextView mUserNickName;
    @BindView(R.id.wallet)
    RelativeLayout mWallet;
    @BindView(R.id.liveBroadCast)
    RelativeLayout mLiveBroadCast;
    @BindView(R.id.videoCache)
    RelativeLayout mVideoCache;
    @BindView(R.id.advicesFeedBack)
    RelativeLayout mAdvicesFeedBack;
    @BindView(R.id.contactUs)
    RelativeLayout mContactUs;
    @BindView(R.id.check_out)
    Button mCheckOut;

    @Override
    protected void initView() {
    }

    @Override
    public void onResume() {
        super.onResume();
        MineOkGo();
    }

    private void MineOkGo() {
        //网络请求
        OkGo.post(teacherSetup)
                .tag(this)
                .cacheKey("mine")
                .cacheMode(CacheMode.DEFAULT)
                .params("tcId", NimApplication.teacherId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.e("==mine", s);
                        JSONObject jsonObject = JSON.parseObject(s);
                        if (jsonObject.getIntValue("code") == 1) {
                            if (jsonObject.getJSONObject("data") != null) {
                                if (jsonObject.getJSONObject("data").getJSONObject("userInfo") != null) {
                                    Glide.with(getActivity()).load(parentimageUrl + jsonObject.getJSONObject("data").getJSONObject("userInfo").getString("head"))
                                            .error(R.mipmap.touxiang).into(mUserHead);
                                    mUserNickName.setText(jsonObject.getJSONObject("data").getJSONObject("userInfo").getString("name"));
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Log.e("==mineerror", e.getMessage());
                    }
                });
    }

    @Override
    protected void initData() {
        //设置数据
        Glide.with(getActivity()).load(parentimageUrl + NimApplication.head).error(R.mipmap.icon_head_portrait).into(mUserHead);
        mUserNickName.setText(NimApplication.name);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected int setLayoutResID() {
        return R.layout.fragment_mine;
    }


    @OnClick({R.id.wallet, R.id.liveBroadCast, R.id.videoCache, R.id.advicesFeedBack, R.id.contactUs, R.id.check_out})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.wallet:
                jumpToActivity(MineWalletActivity.class, false);
                break;
            case R.id.liveBroadCast:
                jumpToActivity(MineLiveBroadCastActivty.class, false);
                break;
            case R.id.videoCache:
                jumpToActivity(MIneVideoCacheActivity.class, false);
                break;
            case R.id.advicesFeedBack:
                jumpToActivity(MyAdvicesFeedBack.class, false);
                break;
            case R.id.contactUs:
                jumpToActivity(CustomerServiceActivity.class, false);
                break;
            case R.id.check_out:
                NimApplication.clearCache();
                SharePreferencesUtil.clear(getActivity());
                jumpToActivity(LoginActivity.class, true);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
    }
}
