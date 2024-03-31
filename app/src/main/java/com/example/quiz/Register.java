package com.example.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {

    EditText etMail , etPasswordR,etConfirmPasswd;
    Button bRegister;
    TextView tvLogin;

    FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(Register.this, Quiz1.class));
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
                                    startActivity(new Intent(Register.this, Quiz1.class));
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

