package com.example.seamus.wordfox.player_switch;

import android.content.Context;
import android.content.Intent;

import com.example.seamus.wordfox.GameInstance;
import com.example.seamus.wordfox.game_screen.GameActivity;

import java.util.ArrayList;

/**
 * Created by Gilroy on 4/25/2018.
 */

public class PlayerSwitchPresenter implements PlayerSwitchContract.Listener {
    private int gameIndex;
    private PlayerSwitchContract.View view;
    private Context context;
    private final ArrayList<String> playerList;
    private ArrayList<GameInstance> allGameInstances;
    private ArrayList<String> newPlayers = new ArrayList<>();

    public PlayerSwitchPresenter(PlayerSwitchActivity activity, int gameIndex, ArrayList<GameInstance> allGameInstances, ArrayList<String> existingPlayers) {
        this.view = activity;
        this.context = activity;
        this.gameIndex = gameIndex;
        this.allGameInstances = allGameInstances;
        playerList = playerList(existingPlayers);
    }
    // Create a list of player names for the player to choose from
    private ArrayList<String> playerList(ArrayList<String> existingPlayers){
        // track already chosen players so they can't be chosen again
        ArrayList<String> previousPlayers = new ArrayList<>();
        ArrayList<String> all = new ArrayList<>();
        for (int i = 0; i < gameIndex; i++) {
            previousPlayers.add(allGameInstances.get(i).getPlayerName());
        }
        String thisPlayer = "Player " + (gameIndex + 1);
        if(!previousPlayers.contains(thisPlayer)){
            all.add(thisPlayer);
        }
        // List of available player names excluding already chosen ones
        for (String playerID : existingPlayers) {
            if (previousPlayers.contains(playerID)) {
                continue;
            }
            all.add(playerID);
        }
        return all;
    }
    // Instruct the user of who to pass the game to
    public void setChoice(String choice) {
        String nextPlayerMessage;
        if (choice.equals("")) {
            nextPlayerMessage = "Pass the game to player " + (gameIndex + 1);
        } else {
            allGameInstances.set(gameIndex, new GameInstance(choice, gameIndex, allGameInstances.get(0).getRoundIDs()));
            nextPlayerMessage = "Pass the game to " + choice;
        }
        view.displayMessage(nextPlayerMessage);
    }

    public void newPlayer(String name) {
        // Add at start of array, so first item on menu
        if (newPlayers.contains(name)) {
            newPlayers.remove(name);
        }
        newPlayers.add(0, name);
        setupMenu();
    }

    public void setupMenu() {
        //create a list of items for the spinner.
        ArrayList<String> items = new ArrayList<String>();
        items.addAll(newPlayers);
        for (String player : playerList){
            if (!items.contains(player)){
                items.add(player);
            }
        }
        view.populateMenu(items);
    }
    public void startGame() {
        Intent gameIntent = new Intent(context, GameActivity.class);
        gameIntent.putExtra("game_index", gameIndex);
        context.startActivity(gameIntent);
    }
}
