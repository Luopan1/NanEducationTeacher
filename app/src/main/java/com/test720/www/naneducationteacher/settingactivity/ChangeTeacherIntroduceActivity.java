package com.test720.www.naneducationteacher.settingactivity;

import android.content.Intent;
import android.util.Log;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.test720.www.naneducationteacher.NimApplication;
import com.test720.www.naneducationteacher.R;
import com.test720.www.naneducationteacher.baseui.BaseToolbarActivity;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Response;

import static com.test720.www.naneducationteacher.NanEducationControl.editUserInfo;

public class ChangeTeacherIntroduceActivity extends BaseToolbarActivity {


    @BindView(R.id.teacherIntroduce)
    EditText mTeacherIntroduce;

    @Override
    protected int getContentView() {
        return R.layout.activity_change_teacher_introduce;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initBase() {
        isShowToolBar = true;
        isShowBackImage = true;
    }

    @Override
    protected void initView() {
        initToobar(R.mipmap.fanhui, "讲师介绍", "保存");
    }

    @Override
    public void RightOnClick() {
        if (mTeacherIntroduce.getText().toString().isEmpty()) {
            ShowToast("请输入内容");
            return;
        }
        showLoadingDialog();
        OkGo.post(editUserInfo)
                .tag(this)
                .cacheKey("setting")
                .cacheMode(CacheMode.DEFAULT)
                .params("tcId", NimApplication.teacherId)
                .params("content", mTeacherIntroduce.getText().toString())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.e("==setting", s);
                        JSONObject jsonObject = JSON.parseObject(s);
                        cancleLoadingDialog();
                        ShowToast(jsonObject.getString("msg"));
                        Intent intent = new Intent();
                        intent.putExtra("mTeacherIntroduce", mTeacherIntroduce.getText().toString().trim());
                        setResult(3, intent);
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        cancleLoadingDialog();
                        ShowToast(getString(R.string.timeout));
                        Log.e("==settingerror", e.getMessage());
                    }
                });
    }
}
