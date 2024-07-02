package com.example.lunixpassmob.ui.search;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lunixpassmob.R;
import com.example.lunixpassmob.adapter.searchAdapter;
import com.example.lunixpassmob.databinding.FragmentSearchBinding;
import com.example.lunixpassmob.model.game.Game;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private RecyclerView recyclerView;
    private searchAdapter adapter;
    private List<Game> gameList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SearchView searchView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SearchViewModel searchViewModel =
                new ViewModelProvider(this).get(SearchViewModel.class);

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.search_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchView = binding.searchView;

        gameList = new ArrayList<>();
        adapter = new searchAdapter(getContext(), gameList);
        recyclerView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchList(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return false;
            }
        });
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
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
        return root;
    }

    public void searchList(String text) {
        ArrayList<Game> searchList = new ArrayList<>();
        for(Game dataClass: gameList){
            if(dataClass.getGameName().toLowerCase().contains(text.toLowerCase())){
                searchList.add(dataClass);
            }
        }
        adapter.searchDataList(searchList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
