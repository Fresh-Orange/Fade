package com.sysu.pro.fade;


import android.os.Build;

/**
 * Created by yellow on 2018/5/4.
 */

public final class Permission {

    public static final String[] CALENDAR;   // 读写日历。
    public static final String[] CAMERA;     // 相机。
    public static final String[] CONTACTS;   // 读写联系人。
    public static final String[] LOCATION;   // 读位置信息。
    public static final String[] MICROPHONE; // 使用麦克风。
    public static final String[] PHONE;      // 读电话状态、打电话、读写电话记录。
    public static final String[] SENSORS;    // 传感器。
    public static final String[] SMS;        // 读写短信、收发短信。
    public static final String[] STORAGE;    // 读写存储卡。

    static {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            CALENDAR = new String[]{};
            CAMERA = new String[]{};
            CONTACTS = new String[]{};
            LOCATION = new String[]{};
            MICROPHONE = new String[]{};
            PHONE = new String[]{};
            SENSORS = new String[]{};
            SMS = new String[]{};
            STORAGE = new String[]{};
        } else {
            CALENDAR = new String[]{
                    android.Manifest.permission.READ_CALENDAR,
                    android.Manifest.permission.WRITE_CALENDAR};

            CAMERA = new String[]{
                    android.Manifest.permission.CAMERA};

            CONTACTS = new String[]{
                    android.Manifest.permission.READ_CONTACTS,
                    android.Manifest.permission.WRITE_CONTACTS,
                    android.Manifest.permission.GET_ACCOUNTS};

            LOCATION = new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION};

            MICROPHONE = new String[]{
                    android.Manifest.permission.RECORD_AUDIO};

            PHONE = new String[]{
                    android.Manifest.permission.READ_PHONE_STATE,
                    android.Manifest.permission.CALL_PHONE,
                    android.Manifest.permission.READ_CALL_LOG,
                    android.Manifest.permission.WRITE_CALL_LOG,
                    android.Manifest.permission.USE_SIP,
                    android.Manifest.permission.PROCESS_OUTGOING_CALLS};

            SENSORS = new String[]{
                    android.Manifest.permission.BODY_SENSORS};

            SMS = new String[]{
                    android.Manifest.permission.SEND_SMS,
                    android.Manifest.permission.RECEIVE_SMS,
                    android.Manifest.permission.READ_SMS,
                    android.Manifest.permission.RECEIVE_WAP_PUSH,
                    android.Manifest.permission.RECEIVE_MMS};

            STORAGE = new String[]{
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        }
    }

}
