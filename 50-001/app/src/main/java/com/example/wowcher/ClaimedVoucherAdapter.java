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

public class ClaimedVoucherAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private Context context;
    private List<Voucher> dataList;
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

        String voucherId = voucher.getVoucherId();
        String userId = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();

        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("redeemedVouchers")
                .document(voucherId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Not redeemed
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
                    }
                    int imageResId = context.getResources().getIdentifier(voucher.getImageName(), "drawable", context.getPackageName());

                    if (imageResId != 0) {
                        holder.recImage.setImageResource(imageResId);
                    } else {
                        holder.recImage.setImageResource(R.drawable.lebron_james); // fallback
                    }

                })
                .addOnFailureListener(e -> {
                    holder.recLang.setText("Status Unknown");
                    holder.recCard.setClickable(false);
                });
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