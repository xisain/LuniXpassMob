package com.example.lunixpassmob;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    FirebaseAuth mAuth;
    EditText Lemail, Lpassword;
    TextView SignUp, forgetPass;
    Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        Lemail = findViewById(R.id.login_email);
        Lpassword = findViewById(R.id.login_password);
        SignUp = findViewById(R.id.sign_up);
        loginBtn = findViewById(R.id.login_button);
        forgetPass = findViewById(R.id.forget_password);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        } else {
            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = Lemail.getText().toString().trim();
                    String password = Lpassword.getText().toString().trim();
                    Log.w("Login", "Email: " + email + " Password: " + password);
                    if (TextUtils.isEmpty(email)) {
                        Toast.makeText(Login.this, "Enter Email", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(password)) {
                        Toast.makeText(Login.this, "Enter Password", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String uid = user.getUid();
                                    Log.d("TAG", "signInWithEmail:success UID: " + uid);
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                }
                            }  else {
                                Log.w("Login Failure", task.getException().getMessage(), task.getException());
                                String errorMessage;
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthInvalidUserException e) {
                                    errorMessage = e.getMessage();
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    errorMessage = "Invalid email or password";
                                } catch (Exception e){
                                    errorMessage = e.getMessage();
                                }
                                Toast.makeText(Login.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
            SignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivity(new Intent(getApplicationContext(), Register.class));
                }
            });
            forgetPass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), forgetPass.class));

                }
            });
        }

    }

}