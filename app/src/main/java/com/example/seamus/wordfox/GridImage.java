package com.example.seamus.wordfox;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;
@Deprecated
public class GridImage {

    private Bitmap gridImage;

    public GridImage(Bitmap grid, String word, String letters, int colorNotPressed, int colorSecondary) {
        this.gridImage = grid;
        ArrayList<Integer> clickedIndices = findClickIndices(word, letters);
        gridImage = replaceColor(gridImage, colorNotPressed, colorSecondary, clickedIndices);
        gridImage = drawGridLetters(gridImage, letters);
    }

    public Bitmap getBmp() {
        return gridImage;
    }

    private Bitmap drawGridLetters(Bitmap bmp, String letters) {
        String[] bestLetter = letters.split("");
        for (int y = 1; y < bestLetter.length; ++y) {
            bmp = drawText(bmp,
                    bestLetter[y],
                    Color.parseColor("#4025ed"), y);
        }
        return bmp;
    }

    private Bitmap drawText(Bitmap b, String mytext, int color, int section) {
        int scalex = b.getWidth() / 3;
        int scaley = b.getHeight() / 3;
        section -= 1;
        TextPaint mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        int textSize = b.getWidth() / 5;
        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(0xFFFFFFFF);

        float width = mTextPaint.measureText(mytext);
        float height = -mTextPaint.ascent() + mTextPaint.descent();
        int h = (int) height;
        int w = (int) width;
        int xoffset = ((section % 3) - 1) * (h / 12);
        int yoffset = ((section / 3) - 1) * (w / 12);
        int x = ((section % 3) * scalex + scalex / 2) - (w / 2) + xoffset;
        int y = ((section / 3) * scaley) + (scaley / 2) - (h / 2) + ((9 * h) / 10) - (h / 10) + yoffset;

        Canvas c = new Canvas(b);
        Paint paint = new Paint();
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        // Draw text
        c.save();
        c.translate(x, y);
        c.drawText(mytext, 0, 0, mTextPaint);
//        c.drawRect(textBounds, paint);
        c.restore();
        return b;
    }

    private Bitmap replaceColor(Bitmap mImage, int fromColor, int targetColor, ArrayList<Integer> sections) {
        if (mImage == null) {
            return null;
        }
        int width = mImage.getWidth();
        int height = mImage.getHeight();
        int[] pixels = new int[width * height];
        mImage.getPixels(pixels, 0, width, 0, 0, width, height);
        int sectionWidth = (width / 3);

        for (int section : sections) {
            int start = 0;
            int steps = ((section - 1) / 3);
            for (int i = 0; i < steps; ++i) {
                int t2 = (width * height) / 3;
                start += t2;  // Skip 3 blocks
            }
            start = ((start / width)) * width;      // Round to previous factor
            int end = ((width * height) * (steps + 1)) / 3;

            int skip = 0;
            if (width % 3 == 2 && section % 3 == 0) {
                skip = 2;
            } else if (width % 3 == 1 && section % 3 == 0) {
                skip = 1;
            } else if (width % 3 == 2 && section % 3 == 2) {
                skip = 1;
            }
            int position = start;
            skip += ((section + 2) % 3) * sectionWidth;
            while (position < end) {
                int tempPosition = position;
                tempPosition += skip;
                int run = sectionWidth;
                while (run > 0) {
                    if (tempPosition >= pixels.length) {
                        break;
                    }
                    if (pixels[tempPosition] == fromColor) {
                        pixels[tempPosition] = targetColor;
                    }
                    --run;
                    ++tempPosition;
                }
                position += width;
            }
        }

        Bitmap newImage = Bitmap.createBitmap(width, height, mImage.getConfig());
        newImage.setPixels(pixels, 0, width, 0, 0, width, height);
        return newImage;
    }

    private ArrayList<Integer> findClickIndices(String word, String letters) {

        ArrayList<Integer> clickedIndices = new ArrayList<>();
        String[] bestWord = word.split("");
        String[] bestLetter = letters.split("");
        for (int j = 1; j < bestWord.length; ++j) {
            for (int k = 1; k < bestLetter.length; ++k) {
                if (bestWord[j].equals(bestLetter[k])) {
                    clickedIndices.add(k);
                    bestLetter[k] = "0";
                    break;
                }
            }
        }
        return clickedIndices;
    }

    private ArrayList<Integer> findClickIndices(String word, String letters, boolean ghjk) {
        Queue<String> letterQueue = new ArrayDeque<>(Arrays.asList(letters));
        // TODO: unfinished

        ArrayList<Integer> clickedIndices = new ArrayList<>();
        String[] bestWord = word.split("");
        String[] bestLetter = letters.split("");
        for (int j = 1; j < bestWord.length; ++j) {
            for (int k = 1; k < bestLetter.length; ++k) {
                if (bestWord[j].equals(bestLetter[k])) {
                    clickedIndices.add(k);
                    bestLetter[k] = "0";
                    break;
                }
            }
        }
        return clickedIndices;
    }
}
