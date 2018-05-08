package com.example.seamus.wordfox.test;

import com.example.seamus.wordfox.*;

/**
 * Created by Gilroy on 4/14/2018.
 */

import com.example.seamus.wordfox.R;

public class HomeRobot extends ScreenRobot<HomeRobot> {
    public HomeRobot startGame(){
        return performClick(R.id.bStartGame);
    }
    public HomeRobot setPlayerCount(int count){
        for (int i=1; i<count; ++i){
            advanceNumberPicker(R.id.numberPicker);
        }
        return this;
    }
}
