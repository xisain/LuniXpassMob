package com.example.lunixpassmob.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.example.lunixpassmob.GameDetail;
import com.example.lunixpassmob.R;
import com.example.lunixpassmob.model.user.LibModel;

import java.util.List;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.ViewHolder> {

    private Context context;
    private List<LibModel> libraryList;

    public LibraryAdapter(Context context, List<LibModel> libraryList) {
        this.context = context;
        this.libraryList = libraryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.recycle_view_lib, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LibModel item = libraryList.get(position);
        Glide.with(context).load(item.getUrl()).into(holder.gameIcon);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GameDetail.class);
                intent.putExtra("id", item.getId());
                context.startActivity(intent);
                Log.w("TAG", "onClick: "+item.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return libraryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView gameIcon;
        MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            gameIcon = itemView.findViewById(R.id.gameIcon);
        }
    }
}
