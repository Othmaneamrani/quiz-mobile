package com.example.quiz;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class Quiz extends AppCompatActivity {
    private TextView questionTextView;
    private TextView questionNum;
    private ImageView questionImageView;
    private RadioButton rep1RadioButton;
    private RadioButton rep2RadioButton;
    private RadioButton rep3RadioButton;
    private RadioButton rep4RadioButton;
    private RadioGroup choicesRadioGroup;
    private Button nextButton;
    int cpt=1;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private ImageCapture imageCapture;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference questionsRef = db.collection("Question");
    private List<Question> questions = new ArrayList<>();

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private static final int RECORD_AUDIO_PERMISSION_REQUEST_CODE = 2;
    private static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };

    private boolean capturing = false;
    private Timer captureTimer = new Timer();
    private MediaRecorder mediaRecorder;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();
    private String audioFilePath;

    Boolean fraude = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz);

        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            Toast.makeText(getApplicationContext(), "Record activé !", Toast.LENGTH_SHORT).show();
            initializeCamera();
            startAudioRecording();
        }

        questionNum = findViewById(R.id.nquiz);
        questionTextView = findViewById(R.id.q);
        questionImageView = findViewById(R.id.image1);
        rep1RadioButton = findViewById(R.id.choice1);
        rep2RadioButton = findViewById(R.id.choice2);
        rep3RadioButton = findViewById(R.id.choice3);
        rep4RadioButton = findViewById(R.id.choice4);
        choicesRadioGroup = findViewById(R.id.choices);
        nextButton = findViewById(R.id.bnext);


        loadQuestions();

        nextButton.setOnClickListener(v -> {
            int selectedId = choicesRadioGroup.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton selectedRadioButton = findViewById(selectedId);
                String selectedAnswer = selectedRadioButton.getText().toString();
                String correctAnswer = questions.get(currentQuestionIndex).getRep();

                if (selectedAnswer.equals(correctAnswer)) {
                    score++;
                }
            } else {
                Toast.makeText(getApplicationContext(), "Choose an answer.", Toast.LENGTH_SHORT).show();
                return;
            }

            currentQuestionIndex++;
            if (currentQuestionIndex < questions.size()) {
                displayQuestion();
            } else {
                if(fraude){
                    score=-1;
                }
                Intent intent = new Intent(Quiz.this, Score.class);
                intent.putExtra("score", score);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean hasPermissions() {
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void displayQuestion() {
        if (currentQuestionIndex < questions.size()) {
            Question currentQuestion = questions.get(currentQuestionIndex);
                questionNum.setText("Question " +cpt);
                cpt++;
            questionTextView.setText(currentQuestion.getQuestion());
            Glide.with(this)
                    .load(currentQuestion.getImage())
                    .into(questionImageView);

            rep1RadioButton.setText(currentQuestion.getRep1());
            rep2RadioButton.setText(currentQuestion.getRep2());
            rep3RadioButton.setText(currentQuestion.getRep3());
            rep4RadioButton.setText(currentQuestion.getRep4());

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
                    startCapturing();
                } else {
                    Toast.makeText(getApplicationContext(), "Error in extracting data from firestore !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initializeCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                        .build();
                imageCapture = new ImageCapture.Builder().build();
                cameraProvider.bindToLifecycle(this, cameraSelector, imageCapture);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }


    private void startCapturing() {
        capturing = true;
        captureTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (capturing) {
                    capturePhoto();
                }
            }
        }, 0, 2000);

        startAudioRecording();
    }

    private void capturePhoto() {
        File imageFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "photo_" + System.currentTimeMillis() + ".jpg");

        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(imageFile).build();
        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                uploadPhotoToFirebase(imageFile);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                exception.printStackTrace();
            }
        });
    }

    private void uploadPhotoToFirebase(File imageFile) {
        StorageReference imageRef = storageRef.child("images/" + imageFile.getName());

        imageRef.putFile(Uri.fromFile(imageFile))
                .addOnSuccessListener(taskSnapshot -> {
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(getApplicationContext(), "Failed to upload photo", Toast.LENGTH_SHORT).show();
                });
    }

    private void startAudioRecording() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            audioFilePath = getExternalFilesDir(Environment.DIRECTORY_MUSIC) + "/quiz_audio.mp3";
            mediaRecorder.setOutputFile(audioFilePath);

            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void uploadAudioToFirebase(String audioFilePath) {

        File audioFile = new File(audioFilePath);
        String fileName = audioFile.getName();
        StorageReference audioRef = storageRef.child("audios/" + fileName + System.currentTimeMillis());
        audioRef.putFile(Uri.fromFile(audioFile))
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(getApplicationContext(), "Succeed to upload audio", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(getApplicationContext(), "Failed to upload audio", Toast.LENGTH_SHORT).show();
                });
    }

    private void stopAudioRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAudioRecording();
        if (audioFilePath != null) {
            uploadAudioToFirebase(audioFilePath);
        }
    }
}
