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

import com.example.lunixpassmob.MainActivity;
import com.example.lunixpassmob.PaymentActivity;
import com.example.lunixpassmob.R;
import com.example.lunixpassmob.adapter.GameAdapter;

import com.example.lunixpassmob.databinding.FragmentLunixpassBinding;
import com.example.lunixpassmob.model.game.Game;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
                       mulaiActivity();
                   }else {
                       Toast.makeText(requireContext(), "Must Login First", Toast.LENGTH_SHORT).show();
                   }
               }
           });
           subscribe2.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                       if(user != null){
                           mulaiActivity();
                       } else {
                           Toast.makeText(requireContext(), "Must Login First", Toast.LENGTH_SHORT).show();
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
    public void subsEvent() {
    if(user != null){
        isSubsEnd(new OnSubscriptionCheckCompleteListener() {
            @Override
            public void onComplete(boolean isSubscribed) {
                if(!isSubscribed){
                    DocumentReference documentReference = db.collection("user").document(user.getUid());
                    Timestamp now = Timestamp.now();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(now.toDate());
                    calendar.add(Calendar.DAY_OF_YEAR, 30);
                    Date subsEndDate = calendar.getTime();
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("subscription.subs_start_date", FieldValue.serverTimestamp());
                    updates.put("subscription.subs_status", true);
                    updates.put("subscription.subs_end_date", new Timestamp(subsEndDate));
                    documentReference.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(requireContext(), "Thank You For Using Our Service", Toast.LENGTH_SHORT).show();
                            Log.d("Success", "Subscription updated successfully");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Error", "Error updating subscription: " + e.getMessage());
                        }
                    });
                } else {
                    Log.w("SubscriptionCheck", "User Already Subs");
                    Toast.makeText(getContext(), "Already Subscribing", Toast.LENGTH_SHORT).show();
                }
            }
        });
    } else {
        Toast.makeText(getContext(), "Must Login First", Toast.LENGTH_SHORT).show();
    }
    }
    public void isSubsEnd(final OnSubscriptionCheckCompleteListener listener) {
        if (user != null) {
            db.collection("user").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        Timestamp subsEndDate = documentSnapshot.getTimestamp("subscription.subs_end_date");
                        if (subsEndDate != null) {
                            Timestamp now = Timestamp.now();
                            boolean isSubscribed = subsEndDate.compareTo(now) > 0;
                            listener.onComplete(isSubscribed);
                        } else {
                            listener.onComplete(false); // No subscription end date means not subscribed
                        }
                    } else {
                        listener.onComplete(false); // Document does not exist means not subscribed
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("SubscriptionCheck", "Error checking subscription", e);
                    listener.onComplete(false); // Error means not subscribed
                }
            });
        } else {
            Toast.makeText(getContext(), "Must Login First", Toast.LENGTH_SHORT).show();
            listener.onComplete(false); // User is not logged in means not subscribed
        }
    }

    public interface OnSubscriptionCheckCompleteListener {
        void onComplete(boolean isSubscribed);
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



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}