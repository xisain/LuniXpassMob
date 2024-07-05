package com.example.lunixpassmob.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lunixpassmob.Login;
import com.example.lunixpassmob.MainActivity;
import com.example.lunixpassmob.R;
import com.example.lunixpassmob.adapter.GameAdapter;
import com.example.lunixpassmob.adapter.NewsAdapter;
import com.example.lunixpassmob.adapter.RecommendAdapter;
import com.example.lunixpassmob.databinding.FragmentHomeBinding;
import com.example.lunixpassmob.model.game.Game;
import com.example.lunixpassmob.model.news.NewsItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment {
    private ImageView log;
    private FragmentHomeBinding binding;
    private RecyclerView newsRecyclerView, gameRecyclerView, recommendedRecyclerView;
    private NewsAdapter newsAdapter;
    private GameAdapter gameAdapter;
    private RecommendAdapter recommendAdapter;
    private List<NewsItem> newsList;
    private List<Game> gameList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference docRef = db.collection("user");
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        log = binding.log;
        if (user != null) {
            log.setImageResource(R.drawable.ic_logout_24);
            log.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAuth.signOut();
                    Toast.makeText(getContext(), "Logout Success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            log.setImageResource(R.drawable.ic_login_24);
            log.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), Login.class);
                    startActivity(intent);
                }
            });
        }
        // Access views using the binding object
        newsRecyclerView = binding.newsRecycleView;
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        newsList = new ArrayList<>();
        newsAdapter = new NewsAdapter(getContext(), newsList);
        newsRecyclerView.setAdapter(newsAdapter);

        // Deklarasi Adapter Untuk Recycle View Game
        gameRecyclerView = binding.gameRecycleView;
        gameRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        gameList = new ArrayList<>();
        gameAdapter = new GameAdapter(getContext(), gameList);
        gameRecyclerView.setAdapter(gameAdapter);

        // Deklarasi Adapter Untuk Recycle View Recommended
        recommendedRecyclerView = binding.recommendRecycleView;
        recommendedRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        recommendAdapter = new RecommendAdapter(getContext(), gameList);
        recommendedRecyclerView.setAdapter(recommendAdapter);

        db.collection("berita").orderBy("date", Query.Direction.DESCENDING).limit(3)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("MainActivity", "Listen failed.", e);
                            return;
                        }
                        if (queryDocumentSnapshots != null) {
                            newsList.clear();
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                String id = documentSnapshot.getId();
                                String header = documentSnapshot.getString("header");
                                String imageUrl = documentSnapshot.getString("image");
                                newsList.add(new NewsItem(id, imageUrl, header));
                            }
                            newsAdapter.notifyDataSetChanged();
                        }
                    }
                });
        // Digunakan untuk data game yang akan ditampilkan pada tampilan Utama
        db.collection("game")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("MainActivity", "Listen failed.", e);
                            return;
                        }
                        if (queryDocumentSnapshots != null) {
                            gameList.clear();
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                String id = documentSnapshot.getId();
                                String gameName = documentSnapshot.getString("game_name");
                                String gameImage = documentSnapshot.getString("game_image");
                                String gameDescription = documentSnapshot.getString("game_desc");
                                List<String> genre = (List<String>) documentSnapshot.get("genre");
                                HashMap<String, Integer> gameDetail = (HashMap<String, Integer>) documentSnapshot.get("game_detail");
                                gameList.add(new Game(id, gameName, gameDescription, gameImage, genre, gameDetail));
                            }
                            gameAdapter.notifyDataSetChanged();
                            recommendAdapter.notifyDataSetChanged();
                        }
                    }
                });

        return root;
    }


    @Override
    public void onResume() {
        super.onResume();
        Glide.with(requireContext()).resumeRequests();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}