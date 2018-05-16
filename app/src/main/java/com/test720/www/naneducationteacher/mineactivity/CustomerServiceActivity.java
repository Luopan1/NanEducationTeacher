package com.test720.www.naneducationteacher.mineactivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.test720.www.naneducationteacher.NimApplication;
import com.test720.www.naneducationteacher.R;
import com.test720.www.naneducationteacher.adapter.BaseRecyclerAdapter;
import com.test720.www.naneducationteacher.adapter.BaseRecyclerHolder;
import com.test720.www.naneducationteacher.baseui.BaseToolbarActivity;
import com.test720.www.naneducationteacher.bean.Customer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

import static com.test720.www.naneducationteacher.NanEducationControl.agentInter;

public class CustomerServiceActivity extends BaseToolbarActivity {


    @BindView(R.id.staffNumber)
    TextView mStaffNumber;
    @BindView(R.id.bossNumber)
    TextView mBossNumber;
    @BindView(R.id.questionRecyclerView)
    RecyclerView mQuestionRecyclerView;
    @BindView(R.id.staffName)
    TextView mStaffName;
    @BindView(R.id.bossName)
    TextView mBossName;
    private Customer mCustomer;
    List<Customer.DataBean.QuesListBean> mQuesLists=new ArrayList<>();
    BaseRecyclerAdapter<Customer.DataBean.QuesListBean> adapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_customer_service;
    }

    @Override
    protected void initData() {
        showLoadingDialog();
        OkGo.post(agentInter)
                .tag(this)
                .cacheMode(CacheMode.DEFAULT)
                .params("tcId", NimApplication.teacherId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        cancleLoadingDialog();
                        JSONObject obj = JSON.parseObject(s);
                        ShowToast(obj.getString("msg"));
                        if (obj.getInteger("code") == 1) {
                            mCustomer = JSON.parseObject(s, Customer.class);
                            mStaffName.setText(mCustomer.getData().getAgentName());
                            mBossName.setText(mCustomer.getData().getBossName());

                            mStaffNumber.setText(mCustomer.getData().getAgentphone());
                            mBossNumber.setText(mCustomer.getData().getVisephone());
                            mQuesLists.clear();
                            mQuesLists.addAll(mCustomer.getData().getQuesList());
                            setAdapter(mQuesLists);

                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        cancleLoadingDialog();
                        ShowToast("网络请求失败或超时，请稍后重试...");
                        Log.e("==withdrawerror", e.getMessage());
                    }
                });
    }

    private void setAdapter(List<Customer.DataBean.QuesListBean> quesLists) {
        if (adapter==null){
            adapter=new BaseRecyclerAdapter<Customer.DataBean.QuesListBean>(this, mQuesLists, R.layout.item_question_item) {
                @Override
                public void convert(BaseRecyclerHolder holder, Customer.DataBean.QuesListBean item, int position, boolean isScrolling) {
                    holder.setText(R.id.question, item.getQuestion());
                    holder.setText(R.id.answer, item.getAnswer());
                }
            }; mQuestionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mQuestionRecyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
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
        initToobar("客服");
        setToolbarColor(R.color.base_color);
        setTitleColor(R.color.white);
    }

    @OnClick({R.id.staffNumber, R.id.bossNumber})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.staffNumber:
                // TODO: 2017/11/7 callPhone
                final MaterialDialog dialog = new MaterialDialog(CustomerServiceActivity.this);
                dialog.content("是否拨打客服电话" + mStaffNumber.getText().toString().trim())//
                        .contentTextSize(14)
                        .titleTextSize(16)
                        .btnText("拨打电话", "取消")//
                        .showAnim(new BounceTopEnter())//
                        .show();
                dialog.setOnBtnClickL(new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                        if (ActivityCompat.checkSelfPermission(CustomerServiceActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(CustomerServiceActivity.this, "拨打电话权限已关闭，请打开权限", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        CustomerServiceActivity.this.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mStaffNumber.getText().toString().trim())));
                    }
                }, new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                });



                break;
            case R.id.bossNumber:
                // TODO: 2017/11/7 callPhone
                final MaterialDialog bossDialog = new MaterialDialog(CustomerServiceActivity.this);
                bossDialog.content("是否拨打客服电话" + mBossNumber.getText().toString().trim())//
                        .contentTextSize(14)
                        .titleTextSize(16)
                        .btnText("拨打电话", "取消")//
                        .showAnim(new BounceTopEnter())//
                        .show();
                bossDialog.setOnBtnClickL(new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        bossDialog.dismiss();
                        if (ActivityCompat.checkSelfPermission(CustomerServiceActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(CustomerServiceActivity.this, "拨打电话权限已关闭，请打开权限", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        CustomerServiceActivity.this.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mBossNumber.getText().toString().trim())));
                    }
                }, new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        bossDialog.dismiss();
                    }
                });
                break;
        }
    }
}
