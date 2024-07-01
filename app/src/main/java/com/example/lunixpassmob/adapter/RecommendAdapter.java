package com.example.lunixpassmob.adapter;

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

import com.bumptech.glide.Glide;
import com.example.lunixpassmob.GameDetail;
import com.example.lunixpassmob.R;
import com.example.lunixpassmob.model.game.Game;

import java.util.List;

public class RecommendAdapter extends RecyclerView.Adapter<RecommendAdapter.recommendViewHolder> {

    private Context context;
    private List<Game> gameList;

    public RecommendAdapter(Context context, List<Game> gameList) {
        this.context = context;
        this.gameList = gameList;
    }

    @NonNull
    @Override
    public recommendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.recommended_for_you, parent, false);
       return new recommendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull recommendViewHolder holder, int position) {
        Game item = gameList.get(position);
        Glide.with(context).load(item.getGameImage()).into(holder.gameImage);
        holder.gameName.setText(item.getGameName());
        holder.gameDesc.setText(item.getGameDesc());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              item.getId();
              Intent intent = new Intent(context, GameDetail.class);
              intent.putExtra("id", item.getId());
              context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }

    public class recommendViewHolder extends RecyclerView.ViewHolder {
        ImageView gameImage;
        CardView cardView;
        TextView gameName;
        TextView gameDesc;

        public recommendViewHolder(@NonNull View itemView) {
            super(itemView);
            gameImage = itemView.findViewById(R.id.gameImage);
            cardView = itemView.findViewById(R.id.cardView);
            gameName = itemView.findViewById(R.id.gameName);
            gameDesc = itemView.findViewById(R.id.gameDesc);
        }
    }
}
