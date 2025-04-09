package com.example.wowcher;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wowcher.classes.Rewards;
import com.example.wowcher.R;

import java.util.AbstractCollection;
import java.util.List;

public class RewardsAdapter extends RecyclerView.Adapter<RewardViewHolder> {

    private final Context context;
    private List<Rewards> rewardList;
    private AbstractCollection<Object> dataList;

    // Constructor
    public RewardsAdapter(Context context, List<Rewards> rewardList) {
        this.context = context;
        this.rewardList = rewardList;
    }

    // Set search results or filtered list
    public void setSearchList(List<Rewards> rewardSearchList) {
        this.rewardList = rewardSearchList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RewardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_voucher, parent, false);
        return new RewardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardViewHolder holder, int position) {
        // Get the current reward object from the list
        Rewards reward = rewardList.get(position);

        // Set the values to the TextViews
        holder.rewardName.setText(reward.getName());  // Reward Name
        holder.rewardDescription.setText(reward.getDescription());  // Reward Description
        holder.rewardPoints.setText(reward.getPointsRequired() + " Points");  // Points required for the reward

        // Set click listener for each reward card
        holder.rewardCard.setOnClickListener(view -> {
            Intent intent = new Intent(context, RewardDetailActivity.class);
            // Pass the reward details to the next activity
            intent.putExtra("RewardId", reward.getRewardId());
            intent.putExtra("RewardName", reward.getName());
            intent.putExtra("RewardDescription", reward.getDescription());
            intent.putExtra("RewardPoints", reward.getPointsRequired());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return rewardList.size();
    }

    public void updateRewards(List<Rewards> rewardsList) {
        this.dataList.clear();
        this.dataList.addAll(rewardsList);
        notifyDataSetChanged();
    }



}
// ViewHolder class to hold the views for each item
class RewardViewHolder extends RecyclerView.ViewHolder {
    TextView rewardName, rewardDescription, rewardPoints;
    CardView rewardCard;

    public RewardViewHolder(@NonNull View itemView) {
        super(itemView);
        rewardName = itemView.findViewById(R.id.voucher_type);
        rewardDescription = itemView.findViewById(R.id.voucher_discount);
        rewardPoints = itemView.findViewById(R.id.voucher_points_required);
        rewardCard = itemView.findViewById(R.id.voucher_card);
    }
}
