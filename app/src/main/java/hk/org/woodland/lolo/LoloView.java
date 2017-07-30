package hk.org.woodland.lolo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import hk.org.woodland.mytestbed.R;

/**
 * Created by cpliu on 7/26/17.
 */

public class LoloView extends View {

    private static final String VIEW_STATE = "viewState";
    private static final String TAG = "Lolo";

    private float width1;    // width1 of one tile in dp
    //private float height1;   // height1 of one tile in dp
    int size; // loloview size in px

    public LoloView(Context context, AttributeSet attrs) {
        super(context, attrs);
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
        Log.d(TAG, "size in dp: " + size);
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Draw the background...
        Log.d(TAG, "Draw with width (dp): " + width1);
        Paint background = new Paint();
        background.setColor(getResources().getColor(android.R.color.white));
        canvas.drawRect(0, 0, (int)(width1 *6f), (int)(width1 *6f), background);
        background.setColor(getResources().getColor(R.color.colorAccent));
        for (int i=0; i<6; i++) {
            canvas.drawRect(i*width1, i*width1, (i+1)*width1, (i+1)*width1, background);
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
