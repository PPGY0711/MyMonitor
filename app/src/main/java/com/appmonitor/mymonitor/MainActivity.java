package com.appmonitor.mymonitor;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.appmonitor.tools.InitApmTools;

public class MainActivity extends AppCompatActivity {

    private CustomApplication app;

    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitApmTools.initPermission(this).run();
        webView = findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());
        String url = "file:////android_asset/testWebViewMonitor.html";
        webView.loadUrl(url);

//        System.out.println("Generate caton in MainActivity!");
//        pause();
    }

    /**
     * 按钮Sencond Activity 事件处理
     */
    public void onClick(View view)
    {
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

    public void pause(View view){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
