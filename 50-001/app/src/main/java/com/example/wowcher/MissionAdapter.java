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

import com.example.wowcher.classes.Missions;
import com.example.wowcher.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.List;

public class MissionAdapter extends RecyclerView.Adapter<MissionViewHolder> {

    private final Context context;
    private List<Missions> missionList;

    // Constructor
    public MissionAdapter(Context context, List<Missions> missionList) {
        this.context = context;
        this.missionList = missionList;
    }

    // Set the mission list
    public void setMissionList(List<Missions> missionList) {
        this.missionList = missionList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MissionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mission, parent, false);
        return new MissionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MissionViewHolder holder, int position) {
        Missions mission = missionList.get(position);

        // Set mission details to the views
        holder.missionNameTextView.setText(mission.getMissionName());
        holder.descriptionTextView.setText(mission.getDescription());
        holder.pointsRewardTextView.setText("Reward: " + mission.getPointsReward() + " points");
//        holder.criteriaTextView.setText("Criteria: " + mission.getCriteria());

        // Set the progress in the progress indicator
        holder.missionProgress.setProgress(mission.getProgress());

        // Set the progress text ("3 of 7")
        String progressText = mission.getProgress() + " of 100";  // Assuming progress is percentage based (0-100)
        holder.progressTextView.setText(progressText);

        // Set click listener for each mission card
        holder.missionCard.setOnClickListener(view -> {
//            Intent intent = new Intent(context, MissionDetailActivity.class);
//            // Pass mission details to the next activity
//            intent.putExtra("MissionId", mission.getMissionId());
//            intent.putExtra("MissionName", mission.getMissionName());
//            intent.putExtra("MissionDescription", mission.getDescription());
//            intent.putExtra("MissionPointsReward", mission.getPointsReward());
//            intent.putExtra("MissionCriteria", mission.getCriteria());
//            intent.putExtra("MissionProgress", mission.getProgress());
//            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return missionList.size();
    }
}

class MissionViewHolder extends RecyclerView.ViewHolder {

    TextView missionNameTextView;
    TextView descriptionTextView;
    TextView pointsRewardTextView;
    TextView criteriaTextView;
    TextView progressTextView;  // New TextView for progress text
    LinearProgressIndicator missionProgress;
    CardView missionCard;

    public MissionViewHolder(@NonNull View itemView) {
        super(itemView);
        missionNameTextView = itemView.findViewById(R.id.mission_title);
        descriptionTextView = itemView.findViewById(R.id.mission_progress_text);
        pointsRewardTextView = itemView.findViewById(R.id.mission_points);
         progressTextView = itemView.findViewById(R.id.mission_progress_text);  // Initialize the progress text view
        missionProgress = itemView.findViewById(R.id.mission_progress);
        missionCard = itemView.findViewById(R.id.mission_card);
    }
}