package capsicum.game.wordfox.ui;

import android.app.Activity;
import android.graphics.Point;

import capsicum.game.wordfox.GameData;
import capsicum.game.wordfox.database.FoxSQLData;
import capsicum.game.wordfox.profile.ProfileActivity;
import capsicum.game.wordfox.profile.ProfileContract;
import capsicum.game.wordfox.profile.ProfilePresenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.Assert.*;

/**
 * Created by Gilroy
 */
public class ProfilePresenterTest {
    private GameData myGameData;
    private Activity activity;
    private ProfileContract.View view;
    private ProfilePresenter presenter;
    private FoxSQLData foxDb;

    @Before
    public void setup(){
        myGameData = Mockito.mock(GameData.class);
        ProfileActivity profile = Mockito.mock(ProfileActivity.class);
        foxDb = Mockito.mock(FoxSQLData.class);
        activity = profile;
        view = profile;
        presenter = new ProfilePresenter(profile, myGameData, foxDb, new Point(100, 100));
    }

    @Test
    public void longestWordTest(){
        Mockito.when(myGameData.findLongest()).thenReturn("CONUNDRUM");
        presenter.displayLongestWord();
        Mockito.verify(view, Mockito.times(1)).setLongestWord("CONUNDRUM");
    }
    @Test
    public void noLongestWordTest(){
        Mockito.when(myGameData.findLongest()).thenReturn(GameData.NON_EXISTANT);       // TODO: Pointless test
        presenter.displayLongestWord();
        Mockito.verify(view, Mockito.times(1)).setLongestWord(GameData.NON_EXISTANT);
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
