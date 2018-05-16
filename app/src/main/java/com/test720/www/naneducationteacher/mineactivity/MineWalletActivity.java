package com.test720.www.naneducationteacher.mineactivity;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
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

import static com.test720.www.naneducationteacher.NanEducationControl.teacherWallet;

public class MineWalletActivity extends BaseToolbarActivity {
    @BindView(R.id.fanhuiRelative)
    RelativeLayout mFanhuiRelative;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.walletInfo)
    RelativeLayout mWalletInfo;
    @BindView(R.id.text)
    TextView mText;
    @BindView(R.id.totalMoney)
    TextView mTotalMoney;
    @BindView(R.id.canUseMoney)
    TextView mCanUseMoney;
    @BindView(R.id.FrozenMoney)
    TextView mFrozenMoney;
    @BindView(R.id.Withdrawals)
    RelativeLayout mWithdrawals;
    @BindView(R.id.bankCardNumber)
    TextView mBankCardNumber;
    @BindView(R.id.bankCardManager)
    RelativeLayout mBankCardManager;
    @BindView(R.id.RefreshLayout)
    TwinklingRefreshLayout mRefreshLayout;

    @Override
    protected int getContentView() {
        return R.layout.activity_mine_wallet;
    }

    @Override
    protected void initData() {
        showLoadingDialog();
        MineWalletOkGo();
    }

    private void MineWalletOkGo() {
        OkGo.post(teacherWallet)
                .tag(this)
                .cacheKey("minewallet")
                .cacheMode(CacheMode.DEFAULT)
                .params("tcId", NimApplication.teacherId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.e("==minewallet", s);
                        JSONObject jsonObject = JSON.parseObject(s);
                        cancleLoadingDialog();
                        mRefreshLayout.finishRefreshing();
                        if (jsonObject.getIntValue("code") == 1) {
                            if (jsonObject.getJSONObject("data") != null) {
                                mTotalMoney.setText(jsonObject.getJSONObject("data").getString("price"));
                                mCanUseMoney.setText("可用金额：" + jsonObject.getJSONObject("data").getString("userprice") + "元");
                                mFrozenMoney.setText("冻结金额：" + jsonObject.getJSONObject("data").getString("dieprice") + "元");
                                mBankCardNumber.setText(jsonObject.getJSONObject("data").getString("bankcard"));
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        cancleLoadingDialog();
                        ShowToast(getString(R.string.timeout));
                        mRefreshLayout.finishRefreshing();
                        Log.e("==minewalleterror", e.getMessage());
                    }
                });
    }

    @Override
    protected void setListener() {
        //点击事件
        mFanhuiRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //刷新
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                MineWalletOkGo();
            }

            //上拉加载
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ShowToast("上拉结束了....");
                        mRefreshLayout.finishLoadmore();
                    }
                }, 2000);
            }
        });
    }

    @Override
    protected void initBase() {
        isShowBackImage = false;
        isShowToolBar = false;
    }


    @OnClick({R.id.walletInfo, R.id.Withdrawals, R.id.bankCardManager})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.walletInfo:
                jumpToActivity(WalletInfoActivity.class, false);
                break;
            case R.id.Withdrawals:
                jumpToActivity(WithdrawalsActivity.class, false);
                break;
            case R.id.bankCardManager:
                jumpToActivity(BankCardManagerActivity.class, false);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
    }
}
