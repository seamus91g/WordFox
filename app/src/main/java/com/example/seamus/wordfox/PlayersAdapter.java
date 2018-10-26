package com.example.seamus.wordfox;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlayersAdapter extends RecyclerView.Adapter<PlayersAdapter.PlayerViewHolder> {

    private ArrayList<PlayerIdentity> dataset;
    private PlayerChoiceListener.FragmentView mListener;
    private ArrayList<Bitmap> profilePics;

    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            if (BuildConfig.DEBUG && !(adapterView == null && view == null && l == 0)) {
                throw new AssertionError();
            }
            PlayerIdentity choosenPlayer = dataset.get(position);
            mListener.setChoice(choosenPlayer);
        }
    };

    PlayersAdapter(ArrayList<PlayerIdentity> dataset, ArrayList<Bitmap> profilePics, PlayerChoiceListener.FragmentView mListener) {
        this.dataset = dataset;
        this.profilePics = profilePics;
        this.mListener = mListener;
    }

    public class PlayerViewHolder extends RecyclerView.ViewHolder {
        private final TextView playerNameTV;
        private final TextView playerRankTV;
        private final CircleImageView playerImage;

        public PlayerViewHolder(@NonNull View itemView, AdapterView.OnItemClickListener listener) {
            super(itemView);
            playerNameTV = itemView.findViewById(R.id.data_player_name);
            playerRankTV = itemView.findViewById(R.id.data_player_rank);
            playerImage = itemView.findViewById(R.id.data_page_player_pic);
            itemView.setOnClickListener(view -> listener.onItemClick(null, null, getAdapterPosition(), 0));
        }

        public void bindPlayer(PlayerIdentity player, Bitmap image) {
            playerNameTV.setText(player.username);
            playerRankTV.setText(player.rank.foxRank);
            playerImage.setImageBitmap(image);
        }
    }

    @NonNull
    @Override
    public PlayersAdapter.PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.rv_data_player_header, viewGroup, false);
        return new PlayersAdapter.PlayerViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayersAdapter.PlayerViewHolder viewHolder, int i) {
        viewHolder.bindPlayer(dataset.get(i), profilePics.get(i));
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

}
