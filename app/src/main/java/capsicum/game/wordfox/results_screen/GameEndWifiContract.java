package capsicum.game.wordfox.results_screen;

import org.json.JSONObject;

interface GameEndWifiContract {
    void addReceivedWifiPlayerData(JSONObject newPlayerData);
}
