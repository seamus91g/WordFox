package capsicum.game.wordfox.recyclerview_game_data.RVTypes;

import capsicum.game.wordfox.recyclerview_game_data.DataListItem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TypeCategory implements DataListItem {
    private final UUID ID;
    private final String categoryTitle;
    private final ArrayList<DataListItem> subItems;
    private boolean isExpanded = false;

    public TypeCategory(String categoryTitle, ArrayList<DataListItem> subItems) {
        this.categoryTitle = categoryTitle;
        this.subItems = subItems;
        ID = UUID.randomUUID();
    }

    public String getCategoryTitle() {
        return categoryTitle;
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
        return DataListItem.CATEGORY;
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
