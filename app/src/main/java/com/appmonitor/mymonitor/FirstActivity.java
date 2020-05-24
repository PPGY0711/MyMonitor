package com.appmonitor.mymonitor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

public class FirstActivity extends AppCompatActivity {

    private Object lockObj = new Object();

    private Handler uiHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
    }

    /**
     * 按钮Sencond Activity 事件处理
     */
    public void onClickToSecondActivity(View view)
    {
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
    }

    /**
     * 按钮Sencond Activity 事件处理
     */
    public void onClickToMainActivity(View view)
    {
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
    }

    /**
     * 死循环产生ANR错误
     * @param view
     */
    public void onClickUiThreadGenerateANRCrash(View view){
        while (true){

        }
    }

    public void onClickGenerateCatonCrash(View view){
        try{
            Thread.sleep(10000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

}
