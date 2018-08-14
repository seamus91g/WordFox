package com.example.seamus.wordfox.RV.viewholders;

import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.view.View;
import android.widget.TextView;

import com.example.seamus.wordfox.R;
import com.example.seamus.wordfox.RV.BaseWFViewHolder;
import com.example.seamus.wordfox.RV.DataListItem;
import com.example.seamus.wordfox.RV.RVTypes.TypeGamesDetail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameDetailsVH extends BaseWFViewHolder {
    private final TextView letters1TV;
    private final TextView letters2TV;
    private final TextView letters3TV;
    private final TextView bestPossible1;
    private final TextView bestPossible2;
    private final TextView bestPossible3;
    private final ConstraintLayout rootLayout;
    private Map<String, ArrayList<String>> wordsPerPlayer;

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

    private void displayPlayerDetails(List<String> playerNames) {
        Group singleRow;
        int maxIndex = playerNames.size() - 1;
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
                populateRow(playerNames.get(5), 5);
            case 4:
                singleRow = rootLayout.findViewById(R.id.game_details_row_5);
                singleRow.setVisibility(View.VISIBLE);
                populateRow(playerNames.get(4), 4);
            case 3:
                singleRow = rootLayout.findViewById(R.id.game_details_row_4);
                singleRow.setVisibility(View.VISIBLE);
                populateRow(playerNames.get(3), 3);
            case 2:
                singleRow = rootLayout.findViewById(R.id.game_details_row_3);
                singleRow.setVisibility(View.VISIBLE);
                populateRow(playerNames.get(2), 2);
            case 1:
                singleRow = rootLayout.findViewById(R.id.game_details_row_2);
                singleRow.setVisibility(View.VISIBLE);
                populateRow(playerNames.get(1), 1);
            case 0:
                singleRow = rootLayout.findViewById(R.id.game_details_row_1);
                singleRow.setVisibility(View.VISIBLE);
                populateRow(playerNames.get(0), 0);
        }
    }

    public void populateRow(String player, int playerNumber) {

        String tag = "game_details_player_" + (playerNumber+1) + "_name";
        TextView name = (TextView) rootLayout.findViewWithTag(tag);
        name.setText(player);
        ArrayList<String> words = wordsPerPlayer.get(player);
        for (int i = 0; i < words.size(); ++i) {
            tag  = "best_found_" + (i+1) + "_player_" + (playerNumber+1);
            TextView word = (TextView) rootLayout.findViewWithTag(tag);
            word.setText(words.get(i));
        }
    }

/*    private View addplayer(String player, ArrayList<String> playerWords, View parentConstraint) {
        // Create textviews. Add to constraint layout. Set constraints.
        TextView playerTV = (TextView) LayoutInflater.from(rootLayout.getContext())
                .inflate(R.layout.game_detail_tv, rootLayout, false);
        playerTV.setText(player);
        TextView playerRound1TV = (TextView) LayoutInflater.from(rootLayout.getContext())
                .inflate(R.layout.game_detail_tv, rootLayout, false);
        playerRound1TV.setText(playerWords.get(0));
        TextView playerRound2TV = (TextView) LayoutInflater.from(rootLayout.getContext())
                .inflate(R.layout.game_detail_tv, rootLayout, false);
        playerRound2TV.setText(playerWords.get(1));
        TextView playerRound3TV = (TextView) LayoutInflater.from(rootLayout.getContext())
                .inflate(R.layout.game_detail_tv, rootLayout, false);
        playerRound3TV.setText(playerWords.get(2));
        Log.d(WFAdapter.TAG, "Added player: " + player);
        playerTV.setId(uniqueIDs.pop());
        playerRound1TV.setId(uniqueIDs.pop());
        playerRound2TV.setId(uniqueIDs.pop());
        playerRound3TV.setId(uniqueIDs.pop());
        rootLayout.addView(playerTV);
        rootLayout.addView(playerRound1TV);
        rootLayout.addView(playerRound2TV);
        rootLayout.addView(playerRound3TV);
        ConstraintSet set = new ConstraintSet();
        set.clone(rootLayout);
        set.connect(playerTV.getId(), ConstraintSet.TOP, parentConstraint.getId(), ConstraintSet.BOTTOM, 0);
        set.connect(playerTV.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
        set.connect(playerTV.getId(), ConstraintSet.RIGHT, playerRound1TV.getId(), ConstraintSet.LEFT, 0);
        set.connect(playerRound1TV.getId(), ConstraintSet.TOP, parentConstraint.getId(), ConstraintSet.BOTTOM, 0);
        set.connect(playerRound2TV.getId(), ConstraintSet.TOP, parentConstraint.getId(), ConstraintSet.BOTTOM, 0);
        set.connect(playerRound3TV.getId(), ConstraintSet.TOP, parentConstraint.getId(), ConstraintSet.BOTTOM, 0);
        set.connect(playerRound1TV.getId(), ConstraintSet.LEFT, playerTV.getId(), ConstraintSet.RIGHT, 0);
        set.connect(playerRound2TV.getId(), ConstraintSet.LEFT, playerRound1TV.getId(), ConstraintSet.RIGHT, 0);
        set.connect(playerRound3TV.getId(), ConstraintSet.LEFT, playerRound2TV.getId(), ConstraintSet.RIGHT, 0);
        set.connect(playerRound1TV.getId(), ConstraintSet.RIGHT, playerRound2TV.getId(), ConstraintSet.LEFT, 0);
        set.connect(playerRound2TV.getId(), ConstraintSet.RIGHT, playerRound3TV.getId(), ConstraintSet.LEFT, 0);
        set.connect(playerRound3TV.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
        set.applyTo(rootLayout);
        return playerTV;
    }*/

}
