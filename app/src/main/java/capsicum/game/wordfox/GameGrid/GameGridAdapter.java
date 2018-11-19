package capsicum.game.wordfox.GameGrid;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import capsicum.game.wordfox.ImageHandler;
import capsicum.game.wordfox.R;
import capsicum.game.wordfox.WordfoxConstants;
import timber.log.Timber;

public class GameGridAdapter extends RecyclerView.Adapter<PlayerResultViewHolder> {
    private static final int GRID_PRESSED_ID = R.drawable.single_grid_cell_purple;
    private static final int GRID_NOT_PRESSED_ID = R.drawable.single_grid_cell_green;
    private static final int SPACING_PERCENT = 10;
    private static final int GRIDS_PER_ROW = 3;
    public static final String GRID_CONTAINER_TAG = "grid_layout_container";
    private Bitmap pressedCell;
    private Bitmap notPressedCell;
    private final List<String[]> letters;
    private int containerWidth;
    private int containerHeight;
    private final List<PlayerResultListItem> playerResultListItems = new ArrayList<>();
    private final int maxScore;

    // TODO: Remove resources dependency
    public GameGridAdapter(ArrayList<String> words, String[] letters, int oneGridWidth, Resources resources) {
        validateArgs(letters);
        this.letters = new ArrayList<>(Arrays.<String[]>asList(letters));
        maxScore = -1;
        for (String word : words) {
            playerResultListItems.add(new TypeGrid(findClickIndices(word, letters), word));
        }
        gridDims(oneGridWidth, resources);
        log("Created adapter: " + words.size() + ", " + oneGridWidth + ", " + containerHeight);
    }

    // words found, score, rank,
    public GameGridAdapter(List<PlayerResultPackage> players, List<String[]> letters, int maxScore, int oneGridWidth, Resources resources) {
        validateArgs(letters);
        this.letters = letters;
        this.maxScore = maxScore;
        for (PlayerResultPackage player : players) {
            addPlayer(player);
        }
        gridDims(oneGridWidth, resources);
    }

    private void addPlayer(PlayerResultPackage player) {
        playerResultListItems.add(new TypePlayerResultDetails(player));
        for (int i = 0; i < player.wordsFound.size(); ++i) {
            playerResultListItems.add(new TypeGrid(findClickIndices(player.wordsFound.get(i), letters.get(i)), player.wordsFound.get(i)));
        }
    }

    // For wifi games, player results may be received late so need ability to add them to the list
    public void newPlayer(PlayerResultPackage player) {
        addPlayer(player);
        notifyDataSetChanged();
    }

    private void validateArgs(List<String[]> letters) {
        for (String[] roundLetters : letters) {
            validateArgs(roundLetters);
        }
    }

    private void validateArgs(String[] letters) {
        if (letters.length != WordfoxConstants.GRID_LETTERS_COUNT) {
            throw new IllegalArgumentException("Requires " + WordfoxConstants.GRID_LETTERS_COUNT + " letters!");
        }
    }

    private void gridDims(int oneGridWidth, Resources resources) {
        containerWidth = oneGridWidth;
        int oneCellWidth = ((oneGridWidth * (100 - SPACING_PERCENT)) / 100) / GRIDS_PER_ROW;
        pressedCell = ImageHandler.getScaledBitmapByWidth(GRID_PRESSED_ID, oneCellWidth, resources);
        notPressedCell = ImageHandler.getScaledBitmapByWidth(GRID_NOT_PRESSED_ID, oneCellWidth, resources);
        int oneCellHeight = pressedCell.getHeight();
        containerHeight = (((oneCellHeight * GRIDS_PER_ROW) * 100) / (100 - SPACING_PERCENT));

        log("One cell h,w : " + oneCellHeight + ", " + oneCellWidth);
    }

    public static Set<Integer> findClickIndices(String gameWord, String[] inputLetters) {
        Set<Integer> clickedIndices = new HashSet<>();
        String[] gameLetters = inputLetters.clone();
        String[] wordLetters = gameWord.toUpperCase().split("");
        for (int j = 1; j < wordLetters.length; ++j) {
            for (int k = 0; k < gameLetters.length; ++k) {
                if (wordLetters[j].equals(gameLetters[k])) {
                    gameLetters[k] = "0";
                    clickedIndices.add(k);
                    break;
                }
            }
        }
        return clickedIndices;
    }

    @NonNull
    @Override
    public PlayerResultViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v;
        switch (viewType) {
            case PlayerResultListItem.PLAYER:
                v = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.result_player_details, viewGroup, false);
                return new PlayerDetailsViewHolder(v, maxScore);
            case PlayerResultListItem.GRID:
                v = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.game_grid_xml, viewGroup, false);
                return new GridViewHolder(v, letters, notPressedCell, pressedCell, containerHeight, containerWidth);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return playerResultListItems.get(position).getListItemType();
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerResultViewHolder viewHolder, int position) {
        viewHolder.onBind(playerResultListItems.get(position));
    }

    @Override
    public int getItemCount() {
        return playerResultListItems.size();
    }

    private void log(String msg) {
        Timber.d(msg);
    }

}
