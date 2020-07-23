package com.example.motionlayoutdemo.motionLayout;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.motionlayoutdemo.R;
import com.example.motionlayoutdemo.fragment.FirstFragment;
import com.example.motionlayoutdemo.fragment.SecondFragment;

public class FragmentDemo01 extends AppCompatActivity implements View.OnClickListener, MotionLayout.TransitionListener {

    float lastProgress = 0f;
    Fragment fragment = null;
    float last = 0f;
    MotionLayout motionLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_demo01);
        motionLayout = findViewById(R.id.motion_root);
        motionLayout.setTransitionListener(this);
        if (savedInstanceState == null){
            fragment = FirstFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).commitNow();
        }

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onTransitionStarted(MotionLayout motionLayout, int i, int i1) {

    }

    @Override
    public void onTransitionChange(MotionLayout motionLayout, int beginState, int endState, float position) {
        if (position - lastProgress > 0){
            boolean atEnd = Math.abs(position - 1f) < 0.1f;
            if (atEnd && fragment instanceof FirstFragment){
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.animator.show,0);
                fragment = SecondFragment.newInstance();
                fragmentTransaction
                        //.setCustomAnimations(R.animator.show,0)直接切换 没有动画了
                        .replace(R.id.container,fragment)
                        .commitNow();
            }
        }else {
            boolean atStart = position < 0.9f;
            if (atStart && fragment instanceof SecondFragment){
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(0,R.animator.hide);
                fragment = FirstFragment.newInstance();
                fragmentTransaction.replace(R.id.container,fragment).commitNow();
            }
        }
        lastProgress = position;
    }

    @Override
    public void onTransitionCompleted(MotionLayout motionLayout, int i) {

    }

    @Override
    public void onTransitionTrigger(MotionLayout motionLayout, int i, boolean b, float v) {

    }
}
