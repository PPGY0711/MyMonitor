package com.appmonitor.asmplugin;

public class AsmPluginConfig {
    private  static AsmPluginConfig sInstance = new AsmPluginConfig();

    public AsmPluginExtension extension;

    public static AsmPluginConfig getInstance(){
        return sInstance;
    }
}
