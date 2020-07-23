package com.example.motionlayoutdemo.animation;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.example.motionlayoutdemo.R;

/**
 * 属性动画
 */
public class ObjectDemo extends AppCompatActivity {
    LinearLayoutCompat layoutCompat;
    TextView textViewAplha;
    TextView textViewScale;
    TextView textViewRotation;
    TextView textViewSet;
    TextView textViewProperty1;
    TextView textViewProperty2;
    TextView textViewXML;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obj_demo);
        getSupportActionBar().setTitle("属性动画");
        layoutCompat = (LinearLayoutCompat)findViewById(R.id.ll);
        textViewAplha = (TextView)findViewById(R.id.alpha_tv);
        textViewScale = (TextView)findViewById(R.id.scale_tv);
        textViewRotation = (TextView)findViewById(R.id.rotation_tv);
        textViewSet = (TextView)findViewById(R.id.set_tv);
        textViewProperty1 = (TextView)findViewById(R.id.property1_tv);
        textViewProperty2 = (TextView)findViewById(R.id.property2_tv);
        textViewXML = (TextView)findViewById(R.id.xml_tv);
    }

    public void object01(View view) {
        /**
         * ofFloat()
         * 第一个参数为要实现动画效果的View
         * 第二个参数为属性名 ->translationX,translationY,alpha,rotation,scaleX,scaleY等
         * 第三参数为可变长参数:
         *  第一个值为动画开始的位置
         *  第二个值为结束值得位置
         *  如果数组大于3位数，那么前者将是后者的起始位置
         *
         *  注意：translationX和translationY这里涉及到的位移都是相对自身位置而言
         *  例如 View在点A(x,y)要移动到点B(x1,y1),那么ofFloat（）方法的可变长参数，第一个值应该0f,第二个值应该x1-x。
         */
        ObjectAnimator animator = ObjectAnimator.ofFloat(layoutCompat,"translationX",0f, -300f,0f);
        animator.setDuration(5000);
        animator.start();
    }

    public void object01Alpha(View view) {
        /**
         * ofFloat()方法将属性名换成了透明度alpha
         */
        ObjectAnimator objectAnimation =ObjectAnimator.ofFloat(textViewAplha, "alpha", 1f,0f,1f);
        objectAnimation.setDuration(3000);
        objectAnimation.start();
    }

    public void object01Scale(View view) {
        /**
         * ofFloat()方法传入参数属性为scaleX和scaleY时，动态参数表示缩放的倍数。
         * 设置ObjectAnimator对象的repeatCount属性来控制动画执行的次数，
         * 设置为ValueAnimator.INFINITE表示无限循环播放动画；
         * 通过repeatMode属性设置动画重复执行的效果
         * ValueAnimator.RESTART 即每次都重头开始
         * ValueAnimator.REVERSE 和上一次效果反着来
         */
        ObjectAnimator objectAnimation =ObjectAnimator.ofFloat(textViewScale, "scaleX", 1f,2f);
        objectAnimation.setDuration(3000);
        objectAnimation.setRepeatCount(2);
        objectAnimation.setRepeatMode(ValueAnimator.REVERSE);
        objectAnimation.start();
    }

    public void object01Rotation(View view) {
        /**
         * ofFloat()方法的可变长参数，如果后者的值大于前者，那么顺时针旋转，小于前者，则逆时针旋转。
         */
        ObjectAnimator objectAnimation = ObjectAnimator.ofFloat(textViewRotation, "rotation", 0f,360f,0f);
        objectAnimation.setDuration(3000);
        objectAnimation.start();
    }

    public void object01Set(View view) {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(textViewSet, "alpha", 1f,0f,1f);
        ObjectAnimator scaleX =ObjectAnimator.ofFloat(textViewSet, "scaleX", 1f,2f);
        ObjectAnimator rotation = ObjectAnimator.ofFloat(textViewSet, "rotation", 0f,360f,0f);

        /**
         * 如果想要一个动画结束后播放另外一个动画，或者同时播放，可以通过AnimatorSet来编排
         *
         * 先变化透明度，之后同时缩放和旋转
         */
        AnimatorSet set = new AnimatorSet();
        set.play(alpha).before(scaleX);//a 在b之前播放
        set.play(scaleX).with(rotation);//b和c同时播放动画效果
        set.setDuration(5000);
        set.start();
    }

    public void object01Property1(View view) {
        /**
         * 只是针对View对象的特定属性同时播放动画
         */

        textViewProperty1.animate()
                .translationX(100f).rotation(180)
                .translationX(200f).rotation(180*2)
                .translationX(300f).rotation(180*3)
                .translationX(400f).rotation(180*4)
                .translationX(500f).rotation(180*5)
                .translationX(600f).rotation(180*6)
                .translationX(700f).rotation(180*7)
                .translationX(800f).rotation(180*8)
                .translationX(900f).rotation(180*9)
                .setDuration(2000)
                .start();
    }

    public void object01Property2(View view) {
        /**
         * translationX 点击一次会向右偏移，再点击没效果
         * translationXBy //每次点击都会向右偏移
         */

        textViewProperty2.animate()
                .translationXBy(100f).rotationBy(180)
                .setDuration(1000)
                .start();
    }

    public void object01Xml(View view) {
        //加载动画资源
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.object_demo_anim);
        set.setTarget(textViewXML);
        set.start();
    }
}
