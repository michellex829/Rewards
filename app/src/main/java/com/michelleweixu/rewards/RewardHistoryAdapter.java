package com.michelleweixu.rewards;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.michelleweixu.rewards.userdata.UserReward;

import java.util.List;

public class RewardHistoryAdapter  extends RecyclerView.Adapter<RewardHistoryViewHolder> {
    public List<UserReward> rewardList;
    private ViewProfileActivity act;

    RewardHistoryAdapter(List<UserReward> rewardList, ViewProfileActivity act) {
        this.rewardList = rewardList;
        this.act = act;
    }

    @NonNull
    @Override
    public RewardHistoryViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reward_history_entry, parent, false);
        return new RewardHistoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardHistoryViewHolder holder, int position) {
        UserReward r = rewardList.get(position);

        holder.name.setText(r.giverName);
        holder.date.setText(r.awardDate);
        holder.rewardPoints.setText(String.valueOf(r.amount));
        holder.comment.setText(String.valueOf(r.note));
    }

    @Override
    public int getItemCount() {
        if (rewardList != null && rewardList.size() != 0)
            return rewardList.size();
        return 0;
    }
}

