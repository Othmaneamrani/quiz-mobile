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

public class Quiz2 extends AppCompatActivity {
    ImageView imageView,imageView2;
    RadioGroup rg;
    RadioButton rb;
    Button bNext;
    int score;
    String RepCorrect="À droite";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz2);

        rg=(RadioGroup) findViewById(R.id.choices);
        bNext=(Button) findViewById(R.id.bnext);
        imageView = findViewById(R.id.image2);
        imageView2 = findViewById(R.id.image22);


        Glide.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/quiz-394bc.appspot.com/o/q2.jpg?alt=media&token=0df776ff-0bc4-443d-b530-fadb82fb4641")
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
                    Toast.makeText(getApplicationContext(),"Choose an answer.",Toast.LENGTH_SHORT).show();
                }
                else {
                    rb = (RadioButton) findViewById(rg.getCheckedRadioButtonId());
                    if (rb.getText().toString().equals(RepCorrect)) {
                        score += 1;
                    }
                    Intent intent = new Intent(Quiz2.this, Quiz3.class);
                    intent.putExtra("score", score);
                    startActivity(intent);
                    overridePendingTransition(R.anim.exit, R.anim.entry);
                    finish();
                }

            }
        });

    }
}