package com.example.seamus.wordfox;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameGridAdapter extends RecyclerView.Adapter<GameGridAdapter.GridViewHolder> {
    private static final int GRID_PRESSED_ID = R.drawable.single_grid_cell_purple;
    private static final int GRID_NOT_PRESSED_ID = R.drawable.single_grid_cell_green;
    private static final int SPACING_PERCENT = 10;
    private static final int GRIDS_PER_ROW = 3;
    private static final String MONITOR_TAG = "myTag";
    public static final String GRID_CONTAINER_TAG = "grid_layout_container";
    private Bitmap pressedCell;
    private Bitmap notPressedCell;
    private final ArrayList<String> words;
    private final String[] letters;
    private int containerWidth;
    private int containerHeight;
    private List<Set<Integer>> gridClicksByWord = new ArrayList<>();

    public GameGridAdapter(ArrayList<String> words, String[] letters, int oneGridWidth, Resources resources) {
        if (letters.length != 9) {
            throw new IllegalArgumentException("Requires 9 letters!");
        }
        this.words = words;
        this.letters = letters;
        determineGridClicks(words, letters);
        gridDims(oneGridWidth, resources);
        log("Created adapter: " + words.size() + ", " + oneGridWidth + ", " + containerHeight);
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

    private void determineGridClicks(ArrayList<String> words, String[] letters) {
        for (String word : words) {
            gridClicksByWord.add(findClickIndices(word.toUpperCase(), letters));
        }
    }

    public static Set<Integer> findClickIndices(String gameWord, String[] inputLetters) {
        Set<Integer> clickedIndices = new HashSet<>();
        String[] gameLetters = inputLetters.clone();
        String[] wordLetters = gameWord.split("");
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
    public GameGridAdapter.GridViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.game_grid_xml, viewGroup, false);
        ConstraintLayout cl = v.findViewWithTag(GRID_CONTAINER_TAG);
        ViewGroup.LayoutParams clparams = cl.getLayoutParams();
        clparams.height = containerHeight;
        clparams.width = containerWidth;
        cl.setLayoutParams(clparams);
        log("cl h,w : " + clparams.height + ", " + clparams.width);
        return new GridViewHolder(v, letters, notPressedCell, pressedCell);
    }

    @Override
    public void onBindViewHolder(@NonNull GameGridAdapter.GridViewHolder viewHolder, int i) {
        viewHolder.onBind(gridClicksByWord.get(i), words.get(i));
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    private void log(String msg) {
        Log.d(MONITOR_TAG, msg);
    }

    public static class GridViewHolder extends RecyclerView.ViewHolder {
        private static final String GRID_TAG_PREFIX = "game_grid_cell_";
        private static final String LETTER_TAG_PREFIX = "game_grid_letter_";
        private static final String GRID_HEADER_TAG = "game_grid_word_header";
        private final Bitmap notPressed;
        private final Bitmap pressed;
        private final ArrayList<ImageView> gridCells = new ArrayList<>();
        private final TextView wordHeader;
        private final int textSize;
        private final int textSizeGrid;

        public GridViewHolder(@NonNull View itemView, String[] letters, Bitmap notPressed, Bitmap pressed) {
            super(itemView);
            this.notPressed = notPressed;
            this.pressed = pressed;
            this.wordHeader = itemView.findViewWithTag(GRID_HEADER_TAG);
            textSize = (pressed.getHeight() * 20) / 100;
            textSizeGrid = (textSize*3)/2;
            wordHeader.setTextSize(textSize);
            initialiseLetters(itemView, letters);
            initialiseGrids(itemView, letters);
        }

        public void onBind(Set<Integer> gridClickIndices, String word) {
            String headerString = word.toUpperCase() + " (" + word.length() + ")";
            wordHeader.setText(headerString);
            for (int i = 0; i < gridCells.size(); ++i) {
                if (gridClickIndices.contains(i)) {
                    gridCells.get(i).setImageBitmap(pressed);
                } else {
                    gridCells.get(i).setImageBitmap(notPressed);
                }
            }
        }

        private void initialiseGrids(View view, String[] letters) {
            for (int i = 0; i < letters.length; ++i) {
                String gridTag = GRID_TAG_PREFIX + (i + 1);
                ImageView letterIV = view.findViewWithTag(gridTag);
                gridCells.add(letterIV);
            }
        }

        private void initialiseLetters(View view, String[] letters) {
            for (int i = 0; i < letters.length; ++i) {
                String letterTag = LETTER_TAG_PREFIX + (i + 1);
                TextView letterTv = view.findViewWithTag(letterTag);
                letterTv.setTextSize(textSizeGrid);
                letterTv.setText(letters[i]);
            }
        }

    }
}
