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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private final Context context;
    private List<Voucher> dataList;
    private final FirebaseFirestore db;

    public MyAdapter(Context context, List<Voucher> dataList) {
        this.context = context;
        this.dataList = dataList;
        this.db = FirebaseFirestore.getInstance();
    }

    public void setSearchList(List<Voucher> dataSearchList) {
        this.dataList = dataSearchList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Voucher voucher = dataList.get(position);
        String voucherId = voucher.getVoucherId();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Set Image
        int imageResId = context.getResources().getIdentifier(
                voucher.getImageName(), "drawable", context.getPackageName()
        );
        holder.recImage.setImageResource(imageResId != 0 ? imageResId : R.drawable.lebron_james);

        // Tag the card for consistency check
        holder.recCard.setTag(voucherId);

        // Check redemption status
        checkIfVoucherRedeemed(userId, voucherId, (isRedeemed, errorMessage) -> {
            if (!voucherId.equals(holder.recCard.getTag())) return; // Avoid recycled view bugs

            holder.recTitle.setText(voucher.getTitle());
            holder.recDesc.setText(voucher.getDetails());
            holder.recID.setText(voucher.getVoucherId());

            if (errorMessage != null) {
                holder.recLang.setText("Status Unknown");
                holder.recCard.setAlpha(0.5f);
                holder.recCard.setClickable(false);
                return;
            }

            if (isRedeemed) {
                holder.recLang.setText("Redeemed");
                holder.recCard.setAlpha(0.5f);
                holder.recCard.setClickable(false);
                holder.recCard.setOnClickListener(null);
            } else {
                holder.recLang.setText("Available");
                holder.recCard.setAlpha(1f);
                holder.recCard.setClickable(true);
                holder.recCard.setOnClickListener(view -> {
                    Intent intent = new Intent(context, VoucherDetailActivity.class);
                    intent.putExtra("Id", voucher.getVoucherId());
                    intent.putExtra("Title", voucher.getTitle());
                    intent.putExtra("Desc", voucher.getDetails());
                    intent.putExtra("Points", voucher.getPointsReward());
                    intent.putExtra("Image", voucher.getImageName());
                    intent.putExtra("Location", voucher.getLocationId());
                    context.startActivity(intent);
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    // Firestore method to check if the voucher is redeemed
    private void checkIfVoucherRedeemed(String userId, String voucherId, VoucherRedeemedCallback callback) {
        DocumentReference voucherRef = db.collection("users")
                .document(userId)
                .collection("redeemedVouchers")
                .document(voucherId);

        voucherRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    boolean isRedeemed = documentSnapshot.exists();
                    callback.onChecked(isRedeemed, null);
                })
                .addOnFailureListener(e -> {
                    callback.onChecked(false, "Failed to load redemption status");
                });
    }

    // Callback interface
    public interface VoucherRedeemedCallback {
        void onChecked(boolean isRedeemed, String errorMessage);
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