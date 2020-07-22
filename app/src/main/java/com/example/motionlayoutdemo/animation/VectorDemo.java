package com.example.motionlayoutdemo.animation;

import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.motionlayoutdemo.R;

public class VectorDemo extends AppCompatActivity {
    ImageView imageViewPath;
    ImageView imageViewTrack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vector_demo);
        getSupportActionBar().setTitle("矢量动画");
        imageViewTrack = (ImageView)findViewById(R.id.vector_xml_iv);
        imageViewPath = (ImageView)findViewById(R.id.vector_xml_iv2);
    }

    public void vectorAnimXml(View view) {
        Animatable animatable = (Animatable) imageViewTrack.getBackground();
        animatable.start();
    }

    public void vectorAnimXml2(View view) {
        Animatable animatable = (Animatable) imageViewPath.getBackground();
        animatable.start();
    }
}
