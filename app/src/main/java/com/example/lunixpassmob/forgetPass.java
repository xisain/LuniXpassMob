package com.example.lunixpassmob;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class forgetPass extends AppCompatActivity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Button resetPass;
    EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);
        email = findViewById(R.id.reset_email);
        resetPass = findViewById(R.id.button_submit);

        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reset_email = email.getText().toString().trim();
                if(reset_email.isEmpty()){
                    Toast.makeText(forgetPass.this, "Please enter your email address", Toast.LENGTH_SHORT).show();
                } else {
                    //send reset password link
                    mAuth.sendPasswordResetEmail(reset_email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(forgetPass.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(forgetPass.this, Login.class));
                        }
                    });

                }
            }
        });
    }
}
