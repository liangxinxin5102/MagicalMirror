package com.mingrisoft.mymirror.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.mingrisoft.mymirror.R;

/**
 * Created by Administrator on 2016/8/22.
 */
public class HintActivity extends AppCompatActivity {
    private TextView konw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //不显示状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hint);
        konw = (TextView) findViewById(R.id.i_know);
        konw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
