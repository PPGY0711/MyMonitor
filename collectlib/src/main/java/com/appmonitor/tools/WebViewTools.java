package com.appmonitor.tools;

import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.appmonitor.json.AjaxJSObject;
import com.appmonitor.json.ClickJSObject;
import com.appmonitor.json.ErrorJSObject;
import com.appmonitor.json.LoadingJSObject;
import com.appmonitor.webview.MyWebView;
import com.appmonitor.webview.MyWebViewClient;

public class WebViewTools {
    //通过JavascriptInterface为WebView注入Java类对象
    public static void setUpWithWebView(WebView webView, WebViewClient webViewClient){
        webView.addJavascriptInterface(new LoadingJSObject(),"loadingObj"); //页面加载时间数据
        webView.addJavascriptInterface(new ErrorJSObject(),"errorObj");     //JS脚本错误
        webView.addJavascriptInterface(new AjaxJSObject(),"ajaxObj");       //Ajax性能数据
        webView.addJavascriptInterface(new ClickJSObject(),"clickObj");     //点击流数据
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
    }

}
