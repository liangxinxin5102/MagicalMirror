package com.mingrisoft.mymirror.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

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
        return drawBitmap(backBitmap,foreBitmap);
    }

    /**
     * 为背景图片添加滤镜效果后再合成图片
     * @param backBitmap
     * @param foreBitmap
     * @param rotate
     * @param gpuImage
     * @param filter
     * @return
     */
    public static Bitmap composeBitmapFilter(Bitmap backBitmap, Bitmap foreBitmap, int rotate, GPUImage gpuImage, GPUImageFilter filter){
        backBitmap = composeBitmapRotate(backBitmap,rotate);
        // 使用GPUImage处理图像
        gpuImage.setImage(backBitmap);
        gpuImage.setFilter(filter);
        backBitmap = gpuImage.getBitmapWithFilterApplied();
        return drawBitmap(backBitmap,foreBitmap);
    }

    /**
     * 将背景图和前景图合成一张图片
     * @param backBitmap
     * @param foreBitmap
     * @return
     */
    private static Bitmap drawBitmap(Bitmap backBitmap,Bitmap foreBitmap){
        Canvas canvas = new Canvas(backBitmap);//创建画布
        RectF rectF =  new RectF(0,0,backBitmap.getWidth(),backBitmap.getHeight());//绘制区域
        canvas.drawBitmap(foreBitmap,null,rectF,null);//将前景图覆盖给背景图
        return backBitmap;
    }
}
