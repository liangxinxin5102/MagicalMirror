package com.mingrisoft.mymirror.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.mingrisoft.mymirror.R;
import com.mingrisoft.mymirror.service.ChangePictureFrameService;
import com.mingrisoft.mymirror.utils.AudioRecordManger;
import com.mingrisoft.mymirror.utils.SetBrightness;
import com.mingrisoft.mymirror.view.DrawView;
import com.mingrisoft.mymirror.view.FunctionView;
import com.mingrisoft.mymirror.view.PictureView;
import com.zys.brokenview.BrokenCallback;
import com.zys.brokenview.BrokenTouchListener;
import com.zys.brokenview.BrokenView;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback,SeekBar.OnSeekBarChangeListener,
        View.OnTouchListener,View.OnClickListener,FunctionView.onFunctionViewItemClickListener,DrawView.OnCaYiCaCompleteListener{
    private static final String TAG = MainActivity.class.getSimpleName(); //获得类名
    private SurfaceHolder holder;//显示相机拍摄的内容
    private SurfaceView surfaceView;//显示相机拍摄的内容
    private PictureView pictureView;//效果自定义View
    private FunctionView functionView;//标题栏
    private SeekBar seekBar;//控制焦距滑动条
    private ImageView add, minus;//控制焦距按钮
    private LinearLayout bottom;//调节焦距的按钮
    private ImageView save;//保存图片的按钮
    private ProgressDialog dialog;//弹窗
    private DrawView drawView;//覆盖层

    private boolean haveCamera;//是否有相机设备
    private Camera camera;//相机
    private int mCurrentCamIndex;//相机的指数
    private int ROTATE;//旋转值
    private int minFocus;//当前手机默认的焦距
    private int maxFocus;//当前手机的最大焦距
    private int everyFocus;//用于调整焦距
    private int nowFocus;//当前的焦距值

    private int frame_index;//相框类型
    private int[] frame_index_ID;//图片资源ID的数组
    private static final int PHOTO = 1;//相框请求值

    private int brightnessValue;//屏幕亮度值
    private boolean isAutoBrightness;//屏幕是否为自动调节
    private int SegmentLengh;//把亮度分为八段每段256的1/8

    private BrokenView brokenView;//碎屏控件
    private boolean isBroken;//开启或关闭碎屏功能
    private AudioRecordManger audioRecordManger;//调用话筒实现类
    private static final int RECORD = 2;//监听话筒
    private BrokenTouchListener brokenTouchListener;//碎屏的点击监听
    private MyBrokenCallback callBack;//BrokenView的生命周期
    private Paint brokenPaint;//碎屏裂缝的画笔
    private GestureDetector gestureDetector;//手势
    private MySimpleGestureListener mySimpleGestureListener;//手势自定义子类

    //晃一晃
    private Intent changePictureFrameService;//晃一晃服务
    private ChangePictureFrameReceiver changePictureFrameReceiver;//晃一晃广播接收器
    private SoundPool soundPool;//用于播放音频
    private Map<Integer,Integer> sound;//音频源
    private long exitTime;//第一次单机退出键的时间

    class MySimpleGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent e) {//开启碎屏效果
            super.onLongPress(e);
            Log.e("手势", "长按");
            isBroken = true;//碎屏
            brokenView.setEnable(isBroken);//碎屏组件可以
            pictureView.setOnTouchListener(brokenTouchListener);//设置碎屏长按监听
            hideView();//隐藏控件

            audioRecordManger.isGetVoiceRun = false;//设置话筒不启动
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();//初始化控件
        setViews();//设置控件属性

        //设置默认相框
        frame_index = 0;
        frame_index_ID = new int[]{R.mipmap.mag_0001, R.mipmap.mag_0003, R.mipmap.mag_0005,
                	        R.mipmap.mag_0006, R.mipmap.mag_0007, R.mipmap.mag_0008, R.mipmap.mag_0009,
                	        R.mipmap.mag_0011, R.mipmap.mag_0012, R.mipmap.mag_0014};
        getBrightnessFromWindow();//获取屏幕亮度的相关属性

        audioRecordManger = new AudioRecordManger(handler,RECORD);//实例化话筒实现类
        audioRecordManger.getNoiseLevel();//打开话筒监听声音

        mySimpleGestureListener = new MySimpleGestureListener();//创建手势识别监听对象
        gestureDetector = new GestureDetector(this,mySimpleGestureListener);//创建手势识别对象

        //打开服务
        changePictureFrameService = new Intent(this, ChangePictureFrameService.class);
        startService(changePictureFrameService);
        //初始化广播接收器
        changePictureFrameReceiver = new ChangePictureFrameReceiver();
        //设置音频相关属性
        loadSoundValues();
    }
    //获取布局文件中的组件
    private void initViews() {
        surfaceView = (SurfaceView) findViewById(R.id.surface); //获得布局文件中Id为surface的组件
        pictureView = (PictureView) findViewById(R.id.picture); //获得布局文件中picture的组件
        functionView = (FunctionView) findViewById(R.id.function);//获得布局文件中function组件
        seekBar = (SeekBar) findViewById(R.id.seekbar);   //获得布局文件中seekbar拖动条
        add = (ImageView) findViewById(R.id.add);     //获得布局文件中add焦距放大组件
        minus = (ImageView) findViewById(R.id.minus);  //获得布局文件中minus焦距缩小组件
        bottom = (LinearLayout) findViewById(R.id.bottom_bar); //获得布局文件中底部线性布局
        drawView = (DrawView) findViewById(R.id.draw_glasses);  //获得布局文件中擦屏组件
    }

    //检测手机是否有摄像头
    private boolean checkCameraHardware() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true; // 手机有摄像头
        } else {
            return false;// 手机没有摄像头
        }
    }
    //打开前置相机
    private Camera openFrontFacingCameraGingerbread() {
        int cameraCount;//摄像头个数
        Camera mCamera = null;//声明相机变量
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();//创建相机对象
        cameraCount = Camera.getNumberOfCameras();//获取摄像头个数
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);//获取相机对象
            //判断是否是前置摄像头
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    mCamera = Camera.open(camIdx);//打开摄像头
                    mCurrentCamIndex = camIdx;//设置前置摄像头ID
                } catch (RuntimeException e) {
                    Log.e(TAG, "相机打开失败:" + e.getLocalizedMessage());
                }
            }
        }
        return mCamera;
    }
    //调整摄像头方向
    private void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();//实例化相机对象
        Camera.getCameraInfo(cameraId, info);//获得相机对象
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();//获得旋转角度
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0://未旋转
                degrees = 0;
                break;
            case Surface.ROTATION_90://旋转90度
                degrees = 90;
                break;
            case Surface.ROTATION_180://旋转180度
                degrees = 180;
                break;
            case Surface.ROTATION_270://旋转270度
                degrees = 270;
                break;
        }
        int result = 0 ;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            //前置摄像头旋转算法
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            //后置摄像头旋转算法
            result = (info.orientation - degrees + 360) % 360;
        }
        ROTATE = result + 180;
        camera.setDisplayOrientation(result);//设置相机拍摄角度
    }
    //设置摄像头
    private void setCamera() {
        if (checkCameraHardware()) {
            camera = openFrontFacingCameraGingerbread();//打开前置摄像头
            //设置摄像头方向，调整摄像头旋转90度，成竖屏
            setCameraDisplayOrientation(this, mCurrentCamIndex, camera);   //设置相机的相关参数
            Camera.Parameters parameters = camera.getParameters();
            parameters.setPictureFormat(ImageFormat.JPEG);
            List<String> list = parameters.getSupportedFocusModes();
            for (String str : list) {
                Log.e(TAG, "支持的对焦的模式:"+ str);
            }
            //手机支持的图片尺寸集合
            List<Camera.Size> pictureList = parameters.getSupportedPictureSizes();
            //手机支持的预览尺寸集合
            List<Camera.Size> previewList = parameters.getSupportedPreviewSizes();
            //设置为当前使用手机的最大尺寸
            parameters.setPictureSize(pictureList.get(0).width, pictureList.get(0).height);
            //设置为当前使用手机的最大尺寸
            parameters.setPreviewSize(previewList.get(0).width, previewList.get(0).height);
            minFocus = parameters.getZoom();//最小焦距
            maxFocus = parameters.getMaxZoom();//最大焦距
            everyFocus = 1;
            nowFocus = minFocus;
            seekBar.setMax(maxFocus);
            Log.e(TAG, "当前镜头距离:"+ minFocus + "\t\t获取最大距离:"+ maxFocus);
            camera.setParameters(parameters);//设置相机参数
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e("surfaceCreated", "绘制开始");
        try {
            setCamera();//设置摄像头
            camera.setPreviewDisplay(holder);//设置预览显示的surfaceholder接口
            camera.startPreview();//开始预览
        } catch (IOException e) {
            camera.release();//相机释放
            camera = null;
            e.printStackTrace();
        }
    }
    //绘制改变
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e("surfaceChanged", "绘制改变");
        try {
            camera.stopPreview();
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e("surfaceDestroyed", "绘制结束");
        toRelease();//释放相机资源
    }
    /**
     * 释放照相机的资源
     */
    private void toRelease(){
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    private void setViews() {
        holder = surfaceView.getHolder();//获得对象
        holder.addCallback(this);//增加callback接口

        add.setOnTouchListener(this);//放大焦距
        minus.setOnTouchListener(this);//缩小焦距
        seekBar.setOnSeekBarChangeListener(this);//进度条监听
        functionView.setOnFunctionViewItemClickListener(this);//调用按钮点击监听事件
        pictureView.setOnTouchListener(this);//图片pictureView监听
        drawView.setOnCaYiCaCompleteListener(this);//画布监听
        setToBrokenTheView();//设置碎屏的相关属性

    }

    //设置焦距
    private void setZoomValues(int want) {
        Camera.Parameters parameters = camera.getParameters();//获取相机参数
        seekBar.setProgress(want);//设置进度
        parameters.setZoom(want);//设置焦距参数
        camera.setParameters(parameters);//设置相机参数-焦距
    }
    //获取焦距
    private int getZoomValues() {
        Camera.Parameters parameters = camera.getParameters();//获取相机参数
        int values = parameters.getZoom();//获取当前焦距
        return values;
    }
    //放大焦距
    private void addZoomValues() {
        if (nowFocus > maxFocus) { //当前焦距 大于 最大焦距
            Log.e(TAG, "大于maxFocus是不可能的！");
        } else if (nowFocus == maxFocus) {
        } else {
            setZoomValues(getZoomValues() + everyFocus);//设焦距
        }
    }
    //缩小焦距
    private void minusZoomValues() {
        if (nowFocus < 0) {
            Log.e(TAG, "小于0是不可能的！");
        } else if (nowFocus == 0) {
        } else {
            setZoomValues(getZoomValues() - everyFocus);//设焦距
        }
    }
    //焦距拖动条
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        //0~99  99级
        Camera.Parameters parameters = camera.getParameters();//获取相机参数
        nowFocus = progress; //进度值赋值给焦距
        parameters.setZoom(progress);//设置焦距
        camera.setParameters(parameters);//设置相机
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    //焦距调节的按钮事件
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.add:
                addZoomValues();//放大焦距
                break;
            case R.id.minus:
                minusZoomValues();//缩小焦距
                break;
            case R.id.picture://多点触控的操作
                //待添加手势识别事件函数
                gestureDetector.onTouchEvent(event);//手势识别事件
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void hint() {
        Intent intent = new Intent(this,HintActivity.class);
        startActivity(intent);
    }

    @Override
    public void choose() {
        Intent intent = new Intent(this, PhotoFrameActivity.class);
        startActivityForResult(intent, PHOTO);
        Toast.makeText(this, "选择!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);//获得选择相框界面的data
        Log.e(TAG, "返回值:" + resultCode + "\t\t请求值:" + requestCode);
        if (resultCode == RESULT_OK && requestCode == PHOTO) {
            int position = data.getIntExtra("POSITION", 0); //从返回数据获得POSITION值
            frame_index = position;
            Log.e(TAG, "返回的相框类别:"+ position);
            pictureView.setPhotoFrame(position);//传递POSITION值给pictureView
        }
    }
    private void setMyActivityBright(int brightnessValues) {
        SetBrightness.setBrightness(this, brightnessValues);//调用SetBrightness类方法设置亮度
        SetBrightness.saveBrightness(SetBrightness.getResolver(this), brightnessValues);//保存亮度
    }
    private void getAfterMySetBrightnessValues() {
        brightnessValue = SetBrightness.getScreenBrightness(this);//获得亮度
        Log.e(TAG, "当前手机屏幕亮度值:" + brightnessValue);
    }
    public void getBrightnessFromWindow() {
        //获得是否自动自动调节亮度
        isAutoBrightness = SetBrightness.isAutoBrightness(SetBrightness.getResolver(this));
        Log.e(TAG, "当前手机是否是自动调节屏幕亮度:"+ isAutoBrightness);
        if (isAutoBrightness) {//如果为true
            //关闭自动调节亮度
            SetBrightness.stopAutoBrightness(this);
            Log.e(TAG, "关闭了自动调节!");
            setMyActivityBright(255 / 2 + 1);
        }
        //亮度值0~256
        SegmentLengh = (255 / 2 + 1) / 4;//每32为一个区间
        getAfterMySetBrightnessValues();//获取设置后的亮度
    }
    private void downCurrentActivityBrightnessValues(){
        if (brightnessValue >0) {
            setMyActivityBright(brightnessValue - SegmentLengh);//减少亮度
        }
        getAfterMySetBrightnessValues();//获取设置后的屏幕亮度
    }
    @Override
    public void down() {
        downCurrentActivityBrightnessValues();//调用调低亮度方法
    }
    private void upCurrentActivityBrightnessValues(){
        if (brightnessValue <255) {
            if (brightnessValue + SegmentLengh >= 256){ //最大值255
                return;
            }
            setMyActivityBright(brightnessValue + SegmentLengh );//调高亮度
        }
        getAfterMySetBrightnessValues();//获取设置后的屏幕亮度
    }
    /**
     * 增加亮度，复写接口函数
     */
    @Override
    public void up() {
        upCurrentActivityBrightnessValues();
    }

    @Override
    public void complete() {
        showView();//显示控件
        audioRecordManger.getNoiseLevel();//获取声音级别
        drawView.setVisibility(View.GONE);//设置控件visibility属性
    }

    class MyBrokenCallback extends BrokenCallback {

        //按住控件
        @Override
        public void onStart(View v) {
            super.onStart(v);
            Log.e("Broken", "onStart");
        }
        //执行碎屏
        @Override
        public void onFalling(View v) {
            super.onFalling(v);
            Log.e("Broken", "onFalling");
            soundPool.play(sound.get(1), 1, 1, 0, 0, 1);//可加入碎屏声音
        }

        //碎屏结束
        @Override
        public void onFallingEnd(View v) {
            super.onFallingEnd(v);
            Log.e("Broken", "onFallingEnd");
            brokenView.reset();//控件重置
            pictureView.setOnTouchListener(MainActivity.this);//pictureView控件按键监听
            pictureView.setVisibility(View.VISIBLE);//控件可见
            isBroken = false;//碎屏停止
            Log.e("isEnable", isBroken + "");//打印日志
            brokenView.setEnable(isBroken);//设置碎屏停止
            audioRecordManger.getNoiseLevel();//话筒启动
            showView();//显示控件
        }
        //取消碎屏结束
        @Override
        public void onCancelEnd(View v) {
            super.onCancelEnd(v);
            Log.e("Broken", "onCancelEnd");
        }
    }

    private void hideView() {
        bottom.setVisibility(View.INVISIBLE);//底部焦距缩放不可见
        functionView.setVisibility(View.GONE);//顶部亮度、提示、选择相框不可见
    }
    private void showView() {
        pictureView.setImageBitmap(null);//设置图片为null
        bottom.setVisibility(View.VISIBLE);//底部焦距缩放可见
        functionView.setVisibility(View.VISIBLE);//顶部亮度、提示、选择相框可见
    }
    private void getSoundValues(double values){
        //话筒分贝大于70，屏幕起雾
        if (values >80){
            hideView();//隐藏无关控件

            drawView.setVisibility(View.VISIBLE);  //显示控件

            Animation animation = AnimationUtils.loadAnimation(this, R.anim.in_window);//设置透明度
            drawView.setAnimation(animation);

            audioRecordManger.isGetVoiceRun = false;//设置话筒停止运行
            Log.e("玻璃显示","执行");
        }
    }
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case RECORD://监测话筒
                    double soundValues = (double) msg.obj;
                    getSoundValues(soundValues);//获得话筒声音后，屏幕重绘起雾
                    break;
                default:
                    break;
            }
            return false;
        }
    });
    private void setToBrokenTheView() {
        brokenPaint = new Paint(); //新建碎屏画笔
        brokenPaint.setStrokeWidth(5); //设置空心线宽
        brokenPaint.setColor(Color.BLACK);//设置颜色
        brokenPaint.setAntiAlias(true);//抗锯齿
        brokenView = BrokenView.add2Window(this);//碎屏视图加入窗体
        //碎屏按键监听事件
        brokenTouchListener = new BrokenTouchListener.Builder(brokenView).setPaint(brokenPaint).setBreakDuration(2000).setFallDuration(5000).build();
        brokenView.setEnable(true);//默认不开启碎屏功能
        callBack = new MyBrokenCallback();//碎屏生命周期
        brokenView.setCallback(callBack);//执行碎屏
    }

    /**
     * 设置音频
     */
    private void loadSoundValues() {
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC,0);
        sound = new HashMap<>();
        sound.put(1, soundPool.load(this, R.raw.broken, 1));
    }
    //===================================== 摇一摇换相框 =====================================

    class ChangePictureFrameReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int cut = intent.getIntExtra("INDEX",0);
            int position = pictureView.getPhotoFrame();
            Log.e("接收到广播","开始取随机数:"+cut);
            if(cut == 1){
                int change_pic = getDifferentIndex(position);
                Log.e("返回位置",change_pic+"");
                pictureView.setPhotoFrame(change_pic);
            }
        }
    }

    /**
     * 获取要更改的值
     * @param pos
     * @return
     */
    private int getDifferentIndex(int pos){
        int index = (int) (Math.random()*9);
        Log.e("当前的相框的index",pos+"");
        Log.e("将要更换的相框的index", index + "");
        return verify(pos,index);
    }

    /**
     * 验证值是否符合要求
     * @param pos
     * @param in
     * @return
     */
    private int verify(int pos,int in) {
        if (pos == in){
            Log.e("index相同", "重新获取");
            return getDifferentIndex(pos);
        }else {
            Log.e("index不相同","获取成功");
            return in;
        }
    }

    /**
     * 管理生命周期
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("生命周期", "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        audioRecordManger.getNoiseLevel();
        startService(changePictureFrameService);
        registerReceiver(changePictureFrameReceiver, new IntentFilter("mrkj.mirror.mrkj.mirror.Change"));
        Log.e("生命周期", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("生命周期", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        audioRecordManger.isGetVoiceRun = false;
        stopService(changePictureFrameService);
        unregisterReceiver(changePictureFrameReceiver);
        Log.e("生命周期", "onStop");
    }

    /**
     * 按两次退出按钮退出程序
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            // System.currentTimeMillis()无论何时调用，肯定大于2000
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
