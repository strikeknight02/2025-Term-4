package com.example.wowcher;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wowcher.classes.Reward;

public class ClaimedRewardAdapter extends RecyclerView.Adapter<ClaimedRewardViewHolder> {
    private Context context;
    private List<Reward> dataList;
    public void setSearchList(List<Reward> dataSearchList){
        this.dataList = dataSearchList;
        notifyDataSetChanged();
    }
    public ClaimedRewardAdapter(Context context, List<Reward> dataList){
        this.context = context;
        this.dataList = dataList;
    }
    @NonNull
    @Override
    public ClaimedRewardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.modified_item_voucher, parent, false);
        return new ClaimedRewardViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ClaimedRewardViewHolder holder, int position) {
        Reward reward = dataList.get(position);
        holder.rewardName.setText(reward.getName());
        holder.rewardDescription.setText(reward.getDescription());
        holder.rewardPoints.setText(reward.getPointsRequired() + " Points");  // Points required for the reward

        String userId = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();

        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("redeemedRewards")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                        holder.rewardCard.setOnClickListener(view -> {
                            // Log what is being passed to the Intent
                            Log.d("INTENT_DATA", "Description: " + reward.getDescription());
                            Log.d("INTENT_DATA", "Name: " + reward.getName());
                            Log.d("INTENT_DATA", "Points: " + reward.getPointsRequired());
                            Log.d("INTENT_DATA", "Timestamp: " + reward.getExpirationDate());


                            Intent intent = new Intent(context, ClaimedRewardActivity.class);
                            intent.putExtra("description", reward.getDescription());
                            intent.putExtra("name", reward.getName());
                            intent.putExtra("points", reward.getPointsRequired());
                            intent.putExtra("timestamp", reward.getExpirationDate());
                            context.startActivity(intent);
                        });
                });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


}
// ViewHolder class to hold the views for each item
class ClaimedRewardViewHolder extends RecyclerView.ViewHolder {
    TextView rewardName, rewardDescription, rewardPoints;
    CardView rewardCard;

    public ClaimedRewardViewHolder(@NonNull View itemView) {
        super(itemView);
        rewardName = itemView.findViewById(R.id.voucher_type);
        rewardDescription = itemView.findViewById(R.id.voucher_discount);
        rewardPoints = itemView.findViewById(R.id.voucher_points_required);
        rewardCard = itemView.findViewById(R.id.voucher_card);
    }
}
