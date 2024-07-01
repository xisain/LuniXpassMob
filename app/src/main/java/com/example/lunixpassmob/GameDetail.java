package com.example.lunixpassmob;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.List;

public class GameDetail extends AppCompatActivity {

    String uid = "";
    Button addToLib;
    TextView gameName, gameDesc, gameDeveloper, gameReleaseDate, gameSize;
    ImageView gameImage;
    FirebaseFirestore db;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d("GameDetail", user.getUid());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);
        getSupportActionBar().hide();
        gameName = findViewById(R.id.gameName);
        gameDesc = findViewById(R.id.gameDescription);
        gameDeveloper = findViewById(R.id.gameDeveloper);
        gameImage = findViewById(R.id.gameIcon);
        gameReleaseDate = findViewById(R.id.releaseDate);
        gameSize = findViewById(R.id.size);
        addToLib = findViewById(R.id.button_add_to_library);

        db = FirebaseFirestore.getInstance();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            uid = extras.getString("id");
            if (uid != null) {
                fetchGameDetails(uid);
            } else {
                Toast.makeText(this, "Game ID not found", Toast.LENGTH_SHORT).show();
            }
        }

        addToLib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("GameDetail", "Adding to library: " + uid);
                checkAndAddGameToLibrary(uid);
            }
        });
    }

    private void fetchGameDetails(String uid) {
        DocumentReference gameRef = db.collection("game").document(uid);
        gameRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("GameDetail", "Listen failed.", e);
                    Toast.makeText(GameDetail.this, "Error fetching game details", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    String gameNameStr = documentSnapshot.getString("game_name");
                    String gameDescStr = documentSnapshot.getString("game_desc");
                    String gameImageStr = documentSnapshot.getString("game_image");
                    HashMap<String, Object> gameDetailMap = (HashMap<String, Object>) documentSnapshot.get("game_detail");
                    String gameDeveloperStr = "";
                    String gameReleaseDateStr = "";
                    String gameSizeStr = "";

                    if (gameDetailMap != null) {
                        // Assuming "publisher" is a key in the gameDetail map and its value is the developer's name
                        gameDeveloperStr = (String) gameDetailMap.get("publisher");
                        gameReleaseDateStr = (String) gameDetailMap.get("release_date");
                        gameSizeStr = String.valueOf(gameDetailMap.get("size"));
                    }

                    gameName.setText(gameNameStr);
                    gameDesc.setText(gameDescStr);
                    gameDeveloper.setText("Developer: " + gameDeveloperStr);
                    gameReleaseDate.setText("Release Date: " + gameReleaseDateStr);
                    gameSize.setText("Size: " + gameSizeStr + "GB");

                    Glide.with(GameDetail.this).load(gameImageStr).into(gameImage);
                } else {
                    Toast.makeText(GameDetail.this, "Game not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkAndAddGameToLibrary(String gameId) {
        if(user !=null) {
            DocumentReference userRef = db.collection("user").document(user.getUid());
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        List<DocumentReference> library = (List<DocumentReference>) documentSnapshot.get("library");
                        if (library != null) {
                            for (DocumentReference gameRef : library) {
                                if (gameRef.getId().equals(gameId)) {
                                    Toast.makeText(GameDetail.this, "Game already in library", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        }
                        addGameToLibrary(gameId);
                    } else {
                        addGameToLibrary(gameId);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("GameDetail", "Error checking library", e);
                    Toast.makeText(GameDetail.this, "Error checking library", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Must Login First", Toast.LENGTH_SHORT).show();
        }
    }

    private void addGameToLibrary(String gameId) {
        if(user !=null){
            DocumentReference userRef = db.collection("user").document(user.getUid());
            userRef.update("library", FieldValue.arrayUnion(db.collection("game").document(gameId)))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(GameDetail.this, "Game added to library", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("GameDetail", "Error adding game to library", e);
                            Toast.makeText(GameDetail.this, "Error adding game to library", Toast.LENGTH_SHORT).show();
                        }
                    });
        }else {
            Toast.makeText(this, "Must Login First", Toast.LENGTH_SHORT).show();
        }
    }
}
