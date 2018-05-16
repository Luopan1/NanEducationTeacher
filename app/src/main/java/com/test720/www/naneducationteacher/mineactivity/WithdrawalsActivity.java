package com.test720.www.naneducationteacher.mineactivity;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.test720.www.naneducationteacher.NimApplication;
import com.test720.www.naneducationteacher.R;
import com.test720.www.naneducationteacher.baseui.BaseToolbarActivity;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

import static com.test720.www.naneducationteacher.NanEducationControl.userDeposit;
import static com.test720.www.naneducationteacher.NanEducationControl.userMoneyCash;

public class WithdrawalsActivity extends BaseToolbarActivity {
    @BindView(R.id.ServiceCharge)
    TextView ServiceCharge;
    @BindView(R.id.leastCanWithDrawal)
    TextView leastCanWithDrawal;
    @BindView(R.id.confirmWithDrawal)
    Button confirmWithDrawal;
    @BindView(R.id.WithdrawalMoney)
    EditText WithdrawalMoney;
    @BindView(R.id.canWithDrwasMoney)
    TextView canWithDrwasMoney;
    private String price;
    private JSONObject mJsonObject;

    @Override
    protected int getContentView() {
        return R.layout.activity_withdrawals;
    }

    @Override
    protected void initData() {
        initToobar("提现", R.mipmap.fanhui);
        showLoadingDialog();
        WithDrawalsOkGo();
    }

    private void WithDrawalsOkGo() {
        OkGo.post(userDeposit)
                .tag(this)
                .cacheKey("withdrawals")
                .cacheMode(CacheMode.DEFAULT)
                .params("tcId", NimApplication.teacherId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.e("==withdrawals", s);
                        mJsonObject = JSON.parseObject(s);
                        cancleLoadingDialog();
                        if (mJsonObject.getIntValue("code") == 1) {
                            if (mJsonObject.getJSONObject("data") != null) {
                                ServiceCharge.setText("请输入提现金额(提现收取" + mJsonObject.getJSONObject("data").getString("procedures") + "%的手续费)");
                                leastCanWithDrawal.setText("最少可提现金额" + mJsonObject.getJSONObject("data").getString("downprice") + "元");
                                canWithDrwasMoney.setText(mJsonObject.getJSONObject("data").getString("price") + "元");
                                price = mJsonObject.getJSONObject("data").getString("price");
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        cancleLoadingDialog();
                        ShowToast(getString(R.string.timeout));
                        Log.e("==withdrawalserror", e.getMessage());
                    }
                });
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void initBase() {
    }

    @OnClick(R.id.confirmWithDrawal)
    public void onClick() {
        if (editTextIsEmpty(WithdrawalMoney)) {
            ShowToast("请输入提现金额");
            return;
        }
        if (Double.valueOf(WithdrawalMoney.getText().toString().trim()) > Double.valueOf(price)) {
            ShowToast("金额不足");
            return;
        }
        if (Double.parseDouble(mJsonObject.getJSONObject("data").getString("downprice")) > Double.parseDouble(WithdrawalMoney.getText().toString().trim())) {
            ShowToast("提现金额小于最少提现金额");
            return;
        }
        showLoadingDialog();
        confirmWithDrawalOkGo();
    }

    private void confirmWithDrawalOkGo() {
        OkGo.post(userMoneyCash)
                .tag(this)
                .cacheKey("withdrawals")
                .cacheMode(CacheMode.DEFAULT)
                .params("tcId", NimApplication.teacherId)
                .params("money", WithdrawalMoney.getText().toString().trim())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.e("==withdrawals", s);
                        cancleLoadingDialog();
                        JSONObject jsonObject = JSON.parseObject(s);
                        if (jsonObject.getIntValue("code") == 1) {
                            if (jsonObject.getJSONArray("data") != null) {
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        cancleLoadingDialog();
                        ShowToast(getString(R.string.timeout));
                        Log.e("==withdrawalserror", e.getMessage());
                    }
                });
    }
}
