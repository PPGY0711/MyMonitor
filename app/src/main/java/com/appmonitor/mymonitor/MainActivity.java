package com.appmonitor.mymonitor;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.appmonitor.mymonitor.test.TestPackage;
import com.appmonitor.tools.InitApmTools;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitApmTools.initPermission(this).run();
    }

    /**
     * 按钮Sencond Activity 事件处理
     */
    public void onClickToFirstActivity(View view)
    {
        try
        {
            Intent intent = new Intent(this, FirstActivity.class);//显示intent
            startActivity(intent);
        }
        catch (Exception ex)
        {
            // 显示异常信息
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onClickTestArithmeticException(View view){
        TestPackage.TestArithmeticException();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onClickTestNullPointerException(View view){
        TestPackage.TestNullPointerException();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onClickTestArrayIndexOutOfBoundsException(View view){
        TestPackage.TestArrayIndexOutOfBoundsException();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onClickTestClassCastException(View view){
        TestPackage.TestClassCastException();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onClickTestArrayStoreException(View view){
        TestPackage.TestArrayStoreException();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onClickTestIndexOutOfBoundsException(View view){
        TestPackage.TestIndexOutOfBoundsException();
    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public void onClickTestDateTimeException(View view){
//        TestPackage.TestDateTimeException();
//    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onClickTestNegativeArraySizeException(View view){
        TestPackage.TestNegativeArraySizeException();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onClickTestNumberFormatException(View view){
        TestPackage.TestNumberFormatException();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onClickTestIllegalArgumentException(View view){
        TestPackage.TestIllegalArgumentException();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void main(String[] args) {
        TestPackage.TestNullPointerException();
    }
}
