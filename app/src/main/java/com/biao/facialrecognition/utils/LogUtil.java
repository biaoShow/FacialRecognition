package com.biao.facialrecognition.utils;

import android.util.Log;

/**
 * 日志封装
 * Created by benxiang on 2019/5/24.
 */

public class LogUtil {
    private static String className;//类名
    private static String methodName;//方法名
    private static int lineNumber;//行数

    /**
     * 获取文件名、方法名、所在行数
     *
     * @param sElements
     */
    private static void getMethodNames(StackTraceElement[] sElements) {
        className = sElements[1].getFileName();
        methodName = sElements[1].getMethodName();
        lineNumber = sElements[1].getLineNumber();
    }

    /**
     * 拼接TAG内容
     *
     * @return
     */
    private static String createTAG() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(className);
        buffer.append("(").append(className).append(":").append(lineNumber).append(")");
        return buffer.toString();
    }

    public static void e(String message) {
        getMethodNames(new Throwable().getStackTrace());
        Log.e(createTAG(), message);
    }

    public static void i(String message) {
        getMethodNames(new Throwable().getStackTrace());
        Log.i(createTAG(), message);
    }

    public static void d(String message) {
        getMethodNames(new Throwable().getStackTrace());
        Log.d(createTAG(), message);
    }

    public static void v(String message) {
        getMethodNames(new Throwable().getStackTrace());
        Log.v(createTAG(), message);
    }

    public static void w(String message) {
        getMethodNames(new Throwable().getStackTrace());
        Log.w(createTAG(), message);
    }
}
