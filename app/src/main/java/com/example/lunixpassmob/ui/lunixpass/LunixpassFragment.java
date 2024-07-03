package com.example.lunixpassmob.ui.lunixpass;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lunixpassmob.adapter.GameAdapter;

import com.example.lunixpassmob.databinding.FragmentLunixpassBinding;
import com.example.lunixpassmob.model.game.Game;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LunixpassFragment extends Fragment {
    private RecyclerView  gameRecyclerView;
    private GameAdapter gameAdapter;
    private List<Game> gameList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FragmentLunixpassBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLunixpassBinding.inflate(inflater, container, false);



        gameRecyclerView = binding.passRecycleView;
        gameRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        gameList = new ArrayList<>();
        gameAdapter = new GameAdapter(getContext(), gameList);
        gameRecyclerView.setAdapter(gameAdapter);


        View root = binding.getRoot();
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
                                Log.w("Find ID ", id);
                                HashMap<String, Integer> gameDetail = (HashMap<String, Integer>) documentSnapshot.get("game_detail");
                                gameList.add(new Game(id,gameName, gameDescription, gameImage, genre, gameDetail));
                            }
                            gameAdapter.notifyDataSetChanged();
                        }
                    }
                });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}