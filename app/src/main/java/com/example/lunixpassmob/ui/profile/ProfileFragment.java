package com.example.lunixpassmob.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.lunixpassmob.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {
    TextView number_played, number_session, number_achievment , tv_username;
    private FragmentProfileBinding binding;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        FirebaseUser user = mAuth.getCurrentUser();
        number_played = binding.textNumbergameplayed;
        number_session = binding.textNumberplaysession;
        number_achievment = binding.textNumberAchievment;
        tv_username = binding.tvUsername;
        if(user !=null){
        db.collection("user").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                tv_username.setText(documentSnapshot.getString("username"));
                number_played.setText(documentSnapshot.getLong("statistic.game_owned").toString());
                number_session.setText(documentSnapshot.getLong("statistic.game_time").toString());
                number_achievment.setText(documentSnapshot.getLong("statistic.achievement").toString());

            }
        });
        }



        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}