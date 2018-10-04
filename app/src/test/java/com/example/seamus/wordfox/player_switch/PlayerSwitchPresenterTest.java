package com.example.seamus.wordfox.player_switch;

import com.example.seamus.wordfox.GameInstance;
import com.example.seamus.wordfox.PlayerIdentity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/**
 * Created by Gilroy on 4/26/2018.
 */
public class PlayerSwitchPresenterTest {
    private PlayerSwitchContract.View view;
    private int index = 1;
    private PlayerSwitchPresenter presenter;

    @Before
    public void setup() {
        PlayerSwitchActivity activity = Mockito.mock(PlayerSwitchActivity.class);
        view = activity;

        GameInstance dudGI = Mockito.mock(GameInstance.class);
        GameInstance dudGI2 = Mockito.mock(GameInstance.class);
        PlayerIdentity dummyPlayerOne = new PlayerIdentity(UUID.randomUUID(), "Player 1");
        Mockito.when(dudGI.getPlayer()).thenReturn(dummyPlayerOne);
        Mockito.when(dudGI2.getPlayer()).thenReturn(new PlayerIdentity(UUID.randomUUID(), "Player 2"));
        ArrayList<GameInstance> gameInstances = new ArrayList<>(Arrays.asList(dudGI, dudGI2));

        ArrayList<PlayerIdentity> playerNames = new ArrayList<>();
        playerNames.add(new PlayerIdentity(UUID.randomUUID(), "Alan"));
        playerNames.add(new PlayerIdentity(UUID.randomUUID(), "Joe"));
        playerNames.add(dummyPlayerOne);
        presenter = new PlayerSwitchPresenter(activity, index, gameInstances, playerNames);
    }
//    @Test
//    public void testSetChoiceEmpty(){
//        String expectedResult = "Pass the game to player " + (index + 1);
//        presenter.setChoice("");
//        Mockito.verify(view, Mockito.times(1)).displayMessage(expectedResult);
//    }
    @Test
    public void testSetChoice(){
        String expectedResult = "Pass the game to Alan";
        presenter.setChoice("Alan");
        Mockito.verify(view, Mockito.times(1)).displayMessage(expectedResult);
    }
    @Test
    public void testSetupMenu(){
        ArrayList<String> expectedItems = new ArrayList<>((Arrays.asList("Player 2", "Alan", "Joe")));
        presenter.setupMenu();
        Mockito.verify(view, Mockito.times(1)).populateMenu(expectedItems);
    }
    @Test
    public void testAddNewPlayer(){
        ArrayList<String> expectedItems = new ArrayList<>(Arrays.asList("Baz", "Player 2", "Alan", "Joe"));
        presenter.newPlayer("Baz");
        Mockito.verify(view, Mockito.times(1)).populateMenu(expectedItems);
    }
    @Test
    public void testAddExistingPlayer(){
        ArrayList<String> expectedItems = new ArrayList<>(Arrays.asList("Joe", "Player 2", "Alan"));
        presenter.newPlayer("Joe");
        Mockito.verify(view, Mockito.times(1)).populateMenu(expectedItems);
    }
}