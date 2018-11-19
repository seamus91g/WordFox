package capsicum.game.wordfox.GameGrid;

import java.util.Set;

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
