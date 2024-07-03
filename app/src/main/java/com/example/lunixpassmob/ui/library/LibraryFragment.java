package com.example.lunixpassmob.ui.library;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lunixpassmob.R;
import com.example.lunixpassmob.adapter.LibraryAdapter;
import com.example.lunixpassmob.databinding.FragmentLibraryBinding;
import com.example.lunixpassmob.databinding.FragmentProfileBinding;
import com.example.lunixpassmob.model.user.LibModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends Fragment {

    private RecyclerView recyclerView;
    private LibraryAdapter libraryAdapter;
    private List<LibModel> gameList;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private FragmentLibraryBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLibraryBinding.inflate(inflater, container, false);
         View  view = binding.getRoot();
        recyclerView = binding.libraryRecycleView;
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));

        gameList = new ArrayList<>();
        libraryAdapter = new LibraryAdapter(requireContext(), gameList);
        recyclerView.setAdapter(libraryAdapter);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            loadUserLibrary();
        }
        else{
            Toast.makeText(getContext(), "User Not Found", Toast.LENGTH_SHORT).show();
        }

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void loadUserLibrary() {
        DocumentReference userRef = db.collection("user").document(user.getUid());
        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("Library", "Listen failed.", e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    List<DocumentReference> libraryRefs = (List<DocumentReference>) documentSnapshot.get("library");
                    if (libraryRefs != null) {
                        gameList.clear();
                        for (DocumentReference gameRef : libraryRefs) {
                            gameRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        String image_url = documentSnapshot.getString("game_image");
                                        String gameId = documentSnapshot.getId();
                                        LibModel libModel = new LibModel(gameId, image_url);

                                        gameList.add(libModel);
                                        libraryAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }
}