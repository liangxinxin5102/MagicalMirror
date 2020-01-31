package com.mingrisoft.mymirror.activity;

import android.content.Intent;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.mingrisoft.mymirror.R;



/**
 * Created by Administrator on 2016/8/20.
 */
public class GuideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);//设置布局
        handler.sendEmptyMessageDelayed(1, 3000);//传递what值为1的，空消息，延迟3秒
    }

    //消息处理，接收消息
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                //创建意图
                Intent intent = new Intent(GuideActivity.this, MainActivity.class);
                startActivity(intent);//跳转界面
                finish();//关闭界面
            }
            return false;
        }
    });

    //屏蔽返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return false;
    }
}