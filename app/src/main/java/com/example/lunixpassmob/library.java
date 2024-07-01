//package com.example.lunixpassmob;
//
//import android.os.Bundle;
//import android.util.Log;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.EventListener;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.FirebaseFirestoreException;
//import com.lunixcorp.lunixpass.Adapter.LibraryAdapter;
//import com.lunixcorp.lunixpass.model.user.LibModel;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class library extends AppCompatActivity {
//
//    private RecyclerView recyclerView;
//    private LibraryAdapter libraryAdapter;
//    private List<LibModel> gameList;
//    private FirebaseFirestore db;
//    private FirebaseUser user;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_library);
//
//        recyclerView = findViewById(R.id.library_recycle_view);
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
//
//        gameList = new ArrayList<>();
//        libraryAdapter = new LibraryAdapter(this, gameList);
//        recyclerView.setAdapter(libraryAdapter);
//
//        db = FirebaseFirestore.getInstance();
//        user = FirebaseAuth.getInstance().getCurrentUser();
//
//        if (user != null) {
//            loadUserLibrary();
//        }
//    }
//
//    private void loadUserLibrary() {
//        DocumentReference userRef = db.collection("user").document(user.getUid());
//        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
//                if (e != null) {
//                    Log.w("Library", "Listen failed.", e);
//                    return;
//                }
//
//                if (documentSnapshot != null && documentSnapshot.exists()) {
//                    List<DocumentReference> libraryRefs = (List<DocumentReference>) documentSnapshot.get("library");
//                    if (libraryRefs != null) {
//                        gameList.clear();
//                        for (DocumentReference gameRef : libraryRefs) {
//                            gameRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                                @Override
//                                public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                    if (documentSnapshot.exists()) {
//                                       String image_url= documentSnapshot.getString("game_image");
//                                       String gameId = documentSnapshot.getId();
//                                       LibModel test = new LibModel(gameId,image_url);
//
//                                        gameList.add(test);
//                                       libraryAdapter.notifyDataSetChanged();
//
//                                    }
//                                }
//                            });
//                        }
//                    }
//                }
//            }
//        });
//    }
//}
