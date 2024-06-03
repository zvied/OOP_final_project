package com.example.carrental_1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.carrental_1.data.model.User;
import java.util.List;

public class UsersAdminAdapter extends RecyclerView.Adapter<UsersAdminAdapter.UserViewHolder> {

    private List<User> userList;
    private OnUserItemClickListener onUserItemClickListener;

    public UsersAdminAdapter(List<User> userList, OnUserItemClickListener onUserItemClickListener) {
        this.userList = userList;
        this.onUserItemClickListener = onUserItemClickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_admin, parent, false);
        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.userName.setText(user.getName() + " " +  user.getSurname());
        holder.userEmail.setText(user.getEmail());

        if (user.getIsDeleted()) {
            holder.unblockButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.GONE);
            holder.makeAdminButton.setVisibility(View.GONE);
        } else {
            holder.unblockButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.makeAdminButton.setVisibility(View.VISIBLE);
        }

        holder.deleteButton.setOnClickListener(v -> onUserItemClickListener.onDeleteClick(user));
        holder.makeAdminButton.setOnClickListener(v -> onUserItemClickListener.onMakeAdminClick(user));
        holder.unblockButton.setOnClickListener(v -> onUserItemClickListener.onUnblockClick(user));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userEmail;
        Button deleteButton, makeAdminButton, unblockButton;

        UserViewHolder(View view) {
            super(view);
            userName = view.findViewById(R.id.userName);
            userEmail = view.findViewById(R.id.userEmail);
            deleteButton = view.findViewById(R.id.deleteButton);
            makeAdminButton = view.findViewById(R.id.makeAdminButton);
            unblockButton = view.findViewById(R.id.unblockButton);
        }
    }

    public interface OnUserItemClickListener {
        void onDeleteClick(User user);
        void onMakeAdminClick(User user);
        void onUnblockClick(User user);
    }
}
