package com.example.motionlayoutdemo.ObjectAnimation;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Space;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.example.motionlayoutdemo.R;
import com.example.motionlayoutdemo.motionLayout.Demo01;

public class ObjectDemo01 extends AppCompatActivity {
    Space space;
    LinearLayoutCompat layoutCompat;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obj_demo01);
        space = (Space)findViewById(R.id.space);
        layoutCompat = (LinearLayoutCompat)findViewById(R.id.ll);
    }

    public void object01(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(layoutCompat,"translationX",0f, -300f);
        animator.start();

    }
}
