package com.example.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class Register extends AppCompatActivity {
    ImageView imageView;
    EditText etMail , etPasswordR,etConfirmPasswd;
    Button bRegister;
    TextView tvLogin;
    TextView name;
    FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(Register.this, Depart.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        etMail = (EditText) findViewById(R.id.etMail);
        etPasswordR = (EditText) findViewById(R.id.etPasswordR);
        etConfirmPasswd = (EditText) findViewById(R.id.etConfirmPasswd);
        bRegister = (Button) findViewById(R.id.bRegister);
        tvLogin = (TextView) findViewById(R.id.tvLogin);
        imageView = findViewById(R.id.logoo);
        name =findViewById(R.id.etName);

        Glide.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/quiz2-4121b.appspot.com/o/dev.png?alt=media&token=9ecbc69e-d057-4693-830e-f2593bf97ea2")
                .into(imageView);

        mAuth = FirebaseAuth.getInstance();

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email , password,passwordConfirm;
                email = String.valueOf(etMail.getText());
                password = String.valueOf(etPasswordR.getText());
                passwordConfirm = String.valueOf(etConfirmPasswd.getText());

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(),"Enter email.",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(),"Enter email.",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.contentEquals(passwordConfirm)){
                    Toast.makeText(getApplicationContext(),"Password confirmation incorrect !",Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Register.this, "Registration succeeded .",
                                            Toast.LENGTH_SHORT).show();
                                    String userId = mAuth.getCurrentUser().getUid();

                                    String userName = String.valueOf(name.getText()) ;

                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    DocumentReference userRef = db.collection("User").document(userId);
                                    userRef.set(new HashMap<String, Object>() {{
                                        put("ID", userId);
                                        put("nom", userName);
                                        put("email",email);
                                        }});

                                    startActivity(new Intent(Register.this, Depart.class));
                                    finish();
                                } else {
                                    Toast.makeText(Register.this, "Registration failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this, Login.class));
                finish();
            }
        });


    }
}

