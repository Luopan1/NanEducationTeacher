package com.test720.www.naneducationteacher.mineactivity;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.test720.www.naneducationteacher.R;
import com.test720.www.naneducationteacher.NimApplication;
import com.test720.www.naneducationteacher.baseui.BaseToolbarActivity;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

import static com.test720.www.naneducationteacher.NanEducationControl.teacherFeedBack;

public class MyAdvicesFeedBack extends BaseToolbarActivity {


    @BindView(R.id.advices)
    EditText mAdvices;
    @BindView(R.id.sendFeedBack)
    Button mSendFeedBack;

    @Override
    protected int getContentView() {
        return R.layout.activity_my_advices_feed_back;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initBase() {
        isShowBackImage = true;
        isShowToolBar = true;
    }

    @Override
    protected void initView() {
        initToobar("意见反馈");
        setToolbarColor(R.color.base_color);
        setTitleColor(R.color.white);
    }


    @OnClick(R.id.sendFeedBack)
    public void onClick() {
        if (mAdvices.getText().toString().isEmpty()) {
            ShowToast("请写下建议");
            return;
        }
        showLoadingDialog();
        AdvicesOkGo();
    }

    private void AdvicesOkGo() {
        OkGo.post(teacherFeedBack)
                .tag(this)
                .cacheKey("advices")
                .cacheMode(CacheMode.DEFAULT)
                .params("tcId", NimApplication.teacherId)
                .params("content", mAdvices.getText().toString().trim())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.e("==advices", s);
                        JSONObject jsonObject = JSON.parseObject(s);
                        cancleLoadingDialog();
                        ShowToast(jsonObject.getString("msg"));
                        if (jsonObject.getIntValue("code") == 1) {
                            finish();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        cancleLoadingDialog();
                        ShowToast(getString(R.string.timeout));
                        Log.e("==adviceserror", e.getMessage());
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
    }
}
