package com.lumastech.chapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class CenterViewHolder extends RecyclerView.ViewHolder {
    public TextView name;
    public TextView address;
    public TextView phone;
    public CardView itemCard;
    public ImageView imageView;
    public CenterViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.name);
        address = itemView.findViewById(R.id.address);
        phone = itemView.findViewById(R.id.phone);
        itemCard = itemView.findViewById(R.id.itemCard);
        imageView = itemView.findViewById(R.id.imageView);
    }
}
