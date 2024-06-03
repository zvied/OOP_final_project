package com.example.carrental_1;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private EditText currentEmailEditText, newEmailEditText, currentPasswordEditText, newPasswordEditText;
    private Button updateButton;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        currentEmailEditText = view.findViewById(R.id.current_email);
        newEmailEditText = view.findViewById(R.id.new_email);
        currentPasswordEditText = view.findViewById(R.id.current_password);
        newPasswordEditText = view.findViewById(R.id.new_password);
        updateButton = view.findViewById(R.id.update_button);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            currentEmailEditText.setText(user.getEmail());
        }

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserProfile();
            }
        });

        return view;
    }

    private void updateUserProfile() {
        String currentEmail = currentEmailEditText.getText().toString().trim();
        String newEmail = newEmailEditText.getText().toString().trim();
        String currentPassword = currentPasswordEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(currentEmail) || TextUtils.isEmpty(currentPassword)) {
            Toast.makeText(getActivity(), "Current email and password are required", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            reauthenticateUser(user, currentEmail, currentPassword, newEmail, newPassword);
        }
    }

    private void reauthenticateUser(FirebaseUser user, String currentEmail, String currentPassword, String newEmail, String newPassword) {
        AuthCredential credential = EmailAuthProvider.getCredential(currentEmail, currentPassword);
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Re-authentication successful.");
                    if (!TextUtils.isEmpty(newEmail)) {
                        updateEmail(user, newEmail);
                    }
                    if (!TextUtils.isEmpty(newPassword)) {
                        updatePassword(user, newPassword);
                    }
                } else {
                    Log.e(TAG, "Re-authentication failed: " + task.getException());
                    Toast.makeText(getActivity(), "Re-authentication failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateEmail(FirebaseUser user, String newEmail) {
        user.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Email updated successfully.");
                    sendVerificationEmail(user, newEmail);
                    updateFirestoreEmail(user, newEmail);
                } else {
                    Log.e(TAG, "Failed to update email: " + task.getException());
                    Toast.makeText(getActivity(), "Failed to update email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendVerificationEmail(FirebaseUser user, String newEmail) {
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Verification email sent.");
                    Toast.makeText(getActivity(), "Verification email sent to " + newEmail, Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Failed to send verification email: " + task.getException());
                    Toast.makeText(getActivity(), "Failed to send verification email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateFirestoreEmail(FirebaseUser user, String newEmail) {
        String uid = user.getUid();
        db.collection("users").document(uid).update("email", newEmail)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Email updated in Firestore.");
                        Toast.makeText(getActivity(), "Email updated in Firestore", Toast.LENGTH_SHORT).show();
                        refreshUserDetails();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to update email in Firestore: " + e);
                        Toast.makeText(getActivity(), "Failed to update email in Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updatePassword(FirebaseUser user, String newPassword) {
        user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Password updated successfully.");
                    Toast.makeText(getActivity(), "Password updated", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Failed to update password: " + task.getException());
                    Toast.makeText(getActivity(), "Failed to update password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void refreshUserDetails() {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.refreshUserDetails();
        }
    }
}
