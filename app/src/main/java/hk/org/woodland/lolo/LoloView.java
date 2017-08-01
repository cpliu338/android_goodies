package hk.org.woodland.lolo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

import hk.org.woodland.mytestbed.R;

/**
 * Created by cpliu on 7/26/17.
 */

public class LoloView extends View {

    private static final String VIEW_STATE = "viewState";
    private static final String TAG = "Lolo";
    private Context context;

    public static final int SIZE = 6;
    private Game game;

    private float width1;    // width1 of one tile in dp
    int size; // loloview size in px

    static class Game {
        private short colors[][];   //
        private short values[][];   //

        Game() {
            colors = new short[SIZE][SIZE];
            values = new short[SIZE][SIZE];
            this.initTiles(0);
            /*
            for (short i=0; i<SIZE; i++) {
                for (short j=0; j<SIZE; j++) {
                    colors[i][j] = 1;
                    values[i][j] = 1;
                }
            }
            */
        }

        short getColorAtLeft(short x, short y) {
            return (x<=0 || y<0 || y>=SIZE || x>=SIZE) ? -1 : colors[x-1][y];
        }

        short getColorAtTop(short x, short y) {
            return (x<0 || y<=0 || y>=SIZE || x>=SIZE) ? -1 : colors[x][y-1];
        }

        void revertBlacks(short origColor) {
            for (short i = 0; i < SIZE; i++) {
                for (short j = 0; j < SIZE; j++) {
                    if (colors[i][j] <= 0) {
                        colors[i][j] = origColor;
                        Log.d(TAG, String.format("tile %d, %d reverted to %d", i, j, origColor));
                    }
                }
            }
        }
        int collectBlackValues() {
            int total = 0;
            for (short i = 0; i < SIZE; i++) {
                for (short j = 0; j < SIZE; j++) {
                    if (colors[i][j] == 0) {
                        total += values[i][j];
                    }
                }
            }
            return total;
        }

        short countBlacks() {
            short cnt = 0;
            for (short i = 0; i < SIZE; i++) {
                for (short j = 0; j < SIZE; j++) {
                    if (colors[i][j] <= 0) cnt++;
                }
            }
            return cnt;
        }

        void initTiles(int mode) {
            // This is development mode if (mode == 0)
            Random random = new Random();
            for (short i = 0; i < SIZE; i++) {
                for (short j = 0; j < SIZE; j++) {
                    if (colors[i][j] == 0) {
                        int r = 1+random.nextInt(3);
                        Log.d(TAG, String.format("randomed %d", r));
                        colors[i][j] = (short)(r);
                        values[i][j] = 1;
                    }
                }
            }
        }

        /**
         * Spread this color to 0 for contingent and same color tiles, recursively calls itself
         * @param x x coordinate of starting point
         * @param y y coordinate of starting point
         * @param origColor the original color
         * @param source true if this is the touched tile, false if this has been spreaded
         * @return the new value of this tile
         */
        int spread(short x, short y, short origColor, boolean source) {
            if (!source)
                colors[x][y] = 0; // set itself to black first, black also means "I have been spreaded"
            else
                colors[x][y] = -1; // temporarily set to -1, so it won't be spreaded
            // For left
            if (x>0 && colors[x-1][y]==origColor) {
                spread((short)(x-1), y, origColor,false);
            }
            // For top
            if (y>0 && colors[x][y-1]==origColor) {
                spread(x, (short)(y-1), origColor,false);
            }
            // For right
            if (x<SIZE-1 && colors[x+1][y]==origColor) {
                spread((short)(x+1), y, origColor,false);
            }
            // For bottom
            if (y<SIZE-1 && colors[x][y+1]==origColor) {
                spread(x,(short)(y+1), origColor,false);
            }
            if (source) {
                int nblacks = countBlacks();
                if (nblacks < 3) {
                    Log.d(TAG, String.format("revert %d blacks to %d", nblacks, origColor));
                    revertBlacks(origColor);
                    Log.d(TAG, String.format("now there are %d blacks", countBlacks()));
                    return nblacks;
                }
                int result = values[x][y] += collectBlackValues();
                if (result>=50) {
                    values[x][y] = 50;
                    colors[x][y] = 50;
                }
                else {
                    values[x][y] = (short)result;
                    colors[x][y] = origColor; // restore color
                }
            }
            else ;
                //values[x][y] = 0;
            return 0;
        }
    }

