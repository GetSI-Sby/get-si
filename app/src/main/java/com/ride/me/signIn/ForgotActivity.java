package com.ride.me.signIn;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ForgotActivity extends AppCompatActivity {
    @BindView(com.ride.me.R.id.forgot)
    WebView WebForgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.ride.me.R.layout.forgot_activity);
        ButterKnife.bind(this);

        WebForgot.clearCache(true);
        WebForgot.clearHistory();
        WebForgot.loadUrl("http://go-rideme.com/forgotPassword.php");
        WebForgot.setWebViewClient(new WebViewClient());
        WebForgot.setWebChromeClient(new WebChromeClient());
        WebForgot.getSettings().setJavaScriptEnabled(true);
        WebForgot.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
    }

}
