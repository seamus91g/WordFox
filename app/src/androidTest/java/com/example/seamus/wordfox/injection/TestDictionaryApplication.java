package com.example.seamus.wordfox.injection;

import com.example.seamus.wordfox.data.Diction;
import com.example.seamus.wordfox.data.local.InMemoryDictionary;

/**
 * Created by Gilroy on 4/13/2018.
 */

public class TestDictionaryApplication extends DictionaryApplication{
    private final Diction dictionary = new InMemoryDictionary();
    @Override
    public Diction getDictionary() {
        return dictionary;
    }
}
