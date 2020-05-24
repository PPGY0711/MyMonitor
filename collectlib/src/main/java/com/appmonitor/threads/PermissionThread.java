package com.appmonitor.threads;

import android.Manifest;
import android.app.Activity;
import android.widget.Toast;

import com.appmonitor.tools.InitApmTools;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import java.util.List;

public class PermissionThread implements Runnable{

    public Activity mAcitivity;

    public PermissionThread(Activity mAcitivity) {
        this.mAcitivity = mAcitivity;
    }

    @Override
    public void run() {
        Acp.getInstance(mAcitivity).request(new AcpOptions.Builder()
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE
                                , Manifest.permission.READ_PHONE_STATE
                                ,Manifest.permission.READ_EXTERNAL_STORAGE)
                        .build(),
                new AcpListener() {
                    @Override
                    public void onGranted() {
                        Toast.makeText(InitApmTools.getAppContext(), "OK! Apply permission successfully!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDenied(List<String> permissions) {
                        Toast.makeText(InitApmTools.getAppContext(), "Sorry! Apply permission denied!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}