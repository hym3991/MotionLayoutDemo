package com.example.motionlayoutdemo.animation;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.motionlayoutdemo.R;

/**
 * 补间动画
 */
public class TweenDemo extends AppCompatActivity {
    TextView textViewTween;
    TextView textViewJava;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tween_demo);
        getSupportActionBar().setTitle("补间动画");
        textViewTween = (TextView)findViewById(R.id.tween_tv);
        textViewJava = (TextView)findViewById(R.id.tween_java_tv);
    }

    public void tweenAnim(View view) {
        /**
         * 补间动画就是指开发者指定动画的开始、动画的结束的"关键帧"，而动画变化的"中间帧"由系统计算，并补齐。
         * 补间动画有四种：
         * 淡入淡出： alpha
         * 位移：translate
         * 缩放：scale
         * 旋转： rotate
         */

        @SuppressLint("ResourceType")
        final Animation anim = AnimationUtils.loadAnimation(this,R.animator.tween_demo_anim);
        //设置动画结束后保留结束状态
        anim.setFillAfter(true);
        textViewTween.startAnimation(anim);
    }


    public void tweenAnimJava(View view) {
        // 组合动画设置
        AnimationSet setAnimation = new AnimationSet(true);
        setAnimation.setRepeatMode(Animation.RESTART);
        setAnimation.setRepeatCount(1);// 设置了循环一次,但无效

        // 旋转动画
        Animation rotate = new RotateAnimation(0,360,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        rotate.setDuration(1000);
        rotate.setRepeatMode(Animation.RESTART);
        rotate.setRepeatCount(Animation.INFINITE);

        // 平移动画
        Animation translate = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_PARENT,-0.5f,
                TranslateAnimation.RELATIVE_TO_PARENT,0.5f,
                TranslateAnimation.RELATIVE_TO_SELF,0
                ,TranslateAnimation.RELATIVE_TO_SELF,0);
        translate.setDuration(10000);

        // 透明度动画
        Animation alpha = new AlphaAnimation(1,0);
        alpha.setDuration(3000);
        alpha.setStartOffset(7000);

        // 缩放动画
        Animation scale1 = new ScaleAnimation(1,0.5f,1,0.5f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scale1.setDuration(1000);
        scale1.setStartOffset(4000);

        // 将创建的子动画添加到组合动画里
        setAnimation.addAnimation(alpha);
        setAnimation.addAnimation(rotate);
        setAnimation.addAnimation(translate);
        setAnimation.addAnimation(scale1);

        textViewJava.startAnimation(setAnimation);
    }
}
