package hk.org.woodland.lolo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import hk.org.woodland.mytestbed.R;

/**
 * Created by cpliu on 7/26/17.
 */

public class LoloView extends View {

    private static final String VIEW_STATE = "viewState";
    private static final String TAG = "Lolo";

    private float width1;    // width1 of one tile in dp
    private float height1;   // height1 of one tile in dp

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
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d(TAG, "onSizeChanged: width1 " + w + ", height1 " + h);
        width1 = w / 6f /2f; // assume 320dpi
        height1 = h / 6f / 2f;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Draw the background...
        Paint background = new Paint();
        background.setColor(getResources().getColor(android.R.color.white));
        canvas.drawRect(0, 0, (int)(getWidth1() *6f), (int)(getHeight1() *6f), background);
        background.setColor(getResources().getColor(R.color.colorAccent));
        canvas.drawRect(4* getWidth1(), 4* getHeight1(), getWidth1(), getHeight1(), background);
        canvas.drawRect(0, 0, getWidth1(), getHeight1(), background);
    }

    public float getWidth1() {
        return width1;
    }

    public float getHeight1() {
        return height1;
    }
}
