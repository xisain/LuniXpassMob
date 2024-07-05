package com.example.lunixpassmob;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lunixpassmob.adapter.GameAdapter;
import com.example.lunixpassmob.adapter.NewsAdapter;
import com.example.lunixpassmob.adapter.RecommendAdapter;
import com.example.lunixpassmob.databinding.ActivityMainBinding;
import com.example.lunixpassmob.model.game.Game;
import com.example.lunixpassmob.model.news.NewsItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RecyclerView newsRecyclerView, gameRecyclerView, recommendedRecyclerView;
    private NewsAdapter newsAdapter;
    private GameAdapter gameAdapter;
    private RecommendAdapter recommendAdapter;
    private List<NewsItem> newsList;
    private List<Game> gameList;
    private ActivityMainBinding binding;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        getSupportActionBar().hide();
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // Track user subscription status in real-time
        if (user != null) {
            String uid = user.getUid();
            db.collection("user").document(uid)
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                return;
                            }
                            if (snapshot != null && snapshot.exists()) {
                                Date subsEndDate = snapshot.getDate("subscription.subs_end_date");
                                if (subsEndDate != null) {
                                    Date now = new Date();
                                    if (subsEndDate.before(now)) {
                                        // Update subs_status to false if subs_end_date < now
                                        db.collection("user").document(uid)
                                                .update("subscription.subs_status", false, "library", new ArrayList<>())
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(MainActivity.this, "Your Subscription Has Ended", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w("MainActivity", "Error updating subscription status", e);
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(MainActivity.this, "Your Subscription is still Active Until " + subsEndDate, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    });

            // Check latest transaction for user
            db.collection("transaksi")
                    .whereEqualTo("id_user", user.getUid())
                    .limit(1)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w("Transaction", "Listen failed.", e);
                                return;
                            }
                            if (queryDocumentSnapshots != null) {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    trackApprovalStatus(documentSnapshot.getReference());
                                }
                            }
                        }
                    });
        }

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_search, R.id.navigation_notifications, R.id.navigation_lunixpass, R.id.navigation_library, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    private void updateSubscriptionStatus() {
        if (user != null) {
            DocumentReference userRef = db.collection("user").document(user.getUid());

            // Calculate end date (30 days from now)
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 30);
            Date endDate = calendar.getTime();

            Map<String, Object> updates = new HashMap<>();
            updates.put("subscription.subs_status", true);
            updates.put("subscription.subs_start_date", new Timestamp(new Date())); // Current timestamp
            updates.put("subscription.subs_end_date", new Timestamp(endDate));

            userRef.update(updates)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Subscription updated successfully
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle error updating subscription
                            Log.w("MainActivity", "Error updating subscription", e);
                        }
                    });
        }
    }

    private void trackApprovalStatus(DocumentReference transactionRef) {
        transactionRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    // Handle error
                    Log.w("MainActivity", "Listen failed.", error);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.w("MainActivity", "onEvent: " + snapshot.getBoolean("isApproved"));
                    if (Boolean.TRUE.equals(snapshot.getBoolean("isApproved"))) {
                        updateSubscriptionStatus();
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        Log.d("MainActivity", "onPause: Called");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d("MainActivity", "onResume: Called");
        super.onResume();
    }
}
