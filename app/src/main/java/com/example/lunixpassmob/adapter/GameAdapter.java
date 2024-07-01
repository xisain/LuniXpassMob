package com.example.lunixpassmob.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lunixpassmob.GameDetail;
import com.example.lunixpassmob.model.game.Game;
import com.example.lunixpassmob.R;


import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {
    private Context context;
    private List<Game> gameList;
    public GameAdapter(Context context, List<Game> gameList) {
        this.context = context;
        this.gameList = gameList;
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_view_game, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        Game Item = gameList.get(position);
        Glide.with(context).load(Item.getGameImage()).into(holder.gameImage);
        holder.cardView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GameDetail.class);
                intent.putExtra("id", gameList.get(holder.getAdapterPosition()).getId());
                context.startActivity(intent);
                Log.w("Click", "onClick: " + gameList.get(holder.getAdapterPosition()).getId());
                Toast toast = Toast.makeText(context, gameList.get(holder.getAdapterPosition()).getGameName(), Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }

    public void filterList(List<Game> filteredList) {
        gameList = filteredList;
        notifyDataSetChanged();
    }
    public static class GameViewHolder extends RecyclerView.ViewHolder {
        ImageView gameImage;
        CardView cardView;
        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.gameRec);
            gameImage = itemView.findViewById(R.id.gameImage);
        }
    }
}
