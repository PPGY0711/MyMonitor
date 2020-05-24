package com.appmonitor.webview;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

import com.appmonitor.json.AjaxJSObject;
import com.appmonitor.json.ClickJSObject;
import com.appmonitor.json.ErrorJSObject;
import com.appmonitor.json.LoadingJSObject;

public class MyWebView extends WebView {

    public MyWebView(Context context) {
        super(context);
        super.addJavascriptInterface(new LoadingJSObject(),"loadingObj");
        super.addJavascriptInterface(new ErrorJSObject(),"errorObj");
        super.addJavascriptInterface(new AjaxJSObject(),"ajaxObj");
        super.addJavascriptInterface(new ClickJSObject(),"clickObj");
    }

    public  MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.addJavascriptInterface(new LoadingJSObject(),"loadingObj");
        super.addJavascriptInterface(new ErrorJSObject(),"errorObj");
        super.addJavascriptInterface(new AjaxJSObject(),"ajaxObj");
        super.addJavascriptInterface(new ClickJSObject(),"clickObj");
    }

    public  MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.addJavascriptInterface(new LoadingJSObject(),"loadingObj");
        super.addJavascriptInterface(new ErrorJSObject(),"errorObj");
        super.addJavascriptInterface(new AjaxJSObject(),"ajaxObj");
        super.addJavascriptInterface(new ClickJSObject(),"clickObj");
    }
    
}

