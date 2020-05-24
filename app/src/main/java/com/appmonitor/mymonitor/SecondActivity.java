package com.appmonitor.mymonitor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class SecondActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        webView = findViewById(R.id.testWebView);
        webView.setWebViewClient(new WebViewClient());
        String url = "file:////android_asset/login.html";
        webView.loadUrl(url);
    }

    public void onClickToFirstActivity(View view){
        try
        {
            Intent intent = new Intent(this, FirstActivity.class);//显示intent
            startActivity(intent);
        }
        catch (Exception ex)
        {
            // 显示异常信息
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickToMainActivity(View view){
        try
        {
            Intent intent = new Intent(this, MainActivity.class);//显示intent
            startActivity(intent);
        }
        catch (Exception ex)
        {
            // 显示异常信息
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
