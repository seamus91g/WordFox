package capsicum.game.wordfox.recyclerview_game_data.RVTypes;

import android.graphics.Bitmap;

import capsicum.game.wordfox.recyclerview_game_data.DataListItem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TypePlayer implements DataListItem {
    private final UUID ID;
    private String player;
    private ArrayList<DataListItem> subItems;
    private Bitmap profPic;
    private String rank;
    private boolean isExpanded = false;

    public TypePlayer(String playerName, ArrayList<DataListItem> subItems, Bitmap profPic, String rank) {
        this.player = playerName;
        this.subItems = subItems;
        this.profPic = profPic;
        this.rank = rank;
        this.ID = UUID.randomUUID();
    }

    public String getPlayer() {
        return player;
    }

    public Bitmap getProfPic() {
        return profPic;
    }

    public String getRank() {
        return rank;
    }

    @Override
    public boolean isListExpanded() {
        return isExpanded;
    }

    @Override
    public void toggleExpanded() {
        isExpanded = !isExpanded;
    }

    @Override
    public int getListItemType() {
        return DataListItem.PLAYER;
    }

    @Override
    public List<DataListItem> getSubItems() {
        return subItems;
    }

    @Override
    public int getSubItemCount() {
        return subItems.size();
    }

    @Override
    public UUID getID() {
        return ID;
    }
}
