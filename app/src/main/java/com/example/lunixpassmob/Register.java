package com.example.lunixpassmob;

import static com.google.firebase.firestore.FieldValue.serverTimestamp;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText RegUsername, RegPassword, RegEmail;
    TextView  logBtn;
    Button RegButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        RegEmail = findViewById(R.id.reg_email);
        RegPassword = findViewById(R.id.reg_password);
        RegUsername = findViewById(R.id.reg_username);
        RegButton = findViewById(R.id.reg_button);
        logBtn = findViewById(R.id.login);

        RegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = RegEmail.getText().toString().trim();
                String password = RegPassword.getText().toString().trim();
                String username = RegUsername.getText().toString().trim();

                if(email.isEmpty() || password.isEmpty() || username.isEmpty()) {
                    Toast.makeText(Register.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();
                            createFirestoreUser(userId, username, email);
                        } else {
                            Log.d("Error", "Account creation failed: " + task.getException().getMessage());
                            Toast.makeText(Register.this, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);

            }
        });
    }

    private void createFirestoreUser(String userId, String username, String email) {
        Map<String, Object> user = new HashMap<>();
        user.put("uid", userId);
        user.put("username", username);
        user.put("email", email);
        user.put("subscription", new HashMap<String, Object>() {{
            put("subs_start_date", 0);
            put("subs_type", 0);
            put("subs_end_date", 0);
            put("subs_status", false);
        }});
        user.put("statistic", new HashMap<String, Object>() {{
            put("achievement", 0);
            put("game_owned", 0);
            put("game_time", 0);
        }});
        user.put("library", new ArrayList<>());
        user.put("role", "user");
        user.put("createdAt", serverTimestamp());

        db.collection("user").document(userId).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Success", "User document created successfully");
                        finish();
                        startActivity(new Intent(Register.this, Login.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Error", "Error creating user document: " + e.getMessage());
                    }
                });
    }
}