    public int getColorValueAtXY(short x, short y) {
        switch (game.colors[x][y]) {
            case 1: return context.getResources().getColor(R.color.color1);
            case 2: return context.getResources().getColor(R.color.color2);
            case 3: return context.getResources().getColor(R.color.color3);
            case 50: return context.getResources().getColor(R.color.color50);
            case 0:
            default:
                return context.getResources().getColor(R.color.color0);
        }
    }

    public int spread(short x, short y, short origColor) {
        return game.spread(x, y, origColor, true);
    }
    public short getValueAtXY(short x, short y) {
        return game.values[x][y];
    }
    public short getColorAtXY(short x, short y) {
        return game.colors[x][y];
    }
    public void initTiles(int mode) { game.initTiles(mode);}

    public void setColorAtXY(short x, short y, short color) {
        game.colors[x][y] = color;
    }

    public LoloView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        game = new Game();
        setFocusable(true);
        setFocusableInTouchMode(true);
    }
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable p = super.onSaveInstanceState();
        Log.d(TAG, "onSaveInstanceState");
        Bundle bundle = new Bundle();
        bundle.putParcelable(VIEW_STATE, p);
        return bundle;
    }
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Log.d(TAG, "onRestoreInstanceState");
        Bundle bundle = (Bundle) state;
        super.onRestoreInstanceState(bundle.getParcelable(VIEW_STATE));
    }

    /* should try post a handler
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // w and h are in dp
        width1 = w / 6.0f;
        height1 = h / 6.0f;
        Log.d(TAG, "tile in dp: width1 " + width1 + ", height1 " + height1);
        super.onSizeChanged(w, h, oldw, oldh);
    }
    */

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        size = width > height ? height : width;
        //Log.d(TAG, "size in dp: " + size);
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Draw the background...
        Paint background = new Paint();
        Paint brush =  new Paint();
        brush.setStrokeWidth(8.0f);
        brush.setColor(context.getResources().getColor(android.R.color.white));
        brush.setTextSize(width1/2);
        brush.setTextAlign(Paint.Align.CENTER);
        brush.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        //background.setColor(getResources().getColor(android.R.color.white));
        //canvas.drawRect(0, 0, (int)(width1 *6f), (int)(width1 *6f), background);
        for (short i=0; i<SIZE; i++) {
            for (short j=0; j<SIZE; j++) {
                background.setColor(getColorValueAtXY(i, j));
                short owncolor = game.colors[i][j];
                canvas.drawRect(i * width1, j * width1, (i + 1) * width1, (j + 1) * width1, background);
                Short value = game.values[i][j];
                if (value>1)
                    canvas.drawText(
                        value.toString(), (i+0.5f)*width1, (j+0.5f)*width1, brush
                    );
                if (owncolor != game.getColorAtLeft(i,j))
                    canvas.drawLine(i*width1, j*width1, i*width1, (j+1)*width1, brush);
                if (owncolor != game.getColorAtTop(i,j))
                    canvas.drawLine(i*width1, j*width1, (i+1)*width1, j*width1, brush);
            }
        }
    }

    /**
     * Get the size of one tile
     * @return the size of one tile in dp
     */
    public float getWidth1() {
        return width1;
    }

    /**
     * Get the size of the entire view
     * @return the size in dp
     */
    public int getSize() {return size;}

    public void setWidth1(float width1) {
        this.width1=width1;
        Log.d(TAG, "Width set to (dp): " + width1);
    }
}
