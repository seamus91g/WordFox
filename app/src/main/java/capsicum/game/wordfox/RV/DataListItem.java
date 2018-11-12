package capsicum.game.wordfox.RV;

import java.util.List;
import java.util.UUID;

/*
 * Interface for all the viewHolders used in WFAdapter. The adapter data set consists only
 * of items which implement this interface.
 */

public interface DataListItem {
    int PLAYER = 0;
    int CATEGORY = 1;
    int STATS = 2;
    int GAMES_HEADER = 3;
    int GAMES_DETAIL = 4;
    int WORDS_HEADER = 5;
    int WORDS_DETAIL = 6;
    int ADVERT = 7;
    int HEADING_IMAGE = 8;

    boolean isListExpanded();

    void toggleExpanded();

    int getListItemType();

    List<DataListItem> getSubItems();

    int getSubItemCount();

    UUID getID();
}
