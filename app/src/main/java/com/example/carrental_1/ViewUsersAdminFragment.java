package com.example.carrental_1;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.carrental_1.data.model.User;
import com.example.carrental_1.databinding.FragmentViewUsersAdminBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class ViewUsersAdminFragment extends Fragment implements UsersAdminAdapter.OnUserItemClickListener {

    private FragmentViewUsersAdminBinding binding;
    private FirebaseFirestore db;
    private UsersAdminAdapter adapter;
    private List<User> userList;

    public ViewUsersAdminFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentViewUsersAdminBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        userList = new ArrayList<>();
        adapter = new UsersAdminAdapter(userList, this);

        binding.recyclerViewUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewUsers.setAdapter(adapter);

        fetchUsers();
    }

    private void fetchUsers() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        String currentUserId = currentUser.getUid();

        db.collection("users")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        userList.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            User user = document.toObject(User.class);
                            user.setId(document.getId());
                            // Exclude the current user from the results
                            if (!user.getId().equals(currentUserId)) {
                                userList.add(user);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to fetch users", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDeleteClick(User user) {
        db.collection("users").document(user.getId())
                .update("isDeleted", true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "User deleted successfully", Toast.LENGTH_SHORT).show();
                        fetchUsers();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to delete user", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onMakeAdminClick(User user) {
        db.collection("users").document(user.getId())
                .update("isAdmin", true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "User is now an admin", Toast.LENGTH_SHORT).show();
                        fetchUsers();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to make user admin", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onUnblockClick(User user) {
        db.collection("users").document(user.getId())
                .update("isDeleted", false)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "User unblocked successfully", Toast.LENGTH_SHORT).show();
                        fetchUsers();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to unblock user", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRemoveAdmin(User user) {
        db.collection("users").document(user.getId())
                .update("isAdmin", false)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "User unblocked successfully", Toast.LENGTH_SHORT).show();
                        fetchUsers();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to unblock user", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
