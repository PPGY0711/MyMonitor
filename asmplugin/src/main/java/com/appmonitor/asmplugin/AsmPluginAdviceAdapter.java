package com.appmonitor.asmplugin;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;

import java.util.Map;

public class AsmPluginAdviceAdapter extends AdviceAdapter {

//    public static MethodVisitor makeTextMv = ;


    protected AsmPluginAdviceAdapter(int api, MethodVisitor mv, int access, String name, String desc) {
        super(api, mv, access, name, desc);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        if(owner.equals("android/webkit/WebView") && name.equals("setWebViewClient"))
            super.visitMethodInsn(184, "com/appmonitor/tools/WebViewTools", "setUpWithWebView", "(Landroid/webkit/WebView;Landroid/webkit/WebViewClient;)V", itf);
        else
            super.visitMethodInsn(opcode,owner,name,desc,itf);
    }
}
