package com.michelleweixu.rewards;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class RewardHistoryViewHolder extends RecyclerView.ViewHolder{
    TextView name, date, rewardPoints, comment;

    RewardHistoryViewHolder(View view) {
        super(view);
        name = view.findViewById(R.id.nameInHistoryEntry);
        date = view.findViewById(R.id.dateInHistoryEntry);
        rewardPoints = view.findViewById(R.id.pointsInHistoryEntry);
        comment = view.findViewById(R.id.commentInHistoryEntry);
    }
}
