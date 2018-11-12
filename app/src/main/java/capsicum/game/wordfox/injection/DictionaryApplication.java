package capsicum.game.wordfox.injection;

import android.app.Application;

import capsicum.game.wordfox.data.FoxDictionary;
import capsicum.game.wordfox.data.Diction;

/**
 * Created by Gilroy
 */

public class DictionaryApplication extends Application {
    private FoxDictionary dictionary = null;
    public Diction getDictionary(){
        if(dictionary == null){
            dictionary = new FoxDictionary("word_list_with_alphagram.txt", "letterFrequency.txt", getAssets());
        }else{
            dictionary.resetLetterPool();
        }
        return dictionary;
    }
}
