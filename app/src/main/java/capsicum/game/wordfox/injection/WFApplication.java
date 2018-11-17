package capsicum.game.wordfox.injection;

import android.app.Application;

import capsicum.game.wordfox.BuildConfig;
import capsicum.game.wordfox.data.FoxDictionary;
import capsicum.game.wordfox.data.Diction;
import timber.log.Timber;

/**
 * Created by Gilroy
 */

public class WFApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            Timber.d("Timber is created!");
        }
    }

    private FoxDictionary dictionary = null;

    public Diction getDictionary() {
        if (dictionary == null) {
            dictionary = new FoxDictionary("word_list_with_alphagram.txt", "letterFrequency.txt", getAssets());
        } else {
            dictionary.resetLetterPool();
        }
        return dictionary;
    }
}
