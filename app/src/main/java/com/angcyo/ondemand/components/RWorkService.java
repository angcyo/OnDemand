package com.angcyo.ondemand.components;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;

import com.angcyo.ondemand.util.OkioUtil;
import com.angcyo.ondemand.util.Util;


/**
 * Created by angcyo on 15-09-12-012.
 */
public class RWorkService extends Service {

    private static RWorkService RWS;
    RWorkThread workThread;
    private Handler handler;
    private Runnable doTimeRunnable = new Runnable() {
        @Override
        public void run() {
            workThread.addTask(new RWorkThread.TaskRunnable() {
                @Override
                public void run() {
                    OkioUtil.writeToFile("心跳任务" + Util.callMethodAndLine());
                }
            });
            handler.postDelayed(doTimeRunnable, RConstant.HEART_TIME);
        }
    };

    public static synchronized void addTask(RWorkThread.TaskRunnable task) {
        if (RWS != null) {
            RWS.workThread.addTask(task);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        super.onCreate();
        RWS = this;
        workThread = new RWorkThread();
        workThread.start();//启动工作线程
        handler = new Handler(Looper.myLooper());
        if (RConstant.useHeart) {
            handler.postDelayed(doTimeRunnable, RConstant.HEART_TIME);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        workThread.interrupt();
        workThread.addTask(new RWorkThread.TaskRunnable() {//添加一个空任务,取消线程等待,中断线程
            @Override
            public void run() {
            }
        });
        super.onDestroy();
    }
}
