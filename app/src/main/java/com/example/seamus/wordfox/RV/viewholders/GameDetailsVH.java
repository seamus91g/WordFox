package com.example.seamus.wordfox.RV.viewholders;

import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.view.View;
import android.widget.TextView;

import com.example.seamus.wordfox.PlayerIdentity;
import com.example.seamus.wordfox.R;
import com.example.seamus.wordfox.RV.BaseWFViewHolder;
import com.example.seamus.wordfox.RV.DataListItem;
import com.example.seamus.wordfox.RV.RVTypes.TypeGamesDetail;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class GameDetailsVH extends BaseWFViewHolder {
    private final TextView letters1TV;
    private final TextView letters2TV;
    private final TextView letters3TV;
    private final TextView bestPossible1;
    private final TextView bestPossible2;
    private final TextView bestPossible3;
    private final ConstraintLayout rootLayout;
    private Map<UUID, ArrayList<String>> wordsPerPlayer;

    public GameDetailsVH(View v) {
        super(v);
        rootLayout = (ConstraintLayout) v;
        letters1TV = v.findViewById(R.id.data_letters_1);
        letters2TV = v.findViewById(R.id.data_letters_2);
        letters3TV = v.findViewById(R.id.data_letters_3);
        bestPossible1 = v.findViewById(R.id.best_possible_1);
        bestPossible2 = v.findViewById(R.id.best_possible_2);
        bestPossible3 = v.findViewById(R.id.best_possible_3);
    }

    @Override
    public void bindType(DataListItem item) {
        TypeGamesDetail gameDetail = (TypeGamesDetail) item;    // TODO: Shouldn't need to cast if correctly using generics
        wordsPerPlayer = gameDetail.getPlayerWords();
        letters1TV.setText(gameDetail.getLetters(0));
        letters2TV.setText(gameDetail.getLetters(1));
        letters3TV.setText(gameDetail.getLetters(2));
        bestPossible1.setText(gameDetail.getBestPossible(0));
        bestPossible2.setText(gameDetail.getBestPossible(1));
        bestPossible3.setText(gameDetail.getBestPossible(2));
        displayPlayerDetails(gameDetail.getPlayers());
    }

    private void displayPlayerDetails(ArrayList<PlayerIdentity> players) {
        Group singleRow;
        int maxIndex = players.size() - 1;
        switch (maxIndex) {
            case 0:
                singleRow = rootLayout.findViewById(R.id.game_details_row_1);
                singleRow.setVisibility(View.GONE);
            case 1:
                singleRow = rootLayout.findViewById(R.id.game_details_row_2);
                singleRow.setVisibility(View.GONE);
            case 2:
                singleRow = rootLayout.findViewById(R.id.game_details_row_3);
                singleRow.setVisibility(View.GONE);
            case 3:
                singleRow = rootLayout.findViewById(R.id.game_details_row_4);
                singleRow.setVisibility(View.GONE);
            case 4:
                singleRow = rootLayout.findViewById(R.id.game_details_row_5);
                singleRow.setVisibility(View.GONE);
            case 5:
                singleRow = rootLayout.findViewById(R.id.game_details_row_6);
                singleRow.setVisibility(View.GONE);
        }
        switch (maxIndex) {
            case 5:
                singleRow = rootLayout.findViewById(R.id.game_details_row_6);
                singleRow.setVisibility(View.VISIBLE);
                populateRow(players.get(5), 5);
            case 4:
                singleRow = rootLayout.findViewById(R.id.game_details_row_5);
                singleRow.setVisibility(View.VISIBLE);
                populateRow(players.get(4), 4);
            case 3:
                singleRow = rootLayout.findViewById(R.id.game_details_row_4);
                singleRow.setVisibility(View.VISIBLE);
                populateRow(players.get(3), 3);
            case 2:
                singleRow = rootLayout.findViewById(R.id.game_details_row_3);
                singleRow.setVisibility(View.VISIBLE);
                populateRow(players.get(2), 2);
            case 1:
                singleRow = rootLayout.findViewById(R.id.game_details_row_2);
                singleRow.setVisibility(View.VISIBLE);
                populateRow(players.get(1), 1);
            case 0:
                singleRow = rootLayout.findViewById(R.id.game_details_row_1);
                singleRow.setVisibility(View.VISIBLE);
                populateRow(players.get(0), 0);
        }
    }

    private void populateRow(PlayerIdentity player, int playerNumber) {
        String tag = "game_details_player_" + (playerNumber+1) + "_name";
        TextView name = (TextView) rootLayout.findViewWithTag(tag);
        name.setText(player.username);
        ArrayList<String> words = wordsPerPlayer.get(player.ID);
        for (int i = 0; i < words.size(); ++i) {
            tag  = "best_found_" + (i+1) + "_player_" + (playerNumber+1);
            TextView word = (TextView) rootLayout.findViewWithTag(tag);
            word.setText(words.get(i));
        }
    }

}
