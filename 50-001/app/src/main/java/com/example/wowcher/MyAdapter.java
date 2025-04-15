package com.example.wowcher;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wowcher.classes.Voucher;
import com.example.wowcher.fragments.Home;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private Context context;
    private List<Voucher> dataList;
    public void setSearchList(List<Voucher> dataSearchList){
        this.dataList = dataSearchList;
        notifyDataSetChanged();
    }
    public MyAdapter(Context context, List<Voucher> dataList){
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

        String imageName = voucher.getImageName();
        int imageResId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
        if (imageResId != 0) {
            holder.recImage.setImageResource(imageResId);
        } else {
            holder.recImage.setImageResource(R.drawable.lebron_james);
        }

        String voucherId = voucher.getVoucherId();
        String userId = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Set a tag to identify the voucher
        holder.recCard.setTag(voucherId);

        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("redeemedVouchers")
                .document(voucherId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    // Check if this holder is still bound to the same voucher
                    if (!voucherId.equals(holder.recCard.getTag())) return;

                    if (documentSnapshot.exists()) {
                        holder.recTitle.setText(voucher.getTitle());
                        holder.recDesc.setText(voucher.getDetails());
                        holder.recID.setText(voucher.getVoucherId());
                        holder.recLang.setText("Redeemed");
                        holder.recCard.setClickable(false);
                        holder.recCard.setAlpha(0.5f);
                        holder.recCard.setOnClickListener(null); // remove listener
                    } else {
                        holder.recTitle.setText(voucher.getTitle());
                        holder.recDesc.setText(voucher.getDetails());
                        holder.recID.setText(voucher.getVoucherId());
                        holder.recLang.setText("Available");
                        holder.recCard.setClickable(true);
                        holder.recCard.setAlpha(1f);
                        holder.recCard.setOnClickListener(view -> {
                            Intent intent = new Intent(context, VoucherDetailActivity.class);
                            intent.putExtra("Id", voucher.getVoucherId());
                            intent.putExtra("Title", voucher.getTitle());
                            intent.putExtra("Desc", voucher.getDetails());
                            intent.putExtra("Points", voucher.getPointsReward());
                            intent.putExtra("Image", voucher.getImageName());
                            context.startActivity(intent);
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    if (!voucherId.equals(holder.recCard.getTag())) return;
                    holder.recLang.setText("Status Unknown");
                    holder.recCard.setClickable(false);
                });
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }


}
class MyViewHolder extends RecyclerView.ViewHolder{
    ImageView recImage;
    TextView recTitle, recDesc, recLang,recID;
    CardView recCard;
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        recImage = itemView.findViewById(R.id.recImage);
        recTitle = itemView.findViewById(R.id.recTitle);
        recDesc = itemView.findViewById(R.id.recDesc);
        recLang = itemView.findViewById(R.id.recLang);
        recCard = itemView.findViewById(R.id.recCard);
        recID = itemView.findViewById(R.id.recID);
    }
}