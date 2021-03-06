package capsicum.game.wordfox.test;

import android.app.Application;
import android.content.Context;
import android.support.test.runner.AndroidJUnitRunner;

import capsicum.game.wordfox.injection.TestDictionaryApplication;

/**
 * Created by Gilroy
 */

public class CustomTestRunner extends AndroidJUnitRunner {
    @Override
    public Application newApplication(ClassLoader cl, String className, Context context)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return super.newApplication(cl, TestDictionaryApplication.class.getName(), context);
    }
}
