package com.test720.www.naneducationteacher.mineactivity;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioButton;

import com.test720.www.naneducationteacher.R;
import com.test720.www.naneducationteacher.adapter.PagerAdapter;
import com.test720.www.naneducationteacher.baseui.BaseToolbarActivity;
import com.test720.www.naneducationteacher.fragments.WalletEarnFragment;
import com.test720.www.naneducationteacher.fragments.WalletWithDrawFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class WalletInfoActivity extends BaseToolbarActivity {
    @BindView(R.id.withDrwal)
    RadioButton mWithDrwal;
    @BindView(R.id.earn)
    RadioButton mEarn;
    @BindView(R.id.viewpager)
    ViewPager mViewpager;
    List<Fragment> mFragments = new ArrayList<>();

    @Override
    protected int getContentView() {
        return R.layout.activity_wallet_info;
    }

    @Override
    protected void initData() {
        mFragments.add(new WalletWithDrawFragment());
        mFragments.add(new WalletEarnFragment());
        mViewpager.setAdapter(new PagerAdapter(getSupportFragmentManager(), mFragments));
    }

    @Override
    protected void setListener() {
        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mWithDrwal.setChecked(true);
                        mEarn.setChecked(false);
                        break;
                    case 1:
                        mWithDrwal.setChecked(false);
                        mEarn.setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void initBase() {
        isShowBackImage = true;
        isShowToolBar = true;
    }

    @Override
    protected void initView() {
        initToobar("明细");
    }

    @OnClick({R.id.withDrwal, R.id.earn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.withDrwal:
                mViewpager.setCurrentItem(0);
                break;
            case R.id.earn:
                mViewpager.setCurrentItem(1);
                break;
        }
    }
}
