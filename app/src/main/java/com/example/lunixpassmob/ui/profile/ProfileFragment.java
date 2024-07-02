package com.example.lunixpassmob.ui.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lunixpassmob.Login;
import com.example.lunixpassmob.R;
import com.example.lunixpassmob.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.checkerframework.checker.units.qual.A;

public class ProfileFragment extends Fragment {
    TextView number_played, number_session, number_achievment , tv_username;
    ImageView logout, edit;
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
        logout = binding.logout;
        edit = binding.edit;

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showEditUsernameDialog();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder AlertLogout = new AlertDialog.Builder(requireContext());
                AlertLogout.setMessage("Are you sure want to log out?").setTitle("Warning");
                AlertLogout.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertLogout.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        getActivity().finish();
                        Intent intent = new Intent(requireContext(), Login.class);
                        startActivity(intent);
                    }
                }) .create()
                        .show();
            }
        });
        if(user !=null){
            DocumentReference docRef = db.collection("user").document(user.getUid());
            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value !=null && value.exists()){
                tv_username.setText(value.getString("username"));
                    tv_username.setText(value.getString("username"));
                    number_played.setText(value.getLong("statistic.game_owned").toString());
                    number_session.setText(value.getLong("statistic.game_time").toString());
                    number_achievment.setText(value.getLong("statistic.achievement").toString());

                }
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

    private void showEditUsernameDialog() {
        // Inflate the layout for the dialog
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_username, null);

        // Get the username text input from the dialog
        final EditText usernameInput = dialogView.findViewById(R.id.username_input);

        // Create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit Username")
                .setView(dialogView)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get the new username from the input
                        String newUsername = usernameInput.getText().toString().trim();

                        // Update the username in the database
                        updateUsername(newUsername);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void updateUsername(String newUsername) {
        // Get the current user
        FirebaseUser user = mAuth.getCurrentUser();

        // Update the username in the Firestore database
        db.collection("user").document(user.getUid())
                .update("username", newUsername)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(requireContext(), "Username Updated", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the error
                    }
                });
    }
}