package com.appmonitor.webview;

import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyWebViewClient extends WebViewClient {

    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        view.getSettings().setJavaScriptEnabled(true);
        //TODO collector.js文件的地址，收集的功能主要在这里面实现
        String injectJs = "file:////android_asset/collect.js";
        System.out.println("set MyWebViewClient");
        if(!injectJs.equals("")) {
            String msg = "javascript:" +
                    "   (function() { " +
                    "       var script=document.createElement('script');  " +
                    "       script.setAttribute('type','text/javascript');  " +
                    "       script.setAttribute('src', '" + injectJs + "'); " +
                    "       document.head.appendChild(script); " +
                    "       script.onload = function() {" +
                    "           startWebViewMonitor();" +
                    "           addCollectEvent();" +
                    "       }; " +
                    "    }" +
                    "    )();";

            view.loadUrl(msg);
        }
    }
}