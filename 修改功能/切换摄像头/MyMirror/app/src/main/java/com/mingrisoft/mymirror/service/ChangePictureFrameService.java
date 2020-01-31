package com.mingrisoft.mymirror.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.mingrisoft.mymirror.utils.ChangePictureFrame;


/**
 * 更改相框的服务
 */
public class ChangePictureFrameService extends Service {
    private SensorManager mSensorManager;// 传感器服务
    private PowerManager mPowerManager;// 电源管理服务
    private PowerManager.WakeLock mWakeLock;// 屏幕灯
    private ChangePictureFrame changePictureFrame;//传感器功能实现类
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("服务开启了", "程序开始");
        changePictureFrame = new ChangePictureFrame(this);
        // 获取传感器的服务，初始化传感器
        mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        // 注册传感器，注册监听器
        mSensorManager.registerListener(changePictureFrame,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        // 电源管理服务
        mPowerManager = (PowerManager) this
                .getSystemService(Context.POWER_SERVICE);
        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP, "S");
        mWakeLock.acquire();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (changePictureFrame != null) {
            mSensorManager.unregisterListener(changePictureFrame);
        }
        if (mWakeLock != null) {
            mWakeLock.release();
        }
        Log.e("服务关闭了","程序结束");
    }
}
