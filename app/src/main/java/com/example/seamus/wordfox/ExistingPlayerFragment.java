package com.example.seamus.wordfox;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlayerChoiceListener} interface
 * to handle interaction events.
 */
public class ExistingPlayerFragment extends Fragment {
    private RecyclerView rv;
    private LinearLayoutManager lm;
    private RecyclerView.Adapter rvAdapter;
    private PlayerChoiceListener mListener;

    public ExistingPlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View li = inflater.inflate(R.layout.fragment_existing_player, container, false);
        return li;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Activity activity = getActivity();
        ArrayList<PlayerIdentity> players = GameData.getNamedPlayerList(activity);
        rv = activity.findViewById(R.id.listExistingPlayers);
        rv.setHasFixedSize(true);
        lm = new LinearLayoutManager(activity);
        rv.setLayoutManager(lm);
        rvAdapter = new PlayersAdapter(players, mListener);
        rv.setAdapter(rvAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PlayerChoiceListener) {
            mListener = (PlayerChoiceListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    ////////////////////////////////////////

    private class PlayersAdapter extends RecyclerView.Adapter<PlayersAdapter.PlayerViewHolder> {
        private ArrayList<PlayerIdentity> dataset;
        private PlayerChoiceListener mListener;
        private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(BuildConfig.DEBUG && !(adapterView == null && view == null && l == 0)){
                    throw new AssertionError();
                }
                PlayerIdentity choosenPlayer = dataset.get(position);
                mListener.setChoice(choosenPlayer);
            }
        };

        PlayersAdapter(ArrayList<PlayerIdentity> dataset, PlayerChoiceListener mListener) {
            this.dataset = dataset;
            this.mListener = mListener;
        }

        public class PlayerViewHolder extends RecyclerView.ViewHolder {
            private final TextView playerNameTV;
            private final TextView playerRankTV;

            public PlayerViewHolder(@NonNull View itemView, AdapterView.OnItemClickListener listener) {
                super(itemView);
                playerNameTV = itemView.findViewById(R.id.data_player_name);
                playerRankTV = itemView.findViewById(R.id.data_player_rank);
                itemView.setOnClickListener(view -> listener.onItemClick(null, null, getAdapterPosition(), 0));
            }

            public void bindPlayer(PlayerIdentity player) {
                playerNameTV.setText(player.username);
                playerRankTV.setText(player.ID.toString());
            }
        }

        @NonNull
        @Override
        public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(getContext())
                    .inflate(R.layout.rv_data_player_header, viewGroup, false);
            return new PlayerViewHolder(v, listener);
        }

        @Override
        public void onBindViewHolder(@NonNull PlayerViewHolder viewHolder, int i) {
            viewHolder.bindPlayer(dataset.get(i));
        }

        @Override
        public int getItemCount() {
            return dataset.size();
        }
    }
}
