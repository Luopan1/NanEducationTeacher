package com.test720.www.naneducationteacher.login;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.test720.www.naneducationteacher.MainActivity;
import com.test720.www.naneducationteacher.NimApplication;
import com.test720.www.naneducationteacher.R;
import com.test720.www.naneducationteacher.allclass.SharePreferencesUtil;
import com.test720.www.naneducationteacher.baseui.BaseToolbarActivity;
import com.test720.www.naneducationteacher.utils.SharedPreferenceUtil;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;
import weiyicloud.com.eduhdsdk.message.RoomClient;

import static com.test720.www.naneducationteacher.NanEducationControl.teacherlogin;

public class LoginActivity extends BaseToolbarActivity {
    @BindView(R.id.LOGO)
    ImageView mLOGO;
    @BindView(R.id.Phone)
    ImageView mPhone;
    @BindView(R.id.PhoneNumber)
    EditText mPhoneNumber;
    @BindView(R.id.pwd)
    ImageView mPwd;
    @BindView(R.id.password)
    EditText mPassword;
    @BindView(R.id.Login)
    Button mLogin;

    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void initData() {
        mPhoneNumber.setText("");
        mPassword.setText("");
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initBase() {
        isShowBackImage = false;
        isShowToolBar = false;
    }

    @OnClick({R.id.Login})
    public void onClick() {
        if (mPhoneNumber.getText().toString().isEmpty()) {
            ShowToast("请输入账号");
            return;
        }
        if (mPassword.getText().toString().isEmpty()) {
            ShowToast("请输入密码");
            return;
        }
        showLoadingDialog();
        LoginOkGo();
    }

    private void LoginOkGo() {

        OkGo.post(teacherlogin)
                .tag(this)
                .cacheKey("login")
                .cacheMode(CacheMode.DEFAULT)
                .params("username", mPhoneNumber.getText().toString())
                .params("password", mPassword.getText().toString())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.e("==login", s);
                        JSONObject jsonObject = JSON.parseObject(s);
                        ShowToast(jsonObject.getString("msg"));
                        cancleLoadingDialog();
                        if (jsonObject.getIntValue("code") == 1) {
                            SharedPreferenceUtil.saveName(mPhoneNumber.getText().toString().trim(),LoginActivity.this);
                            SharedPreferenceUtil.savePwd(mPassword.getText().toString().trim(),LoginActivity.this);
                            if (jsonObject.getJSONObject("data") != null) {
                                NimApplication.head = jsonObject.getJSONObject("data").getString("head");
                                NimApplication.name = jsonObject.getJSONObject("data").getString("name");
                                NimApplication.teacherId = jsonObject.getJSONObject("data").getString("teacherId");
                                NimApplication.username = jsonObject.getJSONObject("data").getString("username");
                                SharePreferencesUtil.saveEvent(LoginActivity.this);
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
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
    }
}
