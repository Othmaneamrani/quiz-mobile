package com.example.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class Depart  extends AppCompatActivity {

    ImageView imageView,imageView2;
    Button bStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.depart);

        imageView = findViewById(R.id.couv);
        imageView2 = findViewById(R.id.brain);
        bStart = (Button) findViewById(R.id.bStart);



        Glide.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/quiz2-4121b.appspot.com/o/couv.webp?alt=media&token=848a0564-96ed-4800-a006-80bdc564805d")
                .into(imageView);

        Glide.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/quiz2-4121b.appspot.com/o/brain.jpg?alt=media&token=bf4e400a-6a03-47d0-a4a1-804819142b99")
                .into(imageView2);


        bStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Depart.this, Quiz.class));
                finish();
            }
        });

    }


}
