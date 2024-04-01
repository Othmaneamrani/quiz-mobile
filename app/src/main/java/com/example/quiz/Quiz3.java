package com.example.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class Quiz3 extends AppCompatActivity {
    ImageView imageView,imageView2;
    RadioGroup rg;
    RadioButton rb;
    Button bNext;
    int score;
    String RepCorrect="Non";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz3);

        rg=(RadioGroup) findViewById(R.id.choices);
        bNext=(Button) findViewById(R.id.bnext);
        imageView = findViewById(R.id.image3);
        imageView2 = findViewById(R.id.image32);


        Glide.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/quiz-394bc.appspot.com/o/q3.jpg?alt=media&token=7fe37b0b-babb-478c-912a-25d2c7fbbd78")
                .into(imageView);

        Glide.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/quiz-394bc.appspot.com/o/logoo.jpg?alt=media&token=1a36d40e-79fd-4385-a8d2-840fa79ed12d")
                .into(imageView2);

        Intent intent=getIntent();
        score=intent.getIntExtra("score",0) ;
        bNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rg.getCheckedRadioButtonId()==-1){
                    Toast.makeText(getApplicationContext(),"Choose an answer. !",Toast.LENGTH_SHORT).show();
                }
                else {
                    rb = (RadioButton) findViewById(rg.getCheckedRadioButtonId());
                    if (rb.getText().toString().equals(RepCorrect)) {
                        score += 1;
                    }
                    Intent intent = new Intent(Quiz3.this, Quiz4.class);
                    intent.putExtra("score", score);
                    startActivity(intent);
                    overridePendingTransition(R.anim.exit, R.anim.entry);
                    finish();
                }

            }
        });

    }
}