package capsicum.game.wordfox.recyclerview_game_results.itemtypes;

import java.util.Set;

import capsicum.game.wordfox.recyclerview_game_results.PlayerResultListItem;

public class TypeGrid implements PlayerResultListItem {
    public final Set<Integer> gridClickIndices;
    public final String word;

    public TypeGrid(Set<Integer> gridClickIndices, String word) {
        this.gridClickIndices = gridClickIndices;
        this.word = word.toUpperCase();
    }

    @Override
    public int getListItemType() {
        return PlayerResultListItem.GRID;
    }
}
