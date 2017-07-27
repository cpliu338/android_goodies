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
        Log.d(TAG, "onSizeChanged: width " + w + ", height " + h);
        width1 = w / LoloActivity.SCALE_DPI / 6f * 2.5f;
        // assume 320dpi
        height1 = h / LoloActivity.SCALE_DPI / 6f * 2.5f;
        Log.d(TAG, "tile in dp: width1 " + width1 + ", height1 " + height1);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Draw the background...
        Paint background = new Paint();
        background.setColor(getResources().getColor(android.R.color.white));
        canvas.drawRect(0, 0, (int)(getWidth1() *6f), (int)(getHeight1() *6f), background);
        background.setColor(getResources().getColor(R.color.colorAccent));
        for (int i=0; i<6; i++) {
            canvas.drawRect(i*width1, i*height1, (i+1)*width1, (i+1)*height1, background);
        }
    }

    public float getWidth1() {
        return width1;
    }

    public float getHeight1() {
        return height1;
    }
}
