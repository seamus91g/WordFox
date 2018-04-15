package com.example.seamus.wordfox.test;

import android.app.Application;
import android.content.Context;
import android.support.test.runner.AndroidJUnitRunner;

import com.example.seamus.wordfox.injection.TestDictionaryApplication;

/**
 * Created by Gilroy on 4/13/2018.
 */

public class CustomTestRunner extends AndroidJUnitRunner {
    @Override
    public Application newApplication(ClassLoader cl, String className, Context context)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return super.newApplication(cl, TestDictionaryApplication.class.getName(), context);
    }
}
