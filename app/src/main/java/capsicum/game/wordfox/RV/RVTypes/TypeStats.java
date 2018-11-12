package capsicum.game.wordfox.RV.RVTypes;

import capsicum.game.wordfox.RV.DataListItem;

import java.util.List;
import java.util.UUID;

public class TypeStats<T> implements DataListItem {
    private final UUID ID;
    private final String statistic;
    private T statValue;
    private boolean isExpanded = false;

    public TypeStats(String statistic, T statValue) {
        this.statistic = statistic;
        this.statValue = statValue;
        ID = UUID.randomUUID();
    }

    public String getStatistic() {
        return statistic;
    }

    public T getStatValue() {
        return statValue;
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
        return DataListItem.STATS;
    }

    @Override
    public List<DataListItem> getSubItems() {
        return null;
    }

    @Override
    public int getSubItemCount() {
        return 0;
    }

    @Override
    public UUID getID() {
        return ID;
    }
}
