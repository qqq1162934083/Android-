package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity{
    Intent intent=null;
    MyServiceConn myServiceConn=null;
    TextView tv_out=null;
    MyService.MyBinder binder=null;
    EditText et_data=null;
    Button btn_startService=null;
    Button btn_stopService=null;
    Button btn_bindService=null;
    Button btn_unbindService=null;
    Button btn_syncData=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_data=findViewById(R.id.editText);
        tv_out=findViewById(R.id.textView);
        btn_startService=findViewById(R.id.btn_start_service);
        btn_stopService=findViewById(R.id.btn_stop_service);
        btn_bindService=findViewById(R.id.btn_bind_service);
        btn_unbindService=findViewById(R.id.btn_unbind_service);
        btn_syncData=findViewById(R.id.btn_sync_data);
        intent=new Intent(this,MyService.class);
        myServiceConn=new MyServiceConn();

        //设置监听
        btn_startService.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                startService(intent);
            }
        });
        btn_stopService.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                stopService(intent);
            }
        });
        btn_bindService.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                bindService(intent,myServiceConn, Context.BIND_AUTO_CREATE);
            }
        });
        btn_unbindService.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                unbindService(myServiceConn);
            }
        });
        btn_syncData.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                //注意：先绑定才能同步数据
                if(binder!=null){
                    binder.setData(et_data.getText().toString());
                }
            }
        });
    }

    class MyServiceConn implements ServiceConnection{
        //服务被绑定成功之后执行
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            //IBinder service为onBind方法返回的Service实例
            binder=(MyService.MyBinder)service;
            binder.getService().setDataCallback(new MyService.DataCallback(){
               //执行回调函数
               @Override
                public void dataChanged(String str){
                   Message msg=new Message();
                   Bundle bundle=new Bundle();
                   bundle.putString("str",str);
                   msg.setData(bundle);
                   //发送通知
                   handler.sendMessage(msg);
               }
            });
        }

        Handler handler=new Handler(){
            @Override
            public void handleMessage(android.os.Message msg){
                //在handler中更新UI
                tv_out.setText(msg.getData().getString("str"));
            }
        };
        //服务崩溃或者被杀掉时执行
        @Override
        public void onServiceDisconnected(ComponentName name){
            binder=null;
        }
    }
}
