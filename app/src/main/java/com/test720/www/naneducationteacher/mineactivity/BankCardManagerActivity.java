package com.test720.www.naneducationteacher.mineactivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.test720.www.naneducationteacher.R;
import com.test720.www.naneducationteacher.NimApplication;
import com.test720.www.naneducationteacher.baseui.BaseToolbarActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

import static com.test720.www.naneducationteacher.NanEducationControl.bindUserCard;
import static com.test720.www.naneducationteacher.NanEducationControl.teacherBankcard;

public class BankCardManagerActivity extends BaseToolbarActivity {
    @BindView(R.id.bankCardNumber)
    EditText bankCardNumber;
    @BindView(R.id.confirmBankCardNumber)
    EditText confirmBankCardNumber;
    @BindView(R.id.holdCardPersonName)
    EditText holdCardPersonName;
    @BindView(R.id.openTheCardBank)
    EditText openTheCardBank;
    @BindView(R.id.bindNewCard)
    Button bindNewCard;
    @BindView(R.id.old_bankcardnum)
    TextView oldBankcardnum;

    @Override
    protected int getContentView() {
        return R.layout.activity_bank_card_manager;
    }

    @Override
    protected void initData() {
        initToobar("绑定银行卡", R.mipmap.fanhui);
        showLoadingDialog();
        BankCardMsgOkGo();
    }

    private void BankCardMsgOkGo() {
        OkGo.post(teacherBankcard)
                .tag(this)
                .cacheKey("bankcard")
                .cacheMode(CacheMode.DEFAULT)
                .params("tcId", NimApplication.teacherId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.e("==bankcardmsg", s);
                        cancleLoadingDialog();
                        JSONObject jsonObject = JSON.parseObject(s);
                        if (jsonObject.getIntValue("code") == 1) {
                            if (jsonObject.getJSONObject("data") != null) {
                                oldBankcardnum.setText(jsonObject.getJSONObject("data").getString("bankcard"));
//                                confirmBankCardNumber.setText(jsonObject.getJSONObject("data").getString("bankcard"));
//                                holdCardPersonName.setText(jsonObject.getJSONObject("data").getString("username"));
//                                openTheCardBank.setText(jsonObject.getJSONObject("data").getString("bank_name"));
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        cancleLoadingDialog();
                        ShowToast(getString(R.string.timeout));
                        Log.e("==bankcardmsgerror", e.getMessage());
                    }
                });
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void initBase() {
    }

    @OnClick(R.id.bindNewCard)
    public void onClick() {
        if (editTextIsEmpty(bankCardNumber) || editTextIsEmpty(confirmBankCardNumber) || editTextIsEmpty(holdCardPersonName)
                || editTextIsEmpty(openTheCardBank) || !bankCardNumber.getText().toString().equals(confirmBankCardNumber.getText().toString())) {
            ShowToast("请完善信息");
            return;
        }
        showLoadingDialog();
        BankCardManagerOkGo();
    }

    private void BankCardManagerOkGo() {
        OkGo.post(bindUserCard).tag(this).cacheKey("bankcard").cacheMode(CacheMode.DEFAULT)
                .params("tcId", NimApplication.teacherId)
                .params("bank_name", holdCardPersonName.getText().toString().trim())
                .params("bankcard", bankCardNumber.getText().toString().trim())
                .params("username", openTheCardBank.getText().toString().trim())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.e("==bankcard", s);
                        JSONObject jsonObject = JSON.parseObject(s);
                        ShowToast(jsonObject.getString("msg"));
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
                        Log.e("==bankcarderror", e.getMessage());
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
