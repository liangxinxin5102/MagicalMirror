package com.mingrisoft.mymirror.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mingrisoft.mymirror.R;

/**
 * Created by Administrator on 2016/8/20.
 */
public class FunctionView extends LinearLayout implements View.OnClickListener {
    private LayoutInflater mInflater;//声明寻找XML文件类
  	private ImageView hint,choose,down,up;//控件对象
  	public static final int HINT_ID = R.id.hint;//提示组件ID
  	public static final int CHOOSE_ID = R.id.choose;//选择相框组件ID
  	public static final int DOWN_ID = R.id.light_down;//减少亮度组件ID
  	public static final int UP_ID = R.id.light_up;//增加亮度组件ID

  /**
     * 回调接口，4个按钮
     */
  private onFunctionViewItemClickListener listener;
  public interface onFunctionViewItemClickListener{
	        void hint();//提示
	        void choose();//选择相框
	        void down();//减少亮度
	        void up();//增加亮度
	}
	 /**
     * 初始化构造函数
	 */
	public FunctionView(Context context) {
            this(context, null);
    }
    /**
     * 初始化构造函数
     */
    public FunctionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
  /**
     * 初始化构造函数
     */
    public FunctionView(Context context, AttributeSet attrs, int defStyleAttr) {
   	        super(context, attrs, defStyleAttr);
   	        mInflater = LayoutInflater.from(context);
   	        init();
    }
  /**
    * 初始化组件、导入布局
   */
  private void init() {
  View view  = mInflater.inflate(R.layout.view_function,this);//导入view_function布局文件
  hint = (ImageView) view.findViewById(HINT_ID);//获取提示按钮对象
  choose = (ImageView) view.findViewById(CHOOSE_ID);//获取选择相框按钮对象
  down = (ImageView) view.findViewById(DOWN_ID);//获取减少亮度按钮对象
  up = (ImageView) view.findViewById(UP_ID);//获取增加亮度按钮对象
  setView();//调用设置组件
}


  @Override
  public void onClick(View v) {
    if (listener!= null){ //监听不为空，表示有按钮按下
      switch (v.getId()){
        case HINT_ID: //提示按钮
          listener.hint();//执行监听函数，实现功能
          break;
        case CHOOSE_ID://选择相框按钮
          listener.choose();//执行监听函数，实现功能
          break;
        case DOWN_ID://减少亮度按钮
          listener.down();//执行监听函数，实现功能
          break;
        case UP_ID://增加亮度
          listener.up();//执行监听函数，实现功能
          break;
        default:
          break;
      }
    }
 }
  private void setView(){
    hint.setOnClickListener(this);//设置提示按钮按键监听事件
    choose.setOnClickListener(this);//设置选择相框按钮按键监听事件
    down.setOnClickListener(this);//设置减少亮度按钮按键监听事件
    up.setOnClickListener(this);//设置增加亮度按钮按键监听事件
  }

  public void setOnFunctionViewItemClickListener(onFunctionViewItemClickListener monFunctionViewItemClickListener){
    this.listener = monFunctionViewItemClickListener;//设置监听对象
  }

}
