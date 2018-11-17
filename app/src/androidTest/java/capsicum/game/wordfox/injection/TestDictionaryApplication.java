package capsicum.game.wordfox.injection;

import capsicum.game.wordfox.data.Diction;
import capsicum.game.wordfox.data.local.InMemoryDictionary;

/**
 * Created by Gilroy
 */

public class TestDictionaryApplication extends WFApplication {
    private final Diction dictionary = new InMemoryDictionary();
    @Override
    public Diction getDictionary() {
        return dictionary;
    }
}
