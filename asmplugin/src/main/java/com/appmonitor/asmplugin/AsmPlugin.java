package com.appmonitor.asmplugin;

import com.android.build.gradle.AppExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.util.Collections;

public class AsmPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        AsmPluginExtension asmPluginExtension = project.getExtensions().create("MonitorAgent",AsmPluginExtension.class);
        AsmPluginConfig.getInstance().extension = asmPluginExtension;
        AppExtension appExtension = (AppExtension)project.getProperties().get("android");
        AsmPluginTransform asmPluginTransform = new AsmPluginTransform(project);
        System.out.println("Execute to CreateTransform");
        appExtension.registerTransform(asmPluginTransform, Collections.EMPTY_LIST);
        System.out.println("Execute to apply End");
    }
}
