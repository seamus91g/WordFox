package capsicum.game.wordfox.recyclerview_game_data.datamodels;

/**
 * Created by Gilroy
 */

public class WordData {
    private String letters;
    private String longestPossible;

    public WordData(String letters, String longestPossible){
        this.letters = letters;
        this.longestPossible = longestPossible;
    }
    public String getGivenLetters(){
        return letters;
    }
    public String getLongestPossibleWord(){
        return longestPossible;
    }
}
