package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {
    private String data="Service Data";
    private boolean serviceRunning=false;
    //通过回调机制，将Service内部的变化传递到外部
    public interface DataCallback{
        void dataChanged(String str);
    }

    DataCallback dataCallback =null;

    public DataCallback getDataCallback() {
        return dataCallback;
    }

    public void setDataCallback(DataCallback dataCallback) {
        this.dataCallback = dataCallback;
    }

    public MyService() {
    }

    public class MyBinder extends Binder{
        MyService getService(){
            return MyService.this;
        }
        public void setData(String data){
            MyService.this.data=data;
        }
    }

    @Override
    public void onCreate(){
        super.onCreate();
        serviceRunning=true;
        new Thread(){
            @Override
            public void run(){
                int n=0;
                while (serviceRunning){
                    n++;
                    String str=n+data;
                    Log.d("Thread",str);
                    if(dataCallback!=null){
                        dataCallback.dataChanged(str);
                    }
                    try{
                        sleep(1000);
                    }catch (InterruptedException exp){
                        exp.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new MyBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
    @Override
    public void onDestroy() {
        serviceRunning=false;
        super.onDestroy();
    }
}
