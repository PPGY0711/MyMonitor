package com.appmonitor.caton;

public interface Collector {

    String[] getStackTraceInfo();

    void add(String stackTrace);
}
