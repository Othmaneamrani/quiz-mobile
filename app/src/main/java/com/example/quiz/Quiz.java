package com.example.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Quiz extends AppCompatActivity {
    private TextView questionTextView;
    private TextView questionNum;
    private ImageView questionImageView,imageView12;
    private RadioButton rep1RadioButton;
    private RadioButton rep2RadioButton;
    private RadioGroup choicesRadioGroup;
    private Button nextButton;
    private int currentQuestionIndex = 0;
    private int score = 0;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference questionsRef = db.collection("Question");
    List<Question> questions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz);

        questionNum = findViewById(R.id.nquiz);
        questionTextView = findViewById(R.id.q);
        questionImageView = findViewById(R.id.image1);
        imageView12= findViewById(R.id.image12);
        rep1RadioButton = findViewById(R.id.choice1);
        rep2RadioButton = findViewById(R.id.choice2);
        choicesRadioGroup = findViewById(R.id.choices);
        nextButton = findViewById(R.id.bnext);
        Glide.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/quiz2-4121b.appspot.com/o/logoo.jpg?alt=media&token=e6b26d8d-8432-491e-a1dc-2178e4eb4085")
                .into(imageView12);

        loadQuestions();

        nextButton.setOnClickListener(v -> {
            int selectedId = choicesRadioGroup.getCheckedRadioButtonId();
            if(selectedId != -1) {
                RadioButton selectedRadioButton = findViewById(selectedId);
                String selectedAnswer = selectedRadioButton.getText().toString();
                String correctAnswer = questions.get(currentQuestionIndex).getRep();

                if (selectedAnswer.equals(correctAnswer)) {
                    score++;
                }
            } else {
                Toast.makeText(getApplicationContext(),"Choose an answer.",Toast.LENGTH_SHORT).show();
                return;
            }

            currentQuestionIndex++;
            if (currentQuestionIndex < questions.size()) {
                displayQuestion();
            } else {
                Intent intent=new Intent(Quiz.this, Score.class);
                intent.putExtra("score",score);
                startActivity(intent);
                finish();
            }
        });
    }


    private void displayQuestion() {
        if (currentQuestionIndex < questions.size()) {
            Question currentQuestion = questions.get(currentQuestionIndex);

            questionNum.setText("Question " + currentQuestion.id);
            questionTextView.setText(currentQuestion.getQuestion());
            Glide.with(this)
                    .load(currentQuestion.getImage())
                    .into(questionImageView);

            rep1RadioButton.setText(currentQuestion.getRep1());
            rep2RadioButton.setText(currentQuestion.getRep2());

            choicesRadioGroup.clearCheck();
        }
    }


    private void loadQuestions() {
        questionsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Question question = document.toObject(Question.class);
                        questions.add(question);
                    }
                    displayQuestion();
                } else {
                    Toast.makeText(getApplicationContext(), "Error in extracting data from firestore !", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}