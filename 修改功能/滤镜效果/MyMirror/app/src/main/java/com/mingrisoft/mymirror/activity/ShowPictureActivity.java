package com.mingrisoft.mymirror.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.mingrisoft.mymirror.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ShowPictureActivity extends AppCompatActivity {
    private TextView textView;//返回键
    private List<String> pathList = new ArrayList<>();
    private GridView picList;//图片列表
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_picture);
        //初始化控件
        textView = (TextView) findViewById(R.id.back_to_main);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//退出当前界面
            }
        });
        File file = new File(Environment.getExternalStorageDirectory(), getString(R.string.app_name));
        String[] fileArray = file.list();//获取文件名称数组
        for (String path : fileArray) {//生成图片文件的Uri数据集合
            pathList.add(Environment.getExternalStorageDirectory() + File.separator +
                    getString(R.string.app_name) + File.separator + path);//凭借路径并转换
        }
        picList = (GridView) findViewById(R.id.image_list);
        picList.setAdapter(new TheAdapter());//绑定适配器
        picList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(ShowPictureActivity.this,ImageActivity.class)
                    .putExtra("path",pathList.get(position)));
            }
        });
    }


    class TheAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return pathList.size();
        }

        @Override
        public Object getItem(int position) {
            return pathList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ShowPictureActivity.TheAdapter.ViewHolder holder;
            if (convertView == null){
                holder = new ShowPictureActivity.TheAdapter.ViewHolder();
                //获取布局文件的图片对象
                convertView = getLayoutInflater().inflate(R.layout.item_show,null);
                holder.image = (ImageView) convertView.findViewById(R.id.item_img);//获取图片
                convertView.setTag(holder);
            }else {
                holder = (ShowPictureActivity.TheAdapter.ViewHolder) convertView.getTag();
            }
            holder.image.setImageURI(Uri.parse(pathList.get(position)));
            return convertView;
        }
        /**
         * 复用类
         */
        class ViewHolder{
            ImageView image;
        }
    }
}
