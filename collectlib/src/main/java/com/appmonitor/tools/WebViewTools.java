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

    public static void setUpWithWebView(WebView webView, WebViewClient webViewClient){
        System.out.println("before set webviewclient");
//        System.out.println(new MyWebView(InitApmTools.getAppContext()) instanceof WebView);
        webView.addJavascriptInterface(new LoadingJSObject(),"loadingObj");
        webView.addJavascriptInterface(new ErrorJSObject(),"errorObj");
        webView.addJavascriptInterface(new AjaxJSObject(),"ajaxObj");
        webView.addJavascriptInterface(new ClickJSObject(),"clickObj");
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        System.out.println("after set webviewclient");
    }

}
