package com.example.motionlayoutdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.motionlayoutdemo.ObjectAnimation.ObjectDemo01;
import com.example.motionlayoutdemo.motionLayout.Demo01;
import com.example.motionlayoutdemo.motionLayout.Demo02;
import com.example.motionlayoutdemo.motionLayout.Demo03;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void demo01(View view) {
        startActivity(new Intent(this, Demo01.class));
    }

    public void demo02(View view) {
        startActivity(new Intent(this, Demo02.class));
    }

    public void demo03(View view) {
        startActivity(new Intent(this, Demo03.class));
    }


    public void obj01(View view) {
        startActivity(new Intent(this, ObjectDemo01.class));
    }
}