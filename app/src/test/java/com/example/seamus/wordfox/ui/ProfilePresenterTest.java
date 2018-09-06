package com.example.seamus.wordfox.ui;

import android.app.Activity;
import android.graphics.Bitmap;

import com.example.seamus.wordfox.GameData;
import com.example.seamus.wordfox.profile.ProfileActivity;
import com.example.seamus.wordfox.profile.ProfileContract;
import com.example.seamus.wordfox.profile.ProfilePresenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.*;

/**
 * Created by Gilroy on 4/22/2018.
 */
public class ProfilePresenterTest {
    private GameData myGameData;
    private Activity activity;
    private ProfileContract.View view;
    private ProfilePresenter presenter;

    @Before
    public void setup(){
        myGameData = Mockito.mock(GameData.class);
        ProfileActivity profile = Mockito.mock(ProfileActivity.class);
        activity = profile;
        view = profile;
        presenter = new ProfilePresenter(profile, myGameData);
    }

    @Test
    public void longestWordTest(){
        Mockito.when(myGameData.findLongest()).thenReturn("CONUNDRUM");
        presenter.displayLongestWord();
        Mockito.verify(view, Mockito.times(1)).setLongestWord("CONUNDRUM");
    }
    @Test
    public void noLongestWordTest(){
        Mockito.when(myGameData.findLongest()).thenReturn("");
        presenter.displayLongestWord();
        Mockito.verify(view, Mockito.times(1)).setLongestWord("No words found!");
    }

    @Test
    public void updateName(){
        Mockito.when(myGameData.getUsername()).thenReturn("Harry");
        presenter.updateProfileName("Harry");
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(view, Mockito.times(1)).setUsername(captor.capture());
        assertEquals(captor.getAllValues().get(0), "Harry");
    }
}