package com.appmonitor.mymonitor.test;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.Date;

/**
 * 产生常见的10种Java RunTime Exception
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class TestPackage {
    //1.ArithmeticException 被除数不能为0的异常
    public static void TestArithmeticException(){
        int i = 1/0;
    }

    //2.NullPointerException 空指针异常
    public static void TestNullPointerException(){
        String s = "";
        s = null;
        System.out.println(s.length());
    }

    //3.ArrayIndexOutOfBoundsException 数组下标越界异常
    public static void TestArrayIndexOutOfBoundsException(){
        int[] arr = new int[2];
        arr[2] = 1;
    }

    //4.ClassCastException 类型转换异常
    public static void TestClassCastException(){
        TestA ta = new TestA();
        TestB tb = (TestB)ta;
    }

    //5.ArrayStoreException 尝试将一个不同的数据类型存放到数组中
    public static void TestArrayStoreException(){
        //将String[]类型的数组向上转型成为Object[]数组
        // 而真正在运行时，打印出来该数组的实际类型依旧是String[]数组
        // 之后向该数组中插入一个Object类型的元素时，抛出异常
        String[] array = {"a","b"};
        Object[] o = array;
        o[0] = new Object();
    }

    //6.IndexOutOfBoundsException 尝试从容器中获取一个没有的数据
    public static void TestIndexOutOfBoundsException(){
        ArrayList<Integer> intarr = new ArrayList<>();
        int i = intarr.get(0);
    }

    //7.DateTimeException LocalDateTime会抛出这个异常
    public static void TestDateTimeException(){
        //  日期时间格式书写错误,没有DD
        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-DD hh:mm:ss");
    }

    //8.NegativeArraySizeException 创建一个负数的数组
    public static void TestNegativeArraySizeException(){
        int[] arr = new int[-1];
    }

    //9.NumberFormatException 尝试将一个错误的字符串格式转换为数字 必须为数字格式的字符串
    public static void TestNumberFormatException(){
        String numString = "1 ";
        int i = Integer.parseInt(numString);
    }

    //10.IllegalArgumentException 代码格式参数传递错误
    public static void TestIllegalArgumentException(){

        //无法将给定的对象作为日期格式化，即因为传递了一个错误的参数
        Date day = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(day);

        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM");
        String format = dateFormat.format(date);
        System.out.println(format);
    }

    public static void main(String[] args) {
        try{
            TestArithmeticException();
        }catch (ArithmeticException e){
            System.out.println("[1]: ArithmeticException");
            e.printStackTrace();
        }

        try{
            TestNullPointerException();
        }catch (NullPointerException e){
            System.out.println("[2]: NullPointerException");
            e.printStackTrace();
        }

        try{
            TestArrayIndexOutOfBoundsException();
        }catch (ArrayIndexOutOfBoundsException e){
            System.out.println("[3]: ArrayIndexOutOfBoundsException");
            e.printStackTrace();
        }

        try{
            TestClassCastException();
        }catch (ClassCastException e){
            System.out.println("[4]: ClassCastException");
            e.printStackTrace();
        }

        try{
            TestArrayStoreException();
        }catch (ArrayStoreException e){
            System.out.println("[5]: ArrayStoreException");
            e.printStackTrace();
        }

        try{
            TestIndexOutOfBoundsException();
        }catch (IndexOutOfBoundsException e){
            System.out.println("[6]: IndexOutOfBoundsException");
            e.printStackTrace();
        }

        try{
            TestDateTimeException();
        }catch (DateTimeException e){
            System.out.println("[7]: DateTimeException");
            e.printStackTrace();
        }

        try{
            TestNegativeArraySizeException();
        }catch (NegativeArraySizeException e){
            System.out.println("[8]: NegativeArraySizeException");
            e.printStackTrace();
        }

        try{
            TestNumberFormatException();
        }catch (NumberFormatException e){
            System.out.println("[9]: NumberFormatException");
            e.printStackTrace();
        }

        try{
            TestIllegalArgumentException();
        }catch (IllegalArgumentException e){
            System.out.println("[10]: IllegalArgumentException");
            e.printStackTrace();
        }
    }
}

class TestA{
    int ia;
}

class TestB extends TestA{
    int ib;
}