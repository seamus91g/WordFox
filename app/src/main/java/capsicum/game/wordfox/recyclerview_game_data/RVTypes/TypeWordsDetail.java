package capsicum.game.wordfox.recyclerview_game_data.RVTypes;

import capsicum.game.wordfox.recyclerview_game_data.DataListItem;
import capsicum.game.wordfox.recyclerview_game_data.datamodels.WordData;

import java.util.List;
import java.util.UUID;

public class TypeWordsDetail implements DataListItem {
    private final UUID ID;
    private final String letters;
    private final String longestPossible;
    private boolean isExpanded = false;

    public TypeWordsDetail(WordData wd) {
        this.letters = wd.getGivenLetters();
        this.longestPossible = wd.getLongestPossibleWord();
        ID = UUID.randomUUID();
    }

    public String getLetters() {
        return letters;
    }

    public String getLongestPossible() {
        return longestPossible;
    }

    @Override
    public int getListItemType() {
        return DataListItem.WORDS_DETAIL;
    }

    @Override
    public int getSubItemCount() {
        return 0;
    }

    @Override
    public UUID getID() {
        return ID;
    }

    @Override
    public List<DataListItem> getSubItems() {
        return null;
    }

    @Override
    public boolean isListExpanded() {
        return isExpanded;
    }

    @Override
    public void toggleExpanded() {
        isExpanded = !isExpanded;
    }

}
