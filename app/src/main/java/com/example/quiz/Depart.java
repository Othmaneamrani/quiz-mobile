package com.example.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Depart  extends AppCompatActivity {

    ImageView imageView,imageView2;
    private TextView name;
    Button bStart;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();
String userAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.depart);

        imageView = findViewById(R.id.couv);
        imageView2 = findViewById(R.id.brain);
        bStart = (Button) findViewById(R.id.bStart);
        name= findViewById(R.id.name);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        String userEmail = currentUser.getEmail();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("User");

        usersRef.whereEqualTo("email", userEmail).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                  @Override
                                                                                  public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                      if (task.isSuccessful()) {
                                                                                          for (QueryDocumentSnapshot document : task.getResult()) {
                                                                                              userAuth = String.valueOf(document.get("nom"));
                                                                                              name.setText("Bienvenue "+userAuth);
                                                                                          }
                                                                                      }
                                                                                      else {
                                                                                          Toast.makeText(getApplicationContext(), "User introuvable.", Toast.LENGTH_SHORT).show();
                                                                                      }
                                                                                  }
                                                                              }
                                                                            );


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
