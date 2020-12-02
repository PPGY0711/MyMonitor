package com.appmonitor.asmplugin;

import com.android.build.gradle.AppExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.util.Collections;

public class AsmPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
		//创建一个属于AsmPlugin的名为MonitorAgent的扩展，用于定义AsmPlugin的一些参数
        AsmPluginExtension asmPluginExtension = project.getExtensions().create("MonitorAgent",AsmPluginExtension.class);
        AsmPluginConfig.getInstance().extension = asmPluginExtension;
		//获取应用扩展信息
        AppExtension appExtension = (AppExtension)project.getProperties().get("android");
        AsmPluginTransform asmPluginTransform = new AsmPluginTransform(project);
        System.out.println("Execute to CreateTransform");
		//向Android App注册自定义的AsmPlugin插件
        appExtension.registerTransform(asmPluginTransform, Collections.EMPTY_LIST);
        System.out.println("Execute to apply End");
    }
}
