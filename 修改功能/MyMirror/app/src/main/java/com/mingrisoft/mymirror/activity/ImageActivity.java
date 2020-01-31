package com.mingrisoft.mymirror.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mingrisoft.mymirror.R;

public class ImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        TextView textView = (TextView) findViewById(R.id.back_to_main);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//退出当前界面
            }
        });
        ((ImageView) findViewById(R.id.show)).setImageURI(Uri.parse(getIntent().getStringExtra("path")));
    }
}
