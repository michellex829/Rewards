package com.michelleweixu.rewards;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.michelleweixu.rewards.userdata.UserProfile;

import java.util.List;

public class UserProfileAdapter  extends RecyclerView.Adapter<UserProfileViewHolder> {
    public List<UserProfile> userList;
    private LeaderboardActivity act;
    public UserProfile loggedInUser;

    UserProfileAdapter(List<UserProfile> userList, LeaderboardActivity act, UserProfile user) {
        this.userList = userList;
        this.act = act;
        this.loggedInUser = user;
    }

    @NonNull
    @Override
    public UserProfileViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_profile_entry, parent, false);

        itemView.setOnClickListener(act);
        return new UserProfileViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserProfileViewHolder holder, int position) {
        UserProfile user = userList.get(position);

        holder.name.setText(user.lastName + ", " + user.firstName);
        holder.position.setText(user.position + ", " + user.department);
        holder.rewardPoints.setText(String.valueOf(user.pointsAwarded));

        if ((user.username).equals(loggedInUser.username)) {
            holder.name.setTextColor(Color.parseColor("#FF781F"));
            holder.position.setTextColor(Color.parseColor("#FF781F"));
            holder.rewardPoints.setTextColor(Color.parseColor("#FF781F"));
        } else {
                holder.name.setTextColor(Color.parseColor("#FF000000"));
                holder.position.setTextColor(Color.parseColor("#FF000000"));
                holder.rewardPoints.setTextColor(Color.parseColor("#FF000000"));
                }

        // Set user's profile image in the entry
        if (user.imageString64 == null) return;
        try {
            byte[] imageBytes = Base64.decode(user.imageString64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holder.image.setImageBitmap(bitmap);
        } catch (Exception e) {
        e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}

