package capsicum.game.wordfox.test;

import capsicum.game.wordfox.R;

/**
 * Created by Gilroy
 */

public class HomeRobot extends ScreenRobot<HomeRobot> {
    public HomeRobot startGame(){
        return performClick(R.id.bStartGame);
    }
    public HomeRobot setPlayerCount(int count){

        switch (count){
            case 1:
                performClick(R.id.fox1Button);
                break;
            case 2:
                performClick(R.id.fox2Button);
                break;
            case 3:
                performClick(R.id.fox3Button);
                break;
            case 4:
                performClick(R.id.fox4Button);
                break;
            case 5:
                performClick(R.id.fox5Button);
                break;
            case 6:
                performClick(R.id.fox6Button);
                break;
        }

        return this;
    }
}