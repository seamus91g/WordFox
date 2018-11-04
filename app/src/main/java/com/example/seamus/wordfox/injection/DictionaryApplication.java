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
            dictionary = new FoxDictionary("validWords_alph.txt", "letterFrequency.txt", getAssets());
        }else{
            dictionary.resetLetterPool();
        }
        return dictionary;
    }
}
