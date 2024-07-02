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

import java.util.ArrayList;
import java.util.List;

public class searchAdapter extends RecyclerView.Adapter<searchAdapter.searchViewHolder> {
    private Context context;
    private List<Game> gameList;

    public searchAdapter(Context context, List<Game> gameList) {
        this.context = context;
        this.gameList = gameList;
    }

    @NonNull
    @Override
    public searchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_view_search, parent, false);
        return new searchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull searchViewHolder holder, int position) {
        Game game = gameList.get(position);
        holder.desc.setText(game.getGameDesc());
        holder.Title.setText(game.getGameName());
        Glide.with(context).load(game.getGameImage()).into(holder.imageView);
        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, GameDetail.class);
                intent.putExtra("id",game.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }

    public void searchDataList(ArrayList<Game> searchList){
        gameList = searchList;
        notifyDataSetChanged();
    }

    public class searchViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        CardView cv;
        TextView Title, desc;

        public searchViewHolder(@NonNull View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cardView);
            imageView = itemView.findViewById(R.id.gameImage);
            Title = itemView.findViewById(R.id.gameName);
            desc = itemView.findViewById(R.id.gameDesc);
        }
    }
}
