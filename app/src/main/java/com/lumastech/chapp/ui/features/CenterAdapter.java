package com.lumastech.chapp.ui.features;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lumastech.chapp.CenterViewHolder;
import com.lumastech.chapp.Models.Center;
import com.lumastech.chapp.R;

import java.util.ArrayList;
import java.util.List;

public class CenterAdapter extends RecyclerView.Adapter<CenterViewHolder> {
    Context context;
    ArrayList<Center> items;

    private final SelectListener listener;

    public CenterAdapter(SelectListener listener, ArrayList<Center> items, Context context) {
        this.listener = listener;
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public CenterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CenterViewHolder(LayoutInflater.from(context).inflate(R.layout.center_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CenterViewHolder holder, int position) {
        holder.name.setText(items.get(position).getName());
        holder.address.setText(items.get(position).getAddress());
        holder.phone.setText(items.get(position).getPhone());
        holder.imageView.setImageResource(R.drawable.img_1);
//        Glide.with(context).load(items.get(position).getImage()).into(holder.imageView);
        holder.itemCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClicked(items.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
