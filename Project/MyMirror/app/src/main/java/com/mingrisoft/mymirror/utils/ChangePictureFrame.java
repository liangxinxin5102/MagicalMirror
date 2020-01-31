package com.mingrisoft.mymirror.utils;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 * Created by Administrator on 2016/5/11.
 */
public class ChangePictureFrame implements SensorEventListener{
    private static final int ROCKPOWER = 11;// 这是传感器系数
    private static final long time =1*1000;
    private long startTime;
    private long endTime;
    private Context context;

    public ChangePictureFrame(Context context) {
        this.context = context;
        startTime = System.currentTimeMillis() - time;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        // values[0]:X轴，values[1]：Y轴，values[2]：Z轴
        float[] values = event.values;
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            if ((Math.abs(values[0]) > ROCKPOWER || Math.abs(values[1]) > ROCKPOWER || Math.abs(values[2]) > ROCKPOWER)) {
                endTime = System.currentTimeMillis();
                if ((endTime - startTime)>time){
                    startTime = endTime;
                    Intent broadcast = new Intent("mrkj.mirror.mrkj.mirror.Change");
                    broadcast.putExtra("INDEX", 1);
                    context.sendBroadcast(broadcast);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
