package capsicum.game.wordfox.screen_results_game;

import org.json.JSONObject;

interface GameEndWifiContract {
    void addReceivedWifiPlayerData(JSONObject newPlayerData);
}
