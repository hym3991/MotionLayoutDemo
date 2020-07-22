package com.example.motionlayoutdemo.animation;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.motionlayoutdemo.R;

public class FrameDemo extends AppCompatActivity {
    TextView textViewFrameXML;
    ImageView imageViewXML;
    ImageView imageViewJava;
    AnimationDrawable animationDrawable;
    AnimationDrawable animationDrawable2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame_demo);
        getSupportActionBar().setTitle("逐帧动画");
        /**
         * 逐帧动画的原理就是让一系列的静态图片依次播放，利用人眼“视觉暂留”的原理，实现动画。
         */
        textViewFrameXML = (TextView)findViewById(R.id.frame_xml_tv);
        imageViewXML = (ImageView)findViewById(R.id.frame_xml_iv);
        imageViewJava = (ImageView)findViewById(R.id.frame_java_iv);
    }


    public void frameAnimXml(View view) {
        animationDrawable = (AnimationDrawable) imageViewXML.getBackground();
        animationDrawable.start();
    }

    public void frameAnimJava(View view) {
        animationDrawable2 = new AnimationDrawable();
        for (int i = 0; i < 26; i ++ ){
            int id = getResources().getIdentifier("refresh" + i, "drawable", getPackageName());
            Drawable drawable = getResources().getDrawable(id);
            animationDrawable2.addFrame(drawable, 100);
        }
        animationDrawable2.setOneShot(false);
        imageViewJava.setImageDrawable(animationDrawable2);
        animationDrawable2.start();
    }
}
