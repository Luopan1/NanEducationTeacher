package com.test720.www.naneducationteacher.view;

import android.net.http.SslError;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.test720.www.naneducationteacher.R;
import com.test720.www.naneducationteacher.baseui.BaseToolbarActivity;

import butterknife.BindView;

public class VideoActivity extends BaseToolbarActivity {

    @BindView(R.id.webView)
    WebView mWebView;
    @BindView(R.id.hintText)
    TextView mHintText;

    @Override
    protected int getContentView() {
        return R.layout.activity_video;
    }


    @Override
    protected void initData() {
        mWebView.setWebViewClient(new WVClient());
        mWebView.loadUrl(getIntent().getStringExtra("path"));
    }

    @Override
    public void setFullScreen() {
        PORTRAIT = false;
        isFullScreen = true;
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initBase() {
        isShowBackImage = false;
        isShowToolBar = false;
    }


    private class WVClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            //在当前Activity打开
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            //https忽略证书问题
            handler.proceed();
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            LogUtils.e(view.getContentHeight() + "");
            if (view.getContentHeight() <= 0) {
                mHintText.setVisibility(View.VISIBLE);
            } else {
                mHintText.setVisibility(View.GONE);
            }

            super.onPageFinished(view, url);

        }

    }

}
