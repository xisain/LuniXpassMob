package com.example.lunixpassmob;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Date;

public class NewsDetail extends AppCompatActivity {
    TextView newsHeader, newsContent, newsDate;
    ImageView newsImage;
    FirebaseFirestore db;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_news_detail);
        getSupportActionBar().hide();
        newsHeader = findViewById(R.id.news_header);
        newsContent = findViewById(R.id.news_content);
        newsDate = findViewById(R.id.news_date);
        newsImage = findViewById(R.id.news_image);
        db = FirebaseFirestore.getInstance();
        Bundle extras = getIntent().getExtras();
        String uid = extras.getString("id");
        if(uid !=null){

            DocumentReference newsRef = db.collection("berita").document(uid);
            newsRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w("NewsDetail", "Listen failed.", e);
                        Toast.makeText(NewsDetail.this, "Error fetching game details", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        newsHeader.setText(documentSnapshot.getString("header"));
                        newsContent.setText(documentSnapshot.getString("content"));

                        Date date = documentSnapshot.getDate("date");
                        String newsImageStr = documentSnapshot.getString("image");
                        newsDate.setText(date.toString());
                        Glide.with(NewsDetail.this).load(newsImageStr).into(newsImage);
                    } else {
                        Toast.makeText(NewsDetail.this, "Game not found", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
