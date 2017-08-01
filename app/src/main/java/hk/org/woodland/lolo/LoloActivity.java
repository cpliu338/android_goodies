package hk.org.woodland.lolo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.view.MotionEventCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import hk.org.woodland.mytestbed.MainActivity;
import hk.org.woodland.mytestbed.R;

public class LoloActivity extends Activity implements View.OnTouchListener {

    private static final String TAG = "LoloAct";
    public static float SCALE_DPI;

    LoloView loloView;
    TextView status;
    int state; // 0=>plan, 1=>play

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        SCALE_DPI = (float)(metrics.densityDpi)/(float)(DisplayMetrics.DENSITY_DEFAULT);
        setContentView(R.layout.activity_lolo);
        status = (TextView) findViewById(R.id.status);
        final Button btn1 = (Button)findViewById(R.id.home);
        state = 0;

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state = 1-state;
                btn1.setText(LoloActivity.this.getString(state==0 ? R.string.plan : R.string.play));
                if (state == 0) {
                    loloView.initTiles(0);
                    loloView.invalidate();
                }
            }
        });
        loloView = (LoloView)findViewById(R.id.lolo);
        loloView.setOnTouchListener(this);
        loloView.post(new Runnable() {
            @Override
            public void run() {
                loloView.setWidth1(
                    loloView.getWidth()/LoloView.SIZE
                );
            }
        });
        /* getActionBar().setDisplayHomeAsUpEnabled(true); */
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);

        switch(action) {
            case (MotionEvent.ACTION_DOWN) :
                short x = (short)Math.floor(event.getX()/loloView.getWidth1());
                short y = (short)Math.floor(event.getY()/loloView.getWidth1());
                short oldcolor = loloView.getColorAtXY(x, y);
                if (state == 0) {
                    loloView.setColorAtXY(x, y,
                        (oldcolor == 3) ? 1 : (short) (oldcolor + 1)
                    );
                }
                else {
                    int nblacks = loloView.spread(x, y, oldcolor);
                    Log.d(TAG,String.format("No of blacks %d", nblacks));
                    loloView.initTiles(0);
                }
                loloView.invalidate();

                status.setText(String.format("%d, %d", loloView.getValueAtXY(x,y), 0));
                return true;
            default :
                return super.onTouchEvent(event);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
