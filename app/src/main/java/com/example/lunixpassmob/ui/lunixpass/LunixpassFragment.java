package com.example.lunixpassmob.ui.lunixpass;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lunixpassmob.Login;
import com.example.lunixpassmob.MainActivity;
import com.example.lunixpassmob.PaymentActivity;
import com.example.lunixpassmob.R;
import com.example.lunixpassmob.adapter.GameAdapter;

import com.example.lunixpassmob.databinding.FragmentLunixpassBinding;
import com.example.lunixpassmob.model.game.Game;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class LunixpassFragment extends Fragment {
    Button  subscribe1, subscribe2;
    private RecyclerView  gameRecyclerView;
    private GameAdapter gameAdapter;
    private List<Game> gameList;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    private FragmentLunixpassBinding binding;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLunixpassBinding.inflate(inflater, container, false);



        gameRecyclerView = binding.passRecycleView;
        gameRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        gameList = new ArrayList<>();
        gameAdapter = new GameAdapter(getContext(), gameList);
        gameRecyclerView.setAdapter(gameAdapter);
        subscribe1 = binding.subscribe1;
        subscribe2 = binding.subscribe2;


           subscribe1.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   if(user != null){
                       checkSubs();
//                       mulaiActivity();
                   }else {
                       mustLogin();
                       Intent i = new Intent(getContext(), Login.class);
                       startActivity(i);
                   }
               }
           });
           subscribe2.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                       if(user != null){
                           checkSubs();
                       } else {
                          mustLogin();

                       }
               }});



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
                                HashMap<String, Integer> gameDetail = (HashMap<String, Integer>) documentSnapshot.get("game_detail");
                                gameList.add(new Game(id,gameName, gameDescription, gameImage, genre, gameDetail));
                            }
                            gameAdapter.notifyDataSetChanged();
                        }
                    }
                });


        return root;
    }
    public void checkSubs() {
        if (user != null) {
            DocumentReference subRef = db.collection("user").document(user.getUid());
            subRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Boolean hasSubscription = document.getBoolean("subscription.subs_status");
                            if (Boolean.TRUE.equals(hasSubscription)) {
                                Toast.makeText(requireContext(), "You already have a subscription", Toast.LENGTH_SHORT).show();
                            } else {
                                mulaiActivity();
                            }
                        } else {
                            mulaiActivity();
                        }
                    } else {
                        Log.d("LunixpassFragment", "Failed with: ", task.getException());
                    }
                }
            });
        }
    }

    public void mulaiActivity() {
        Intent intent = new Intent(getContext(), PaymentActivity.class);
        startActivity(intent);
    }
    /*
    TODO : Membuat Sebuah Tombol Subscribe di Klik maka Akan
     Memunculkan sebuah dialog konfirmasi pembayaran user akan
     menginput Bukti pembayaran Sementara itu Data Subscribe akan di pending Hingga
     Admin melakukan Konfirmasi Terhadap pembayaran tersebut

     TODO : Menambahkan data ke koleksi "transaksi" untuk menunggu persetujuan admin
                        Image diupload ke storage dengan path bukti_pembayaran/{userId-TanggalTransaksi}.jpg
                        dan dokumen transaksi berisikan
                        Transaksi/{generated_id}/
                        id-user: user.getUid()
                        tanggal transaksi :serverTimestamp
                        bukti_pembayaran :(image link)
                        isApproved : boolean
      *
      */


    public void mustLogin(){
        Toast.makeText(requireContext(), R.string.must_login, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}