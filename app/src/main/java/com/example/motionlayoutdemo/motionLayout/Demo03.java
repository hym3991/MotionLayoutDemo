package com.example.motionlayoutdemo.motionLayout;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.motionlayoutdemo.R;

/**
 * 最简单的动画设置
 * OnSwipe 与 OnClick的区别
 */
public class Demo03 extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo03);
        getSupportActionBar().setTitle("MotionLayout");
    }
}
