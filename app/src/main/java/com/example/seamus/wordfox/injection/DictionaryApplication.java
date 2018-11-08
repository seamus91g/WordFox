package com.example.seamus.wordfox.injection;

import android.app.Application;

import com.example.seamus.wordfox.data.FoxDictionary;
import com.example.seamus.wordfox.data.Diction;

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
