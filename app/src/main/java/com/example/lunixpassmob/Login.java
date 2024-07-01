//package com.example.lunixpassmob;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//
//public class Login extends AppCompatActivity {
//    FirebaseAuth mAuth;
//    EditText Lemail, Lpassword;
//    TextView SignUp;
//    Button loginBtn;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        mAuth = FirebaseAuth.getInstance();
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//
//        Lemail = findViewById(R.id.login_email);
//        Lpassword = findViewById(R.id.login_password);
//        SignUp = findViewById(R.id.sign_up);
//        loginBtn = findViewById(R.id.login_button);
//
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            finish();
//            startActivity(new Intent(getApplicationContext(), loginData.class));
//        } else {
//            loginBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String email = Lemail.getText().toString().trim();
//                    String password = Lpassword.getText().toString().trim();
//
//                    if (TextUtils.isEmpty(email)) {
//                        Toast.makeText(Login.this, "Enter Email", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    if (TextUtils.isEmpty(password)) {
//                        Toast.makeText(Login.this, "Enter Password", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//
//                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if (task.isSuccessful()) {
//                                FirebaseUser user = mAuth.getCurrentUser();
//                                if (user != null) {
//                                    String uid = user.getUid();
//                                    Log.d("TAG", "signInWithEmail:success UID: " + uid);
//                                    finish();
//                                    startActivity(new Intent(getApplicationContext(), loginData.class));
//                                }
//                            } else {
//                                Log.w("TAG", "signInWithEmail:failure", task.getException());
//                                Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//                }
//            });
//        }
//    }
//
//}