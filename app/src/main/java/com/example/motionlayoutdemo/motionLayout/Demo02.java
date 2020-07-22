package com.example.motionlayoutdemo.motionLayout;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.motionlayoutdemo.R;

/**
 * 关键帧的设置 画一个问号
 */
public class Demo02 extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo02);
        getSupportActionBar().setTitle("MotionLayout");
    }
}
