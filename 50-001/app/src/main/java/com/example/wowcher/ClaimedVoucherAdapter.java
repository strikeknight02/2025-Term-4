package com.example.wowcher;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wowcher.classes.Voucher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ClaimedVoucherAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private Context context;
    private List<Voucher> dataList;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    public void setSearchList(List<Voucher> dataSearchList){
        this.dataList = dataSearchList;
        notifyDataSetChanged();
    }
    public ClaimedVoucherAdapter(Context context, List<Voucher> dataList){
        this.context = context;
        this.dataList = dataList;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Voucher voucher = dataList.get(position);
        holder.recTitle.setText(voucher.getTitle());
        holder.recDesc.setText(voucher.getDetails());

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    if (dataList.isEmpty()){
        holder.recLang.setText("Status Unknown");
        holder.recCard.setClickable(false);
    } else {
        holder.recLang.setText("Redeemed");
        holder.recCard.setAlpha(0.5f);

        holder.recCard.setOnClickListener(view -> {
            Intent intent = new Intent(context, ClaimedVoucherActivity.class);
            intent.putExtra("Id", voucher.getVoucherId());
            intent.putExtra("Title", voucher.getTitle());
            intent.putExtra("Desc", voucher.getDetails());
            intent.putExtra("Points", voucher.getPointsReward());
            intent.putExtra("Image", voucher.getImageName());
            intent.putExtra("Code", voucher.getCode());
            context.startActivity(intent);
        });

        int imageResId = context.getResources().getIdentifier(voucher.getImageName(), "drawable", context.getPackageName());

        if (imageResId != 0) {
        holder.recImage.setImageResource(imageResId);
        } else {
        holder.recImage.setImageResource(R.drawable.lebron_james); // fallback
        }

}

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


}
class ClaimedVoucherHolder extends RecyclerView.ViewHolder{
    ImageView recImage;
    TextView recTitle, recDesc, recLang,recID;
    CardView recCard;
    public ClaimedVoucherHolder(@NonNull View itemView) {
        super(itemView);
        recImage = itemView.findViewById(R.id.recImage);
        recTitle = itemView.findViewById(R.id.recTitle);
        recDesc = itemView.findViewById(R.id.recDesc);
        recLang = itemView.findViewById(R.id.recLang);
        recCard = itemView.findViewById(R.id.recCard);
    }
}