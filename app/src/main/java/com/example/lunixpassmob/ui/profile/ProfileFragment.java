package com.example.lunixpassmob.ui.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;

public class ProfileFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    TextView number_played, number_session, number_achievment, tv_username;
    ImageView logout, edit, imageProfile;
    private FragmentProfileBinding binding;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    Uri imageUri;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        FirebaseUser user = mAuth.getCurrentUser();
        number_played = binding.textNumbergameplayed;
        number_session = binding.textNumberplaysession;
        number_achievment = binding.textNumberAchievment;
        imageProfile = binding.profileImage;
        tv_username = binding.tvUsername;
        logout = binding.logout;
        edit = binding.edit;

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(user != null) {
                   showEditUsernameDialog();
               } else {
                   Toast.makeText(requireContext(), "Must Login First", Toast.LENGTH_SHORT).show();
               }
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
                }).create().show();
            }
        });

        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(user != null) {
                   openImageChooser();
               } else {
                   Toast.makeText(requireContext(), "Must Login First", Toast.LENGTH_SHORT).show();
               }
            }
        });

        if (user != null) {
            DocumentReference docRef = db.collection("user").document(user.getUid());
            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value != null && value.exists() && getActivity() != null) {
                        tv_username.setText(value.getString("username"));
                        List<DocumentReference> library = (List<DocumentReference>) value.get("library");
                        number_played.setText(String.valueOf(library.size()));
                        number_session.setText(value.getLong("statistic.game_time").toString());
                        number_achievment.setText(value.getLong("statistic.achievement").toString());
                        if(value.getString("image") != null) {
                            Glide.with(ProfileFragment.this).load(value.getString("image")).into(imageProfile);
                        } else {
                            Glide.with(ProfileFragment.this).load("https://firebasestorage.googleapis.com/v0/b/quiet-biplane-423907-k3.appspot.com/o/profile%2Fdefault.png?alt=media&token=68f08336-7d84-4a3c-8079-3851caf62768").into(imageProfile);
                        }
                    }
                }
            });
        }

        return root;
    }




    private void showEditUsernameDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_username, null);
        final EditText usernameInput = dialogView.findViewById(R.id.username_input);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit Username")
                .setView(dialogView)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newUsername = usernameInput.getText().toString().trim();
                        updateUsername(newUsername);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void updateUsername(String newUsername) {
        FirebaseUser user = mAuth.getCurrentUser();
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

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadImage();
        }
    }

    private void uploadImage() {
        if (imageUri != null) {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                String userId = user.getUid();
                StorageReference profileImageRef = storage.getReference().child("profile/" + userId + ".jpg");

                profileImageRef.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageUrl = uri.toString();
                                        updateProfileImage(imageUrl, userId);
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    private void updateProfileImage(String imageUrl, String userId) {
        // Update Firestore with new image URL
        db.collection("user").document(userId)
                .update("image", imageUrl)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(requireContext(), "Profile image updated", Toast.LENGTH_SHORT).show();

                        // If there was an old image, delete it
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            DocumentReference userDocRef = db.collection("user").document(user.getUid());
                            userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    String oldImageUrl = documentSnapshot.getString("image");
                                    if (oldImageUrl != null && !oldImageUrl.equals(imageUrl) && !oldImageUrl.equals("https://firebasestorage.googleapis.com/v0/b/quiet-biplane-423907-k3.appspot.com/o/profile%2Fdefault.png?alt=media&token=68f08336-7d84-4a3c-8079-3851caf62768")) {
                                        deleteOldImage(oldImageUrl);
                                    }
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), "Failed to update profile image", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteOldImage(String oldImageUrl) {
        StorageReference oldImageRef = storage.getReferenceFromUrl(oldImageUrl);
        oldImageRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
