package com.mingrisoft.mymirror.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.mingrisoft.mymirror.R;

/**
 * Created by Administrator on 2016/8/22.
 */
public class  DrawView extends View {
		private Canvas mCanvas;// 画布
		private Path mPath;// 路径
		private Paint mPaint;// 画笔
		private float moveX, moveY;//移动坐标
		private Bitmap mBitmap;//图片变量
		private Bitmap bitmap;//图片变量
		private volatile boolean mComplete = false;// 判断遮盖层区域是否消除达到阈值
	    /**
	     * 构造函数
	     */
	    public DrawView(Context context, AttributeSet attrs, int defStyleAttr) {
	        super(context, attrs, defStyleAttr);
			init();//初始化
	    }

	    public DrawView(Context context, AttributeSet attrs) {
	        this(context, attrs, 0);
	    }

	    public DrawView(Context context) {
	        this(context, null);
	    }

	public interface OnCaYiCaCompleteListener //声明刮刮卡特效接口函数
	{
		void complete();
	}
	private OnCaYiCaCompleteListener mListener;//声明刮刮卡接口
	public void setOnCaYiCaCompleteListener(
			OnCaYiCaCompleteListener mListener)
	{
		this.mListener = mListener;//接口回调
	}
	private void init() {
		bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.glasses).copy(Bitmap.Config.ARGB_8888,true);//初始化图片加载
		mPaint = new Paint();  //新建画笔
		mPaint.setColor(Color.RED); //设置画笔颜色
		mPaint.setStyle(Paint.Style.STROKE);//设置画笔样式
		mPaint.setStrokeJoin(Paint.Join.ROUND);//设置结合处样子
		mPaint.setStrokeCap(Paint.Cap.ROUND);//设置画笔笔触风格
		mPaint.setDither(true);// 设置递色
		mPaint.setAntiAlias(true);//设置抗锯齿
		mPaint.setStrokeWidth(100);//设置空心线宽
		mPath = new Path();//创建新路径
	}

	/**
	 * 变量重置
	 */
	public void setEndValues(){
		moveX = 0;          //坐标清零
		moveY = 0;          //坐标清零
		mPath.reset();           //路径重置
		mComplete = false; //恢复未擦除状态
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.TRANSPARENT);
		if (!mComplete)    //如果还未擦除完成
		{
			mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));   //设定目标图模式
			canvas.drawBitmap(mBitmap, 0, 0, null);//画图
			mCanvas.drawPath(mPath, mPaint);    //画布 路径
			canvas.drawBitmap(mBitmap, 0, 0, null); //画布图片
		}

		//如果擦除干净，则进行资源释放操作
		if (mComplete)
		{
			if (mListener != null)
			{
				mListener.complete();  //监听结束
				setEndValues();
			}
		}
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);//宽度
		int height = MeasureSpec.getSize(heightMeasureSpec);//高度
		mBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);//图片
		bitmap = Bitmap.createScaledBitmap(bitmap,width,height,true);//基于原bitmap，创建一个新的宽、高的bitmap
		mCanvas = new Canvas(mBitmap); //画布实例化
		mCanvas.drawColor(Color.TRANSPARENT);//设置颜色
		mCanvas.drawBitmap(bitmap,0,0,null);//画布重绘bitmap
	}
	private Runnable mRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			int w = getWidth();
			int h = getHeight();

			float wipeArea = 0;
			float totalArea = w * h;
			Bitmap bitmap = mBitmap;
			int[] mPixels = new int[w * h];

			// 获得Bitmap上所有的像素信息
			bitmap.getPixels(mPixels, 0, w, 0, 0, w, h);

			for (int i = 0; i < w; i++)
			{
				for (int j = 0; j < h; j++)
				{
					int index = i + j * w;
					if (mPixels[index] == 0)
					{
						wipeArea++;
					}
				}
			}

			if (wipeArea > 0 && totalArea > 0)
			{
				int percent = (int) (wipeArea * 100 / totalArea);

				Log.e("TAG", percent + "");

				if (percent > 50)
				{
					// 清除掉图层区域
					mComplete = true;
					postInvalidate();

				}

			}
		}
	};
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
//			mPath.reset();
				moveX = x;
				moveY = y;
				mPath.moveTo(moveX, moveY);
				break;
			case MotionEvent.ACTION_MOVE:
				int dx = (int) Math.abs(moveX - x);
				int dy = (int) Math.abs(moveY - y);
				if (dx > 1 || dy > 1) {
					mPath.quadTo(x, y, (moveX + x) / 2, (moveY + y) / 2);
				}
				moveX = x;
				moveY = y;
				break;
			case MotionEvent.ACTION_UP:
				if (!mComplete){
					new Thread(mRunnable).start();
				}
				break;

			default:
				break;
		}
		if (!mComplete) {
			invalidate();
		}
		return true;
	}

}
