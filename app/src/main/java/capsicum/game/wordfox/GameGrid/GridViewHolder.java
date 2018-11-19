package capsicum.game.wordfox.GameGrid;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class GridViewHolder extends PlayerResultViewHolder {
    private static final String GRID_TAG_PREFIX = "game_grid_cell_";
    private static final String LETTER_TAG_PREFIX = "game_grid_letter_";
    private static final String GRID_HEADER_TAG = "game_grid_word_header";
    private final Bitmap notPressed;
    private final Bitmap pressed;
    private final ArrayList<ImageView> gridCells = new ArrayList<>();
    private final TextView wordHeader;
    private final int textSize;
    private final int textSizeGrid;
    private final List<String[]> letters;

    public GridViewHolder(@NonNull View itemView, List<String[]> letters, Bitmap notPressed, Bitmap pressed, int height, int width) {
        super(itemView);
        this.notPressed = notPressed;
        this.pressed = pressed;
        this.wordHeader = itemView.findViewWithTag(GRID_HEADER_TAG);
        textSize = (pressed.getHeight() * 40) / 100;
        textSizeGrid = (textSize * 3) / 2;
        wordHeader.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        if(letters.size() > 1){
            this.letters = letters;
        }else {
            initialiseLetters(itemView, letters.get(0));
            this.letters = null;
        }
        initialiseGrids(itemView, letters.get(0).length);
        adjustDimensions(itemView, height, width);
    }

    private void adjustDimensions(View itemView, int height, int width) {
        ConstraintLayout cl = itemView.findViewWithTag(GameGridAdapter.GRID_CONTAINER_TAG);
        ViewGroup.LayoutParams clparams = cl.getLayoutParams();
        clparams.height = height;
        clparams.width = width;
        cl.setLayoutParams(clparams);
    }

    @Override
    public void onBind(PlayerResultListItem player) {
        if(letters != null){
            int position = getAdapterPosition();
            int modulo = (position % 4);
            int fin = modulo -1;
            initialiseLetters(itemView, letters.get(fin));
        }
        TypeGrid grid = (TypeGrid) player;
        String headerString = grid.word.toUpperCase() + " (" + grid.word.length() + ")";
        wordHeader.setText(headerString);
        for (int i = 0; i < gridCells.size(); ++i) {
            if (grid.gridClickIndices.contains(i)) {
                gridCells.get(i).setImageBitmap(pressed);
            } else {
                gridCells.get(i).setImageBitmap(notPressed);
            }
        }
    }

    private void initialiseGrids(View view, int cellCount) {
        for (int i = 0; i < cellCount; ++i) {
            String gridTag = GRID_TAG_PREFIX + (i + 1);
            ImageView letterIV = view.findViewWithTag(gridTag);
            gridCells.add(letterIV);
        }
    }

    private void initialiseLetters(View view, String[] letters) {
        for (int i = 0; i < letters.length; ++i) {
            String letterTag = LETTER_TAG_PREFIX + (i + 1);
            TextView letterTv = view.findViewWithTag(letterTag);
            letterTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeGrid);
            letterTv.setText(letters[i]);
        }
    }

}
