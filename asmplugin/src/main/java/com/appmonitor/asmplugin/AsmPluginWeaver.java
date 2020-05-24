package com.appmonitor.asmplugin;

import com.quinn.hunter.transform.asm.BaseWeaver;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;


public class AsmPluginWeaver extends BaseWeaver {

    @Override
    public boolean isWeavableClass(String fullQualifiedClassName) {
        return fullQualifiedClassName.contains(AsmPluginConfig.getInstance().extension.applicationPackage);
    }

    @Override
    protected ClassVisitor wrapClassWriter(ClassWriter classWriter) {
        return new AsmPluginClassAdapter(classWriter);
    }

}
