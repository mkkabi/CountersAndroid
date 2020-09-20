package com.mkkabi.countersandyj.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import static com.mkkabi.countersandyj.utils.Constants.DUBUG;


public class LoggerToaster {
    private static boolean debugOn = false;

    public static void printD(@NonNull Context context, String tag, String message){
        Log.d(DUBUG, message+"\n"+context.getClass().getName());
        showToast(context,message+" ");
    }

    // prints only to console
    public static void printC(String message, Class clazz){
        Log.d(DUBUG, clazz.getName()+"\n"+message);
    }

    public static void printV(Context context, String message, Throwable tw){
        Log.v(DUBUG, message, tw);
        showToast(context, message);
    }

    private static void showToast(Context context, String message){
        if(debugOn)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
