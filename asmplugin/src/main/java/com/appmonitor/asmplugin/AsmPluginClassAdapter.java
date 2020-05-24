package com.appmonitor.asmplugin;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public class AsmPluginClassAdapter extends ClassVisitor {
    public AsmPluginClassAdapter(ClassWriter cw){
        super(Opcodes.ASM5,cw);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        //在这里判断要监控的类方法
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        return new  AsmPluginAdviceAdapter(Opcodes.ASM5, mv, access, name, desc);
    }

}
