package capsicum.game.wordfox.player_switch;

import android.content.Context;
import android.content.Intent;

import capsicum.game.wordfox.GameData;
import capsicum.game.wordfox.GameInstance;
import capsicum.game.wordfox.PlayerIdentity;
import capsicum.game.wordfox.game_screen.GameActivity;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Gilroy
 */

public class PlayerSwitchPresenter implements PlayerSwitchContract.Listener {
    private int gameIndex;
    private PlayerSwitchContract.View view;
    private Context context;
    private final ArrayList<PlayerIdentity> playerList;
    private ArrayList<GameInstance> allGameInstances;
    private ArrayList<String> newPlayers = new ArrayList<>();
    private PlayerIdentity choosenPlayer;

    public PlayerSwitchPresenter(PlayerSwitchActivity activity, int gameIndex, ArrayList<GameInstance> allGameInstances, ArrayList<PlayerIdentity> existingPlayers) {
        this.view = activity;
        this.context = activity;
        this.gameIndex = gameIndex;
        this.allGameInstances = allGameInstances;
        choosenPlayer = new PlayerIdentity(allGameInstances.get(gameIndex).getID(), allGameInstances.get(gameIndex).getName());
        playerList = playerList(existingPlayers);
    }

    // Create a list of player names for the player to choose from
    private ArrayList<PlayerIdentity> playerList(ArrayList<PlayerIdentity> existingPlayers) {
        // track already chosen players so they can't be chosen again
        ArrayList<PlayerIdentity> previousPlayers = new ArrayList<>();
        ArrayList<PlayerIdentity> all = new ArrayList<>();
        for (int i = 0; i < gameIndex; i++) {
            previousPlayers.add(allGameInstances.get(i).getPlayer());
        }
        PlayerIdentity duePlayer = allGameInstances.get(gameIndex).getPlayer();
//        String thisPlayer = "Player " + (gameIndex + 1);
        if (!previousPlayers.contains(duePlayer)) {     // TODO: Can't be possible when we dont allow asynchronous player number choices
            all.add(duePlayer);
        }
        // List of available player names excluding already chosen ones
        for (PlayerIdentity playerID : existingPlayers) {
            if (containsPlayer(previousPlayers, playerID)) {
                continue;
            }
            all.add(playerID);
        }
        return all;
    }

    private boolean containsPlayer(ArrayList<PlayerIdentity> players, PlayerIdentity name) {
        for (PlayerIdentity p : players) {        // TODO: Seems like this check shouldn't be necessary
            if (p.ID.equals(name.ID)) {
                return true;
            }
        }
        return false;
    }

    private PlayerIdentity findPlayerIdentityFromChoice(String choice) {
        for (PlayerIdentity p : playerList) {
            if (p.username.equals(choice)) {
                return p;
            }
        }
        return null;
    }

    // Instruct the user of who to pass the game to
    public void setChoice(String choice) {
        if (choice.equals("") || choice.equals(choosenPlayer.username)) {
            return;
        }
        String nextPlayerMessage;
        // If choice from newPlayers, create new PlayerIdentity, add to PlayerList and remove choice from newPlayers
        if (newPlayers.contains(choice)) {
            choosenPlayer = new PlayerIdentity(UUID.randomUUID(), choice);
        } else {
            choosenPlayer = findPlayerIdentityFromChoice(choice);
        }
        assert choosenPlayer != null;   // Should not be possible
        if(allGameInstances.get(gameIndex).isOnline()){

            allGameInstances.set(gameIndex, new GameInstance(choosenPlayer.ID, choosenPlayer.username, gameIndex, allGameInstances.get(0).getRoundIDs(), allGameInstances.get(gameIndex).isOnline(), allGameInstances.get(gameIndex).isGroupOwner()));
        }else{

            allGameInstances.set(gameIndex, new GameInstance(choosenPlayer.ID, choosenPlayer.username, gameIndex, allGameInstances.get(0).getRoundIDs()));
        }

        nextPlayerMessage = "Pass the game to " + choice;
        view.displayMessage(nextPlayerMessage);
    }

    public void newPlayer(String name) {
        // Add at start of array, so first item on menu
        if (newPlayers.contains(name)) {
            newPlayers.remove(name);
        }
        newPlayers.add(0, name);
        setChoice(name);
        setupMenu();
    }

    public void setupMenu() {
        //create a list of items for the spinner.
        ArrayList<String> items = new ArrayList<>(newPlayers);
        for (PlayerIdentity player : playerList) {
            if (!items.contains(player.username)) {
                items.add(player.username);
            }
        }
        view.populateMenu(items);
    }

    public void startGame() {
        if(newPlayers.contains(choosenPlayer.username)){
            new GameData(context, choosenPlayer.ID).setUsername(choosenPlayer.username);
        }
        Intent gameIntent = new Intent(context, GameActivity.class);
        gameIntent.putExtra(GameActivity.GAME_INDEX, gameIndex);
        context.startActivity(gameIntent);
    }
}
