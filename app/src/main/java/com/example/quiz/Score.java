package com.example.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

public class Score extends AppCompatActivity {

    ImageView imageView;
    Button bLogout, bTry;
    ProgressBar progressBar;
    TextView tvScore;

    int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score);

        tvScore =(TextView) findViewById(R.id.etscore);
        progressBar=(ProgressBar) findViewById(R.id.etprogress);
        bLogout=(Button) findViewById(R.id.bLogout);
        bTry=(Button) findViewById(R.id.bTry);
        imageView = findViewById(R.id.logoo2);

        Glide.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/quiz-394bc.appspot.com/o/logoo.jpg?alt=media&token=1a36d40e-79fd-4385-a8d2-840fa79ed12d")
                .into(imageView);

        Intent intent=getIntent();
        score=intent.getIntExtra("score",0) ;
        progressBar.setProgress((100*score)/5);
        tvScore.setText((100*score)/5 +" %");

        bLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Thank you for your participation !", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Score.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
        bTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Score.this,Quiz1.class));
            }
        });

    }
}