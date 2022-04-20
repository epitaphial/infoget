package com.example.infoget;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class InfoActivity extends AppCompatActivity {
    // HARDWARE
    // get device wide
    public static int getDeviceWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    // get device height
    public static int getDeviceHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    // get Android version
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    // get system language
    public static String getSystemLanguage() {
        return Locale.getDefault().getLanguage();
    }

    // get system model
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    // get device brand
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    // get device fingerprint
    public static String getDeviceFingerprint() {
        return android.os.Build.FINGERPRINT;
    }

    // get device display
    public static String getDeviceDisplay() {
        return Build.DISPLAY;
    }

    // get SMS
    public static ArrayList<String> getSMS(Context context){
        ArrayList<String> arrayList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        //获取短信表的路径
        Uri uri = Uri.parse("content://sms");
        //设置要查询的列名
        String[] line = {"address", "date", "body"};
        //各个参数的意思，路径、列名、条件、条件参数、排序
        Cursor cursor = contentResolver.query(uri, line, null, null, null);
        //下面就跟操作普通数据库一样了
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String address = cursor.getString(cursor.getColumnIndex("address"));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex("date"));
                @SuppressLint("Range") String body = cursor.getString(cursor.getColumnIndex("body"));
                arrayList.add("address:" + address + "\ndate:" + date + "\nbody:" + body);
            }
            cursor.close();
        }
        return arrayList;
    }

    // TELEPHONE
    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("HardwareIds")
    public static String getPhoneNumber(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.READ_PHONE_NUMBERS},1);
            return "GET PERMISSION FIRST";
        }else {
            return telephonyManager.getLine1Number();
        }
    }

    // get installed app, true for user app, false for system app
    public static List<PackageInfo> getInstalledApps(Context context, boolean flag){
        PackageManager packageManager = context.getPackageManager();
        @SuppressLint("QueryPermissionsNeeded") List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(0);
        List<PackageInfo> sysApps = new ArrayList<>();
        List<PackageInfo> userApps = new ArrayList<>();
        for (int i = 0; i < packageInfoList.size(); i++) {
            PackageInfo packageInfo = packageInfoList.get(i);
            // >0 stand for system app
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                userApps.add(packageInfo);
            }else {
                sysApps.add(packageInfo);
            }
        }
        return flag?userApps:sysApps;
    }

    // get running Apps
    public static List<ActivityManager.RunningAppProcessInfo> getRunningApps(Context context){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        @SuppressLint("QueryPermissionsNeeded") List<ActivityManager.RunningAppProcessInfo> packageInfoList = activityManager.getRunningAppProcesses();
        return packageInfoList;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Intent intent = getIntent();
        int item = Integer.parseInt(intent.getStringExtra("item"));
        switch (item){
            // hardware information
            case 0:{
                ArrayList<String> arrayList = new ArrayList<>();
                String deviceResolution = "Resolution:" + getDeviceWidth(this)+"x"+getDeviceHeight(this);
                arrayList.add(deviceResolution);
                String androidVersion = "Android Version:" + getSystemVersion();
                arrayList.add(androidVersion);
                String systemLang = "System Language:" + getSystemLanguage();
                arrayList.add(systemLang);
                String systemModel = "System Model:" + getSystemModel();
                arrayList.add(systemModel);
                String deviceBrand = "Device Brand:" + getDeviceBrand();
                arrayList.add(deviceBrand);
                String deviceFingerprint = "Device FingerPrint:" + getDeviceFingerprint();
                arrayList.add(deviceFingerprint);
                String deviceDisplay = "Device Display:" + getDeviceDisplay();
                arrayList.add(deviceDisplay);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        InfoActivity.this, android.R.layout.simple_list_item_1, arrayList);
                ((ListView) findViewById(R.id.info_list)).setAdapter(adapter);
                break;
            }
            // telephone information
            case 1:{
                ArrayList<String> arrayList = new ArrayList<>();
                String phoneNumber = "Phone Number:" + getPhoneNumber(this);
                arrayList.add(phoneNumber);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        InfoActivity.this, android.R.layout.simple_list_item_1, arrayList);
                ((ListView) findViewById(R.id.info_list)).setAdapter(adapter);
                arrayList.addAll(getSMS(this));
                break;
            }
            // apps information, click package name get more information.
            case 2:{
                ArrayList<String> arrayList = new ArrayList<>();
                List<PackageInfo> packageInfoList = getInstalledApps(this,false);
                for (PackageInfo pi :
                        packageInfoList) {
                    arrayList.add(pi.packageName);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        InfoActivity.this, android.R.layout.simple_list_item_1, arrayList);
                ((ListView) findViewById(R.id.info_list)).setAdapter(adapter);
                break;
            }
            // activities
            case 3:{
                ArrayList<String> arrayList = new ArrayList<>();
                List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList = getRunningApps(this);
                for (ActivityManager.RunningAppProcessInfo ra :
                        runningAppProcessInfoList) {
                    arrayList.add(ra.processName);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        InfoActivity.this, android.R.layout.simple_list_item_1, arrayList);
                ((ListView) findViewById(R.id.info_list)).setAdapter(adapter);
                break;
            }
        }
    }
}