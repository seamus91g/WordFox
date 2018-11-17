package capsicum.game.wordfox;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

import timber.log.Timber;

public class WifiGameInstance implements GameDetails {
    private ArrayList<String> bestWordsPossible;
    private ArrayList<String> letters;
    private UUID id;
    private String name;
    private ArrayList<String> wordsFound;
    private int highestPossibleScore = 0;
    private int totalScore = 0;

    public WifiGameInstance(JSONObject wifiPlayer, ArrayList<String> bestWordsPossible, ArrayList<String> letters){
        this.bestWordsPossible = bestWordsPossible;
        this.letters = letters;

        parseJSONPlayerDetails(wifiPlayer);
    }

    private void parseJSONPlayerDetails(JSONObject wifiPlayer){
        try {
            name = (String) wifiPlayer.get(GameInstance.PLAYER_NAME);
            id = UUID.fromString((String) wifiPlayer.get(GameInstance.PLAYER_ID));
            wordsFound = new ArrayList<>();
            JSONArray jArray = wifiPlayer.getJSONArray(GameInstance.BEST_WORDS);
            for(int i=0; i<jArray.length(); ++i){
                wordsFound.add(jArray.getString(i));
            }
        } catch (JSONException e) {
            Timber.d( "WifiGameInstance : Failed to parse json");
            e.printStackTrace();
        }
    }

    @Override
    public UUID getID() {
        return id;
    }

    @Override
    public int getHighestPossibleScore() {
        if(highestPossibleScore == 0){
            for (String word : bestWordsPossible){
                highestPossibleScore += word.length();
            }
        }
        return highestPossibleScore;
    }

    @Override
    public int getTotalScore() {
        if(totalScore == 0){
            for (String word : wordsFound){
                totalScore += word.length();
            }
        }
        return totalScore;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getRoundWord(int i) {
        return wordsFound.get(i);
    }

    @Override
    public String getLetters(int i) {
        return letters.get(i);
    }
}
