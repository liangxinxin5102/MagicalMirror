package com.mingrisoft.mymirror.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;

/**
 * 作者： LYJ
 * 功能： 图片工具类
 * 创建日期： 2017/4/18
 */

public class BitmapUtils {
    /**
     * 旋转图片
     * @param bitmap
     * @param rotate
     * @return
     */
    public static Bitmap composeBitmapRotate(Bitmap bitmap,int rotate){
        Matrix matrix = new Matrix();//获取矩阵对象用于图形变换
        matrix.setRotate(rotate);//设置旋转角度
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bitmap;
    }

    /**
     * 将两个图片合成一个图片
     * @param backBitmap
     * @param foreBitmap
     * @param rotate
     * @return
     */
    public static Bitmap composeBitmapLayer(Bitmap backBitmap,Bitmap foreBitmap,int rotate){
        backBitmap = composeBitmapRotate(backBitmap,rotate);
        Canvas canvas = new Canvas(backBitmap);//创建画布
        RectF rectF =  new RectF(0,0,backBitmap.getWidth(),backBitmap.getHeight());//绘制区域
        canvas.drawBitmap(foreBitmap,null,rectF,null);//将前景图覆盖给背景图
        return backBitmap;
    }
}
