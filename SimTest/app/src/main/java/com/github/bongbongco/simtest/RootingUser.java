package com.github.bongbongco.simtest;

import android.os.Environment;

import java.io.File;

/**
 * Created by seungyonglee on 2016. 8. 2..
 */
public class RootingUser extends User implements UnauthorizeUser {

    public static final  String ROOT_PATH = Environment.getExternalStorageDirectory() + "";

    boolean rootFlag = false;
    String[] places = {"/system/bin/su"
            , "/system/xbin/su"
            , "/system/app/superuser.apk"
            , "/data/data/com.noshufou.android.su"
            , "/sbin/su"
            , "/system/su"
            , "/system/sbin/su"};

    public boolean Access() {

        if(SuTest(rootFlag)||FileTest(rootFlag))
        {
            return true;
        }
        return false;
    }

    public boolean SuTest(boolean rootFlag) {
        try {
            Runtime.getRuntime().exec("su"); // 루트권한을 요청하여 확인
            rootFlag = true;
        }
        catch (Exception e) {
            rootFlag = false;
        }
        return rootFlag;
    }

    public boolean FileTest(boolean rootFlag) {
        for(String place : places) {
            if (new File(place).exists()){
                rootFlag = true;
                break;
            }
        }
        return rootFlag;
    }
}
