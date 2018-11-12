package capsicum.game.wordfox.dataWordsRecycler;

import java.util.List;

/**
 * Created by Gilroy
 */

public class WordDataHeader{
    // Word found + how many times it was found
    private String wordSubmitted;
    private int numberTimesFound;
    private List<WordData> wordDataPoints;

    public WordDataHeader(String word, int numFound, List<WordData> data){
        this.wordSubmitted = word;
        this.numberTimesFound = numFound;
        this.wordDataPoints = data;
    }

    public String getWordSubmitted(){
        return wordSubmitted;
    }
    public int getNumberOfTimesFound(){
        return numberTimesFound;
    }
    public WordData getWordDataPoint(int position){
        return wordDataPoints.get(position);
    }
    public List<WordData> getChildList() {
        return wordDataPoints;
    }

}
