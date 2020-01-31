package com.mingrisoft.mymirror.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.mingrisoft.mymirror.R;

/**
 * Created by Administrator on 2016/8/22.
 */
public class PhotoFrameActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener{

private GridView gridView;//相框表格
    private TextView textView;//返回键
    private int[] photo_styles;//图片的数组
    private String[] photo_name;//图片的名称数组
    private Bitmap[] bitmaps;//相框的集合
    private void initDatas() {
        //图片样式
        photo_styles = new int[]{R.mipmap.mag_0001,R.mipmap.mag_0003,R.mipmap.mag_0005,
                R.mipmap.mag_0006,R.mipmap.mag_0007,R.mipmap.mag_0008,R.mipmap.mag_0009,
                R.mipmap.mag_0011,R.mipmap.mag_0012, R.mipmap.mag_0014};
        //图片名称
        photo_name = new String[]{"Beautiful","Special","Wishes","Forever",
                "Journey","Love","River","Wonderful"," Birthday","Nice"};
        bitmaps = new Bitmap[photo_styles.length];//新建图片对象
        for (int i = 0; i <photo_styles.length; i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), photo_styles[i]); //获取图片
            bitmaps[i] = bitmap;
        }
    }

    class PhotoFrameAdapter extends BaseAdapter {
        /**
         * 获取图片数量
         */
        @Override
        public int getCount() {
            return photo_name.length;
        }
        /**
         * 获取图片子对象
         */
        @Override
        public Object getItem(int position) {
            return photo_name[position];
        }

        /**
         * 获取图片ID
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * 实现adapter的虚方法getView
         *
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null){
                holder = new ViewHolder();
                //获取布局文件的图片对象
                convertView = getLayoutInflater().inflate(R.layout.item_gridview,null);
                holder.image = (ImageView) convertView.findViewById(R.id.item_pic);//获取图片
                holder.txt = (TextView) convertView.findViewById(R.id.item_txt);//获取名称
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            setData(holder,position);//设置数据图片
            return convertView;
        }

        /**
         * 设置数据
         */
        private void setData(ViewHolder holder,int position) {
            holder.image.setImageBitmap(bitmaps[position]);//设置对象
            holder.txt.setText((photo_name[position]));  //设置名称
        }

        /**
         * view 组件类
         */
        class ViewHolder{
            ImageView image;
            TextView txt;
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_to_main:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * item点击事件
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        intent.putExtra("POSITION",position);
        setResult(RESULT_OK, intent);  //将选取的结果返回给主窗体
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_frame);  //获取布局

        //初始化控件
        textView = (TextView) findViewById(R.id.back_to_main);
        gridView = (GridView) findViewById(R.id.photo_frame_list);

        initDatas();//初始化数据
        textView.setOnClickListener(this); //设置控件

        PhotoFrameAdapter adapter = new PhotoFrameAdapter();//创建适配器
        gridView.setAdapter(adapter);//绑定适配器
        gridView.setOnItemClickListener(this);//执行子项点击监听事件
    }

}
