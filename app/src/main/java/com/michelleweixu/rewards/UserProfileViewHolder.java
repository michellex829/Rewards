package com.michelleweixu.rewards;

import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;

public class UserProfileViewHolder extends RecyclerView.ViewHolder{
    TextView name, position, rewardPoints;
    ImageView image;


    UserProfileViewHolder(View view) {
        super(view);
        name = view.findViewById(R.id.name_in_entry);
        position = view.findViewById(R.id.position_in_entry);
        rewardPoints = view.findViewById(R.id.rewardpoints_in_entry);
        image = view.findViewById(R.id.image_in_entry);
    }
}
