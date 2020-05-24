package com.appmonitor.mymonitor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class FirstActivity extends AppCompatActivity implements View.OnClickListener{

    private Object lockObj = new Object();

    private Handler uiHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        Button anrBtn = findViewById(R.id.generateUiANRCrashBtn);
        Button catonBtn = findViewById(R.id.generateCatonCrash);
        anrBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.generateCatonCrash:
                try{
                    Thread.sleep(10000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                break;
            case R.id.generateUiANRCrashBtn:
                System.out.println("Activity is blocked!");
                try{
                    Thread.sleep(10000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                break;
            case R.id.JumpToMainActivity:
                try
                {
                    Intent intent = new Intent(this, MainActivity.class);//显示intent
                    startActivity(intent);
                }
                catch (Exception ex)
                {
                    // 显示异常信息
                    Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.JumpToSecondActivity:
                try
                {
                    Intent intent = new Intent(this, SecondActivity.class);//显示intent
                    startActivity(intent);
                }
                catch (Exception ex)
                {
                    // 显示异常信息
                    Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}
