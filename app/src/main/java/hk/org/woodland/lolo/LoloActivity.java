package hk.org.woodland.lolo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.support.v4.view.MotionEventCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.*;

import hk.org.woodland.mytestbed.MainActivity;
import hk.org.woodland.mytestbed.R;

public class LoloActivity extends Activity implements View.OnTouchListener {

    private static final String TAG = "LoloAct";
    public static final int STATE_PLAN = 0;
    public static final int STATE_PLAY = 1;
    public static float SCALE_DPI;
    // Key for saving instance
    public static final String SCORE="score";

    LoloView loloView;
    TextView status;
    int state; // 0=>plan, 1=>play
    Integer score;

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
        state = STATE_PLAY;
        score = 0;
        if (savedInstanceState != null) {
            score = savedInstanceState.getInt(SCORE);
        }
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            switch (state) {
                case STATE_PLAN:
                    state = STATE_PLAY;
                    btn1.setText(LoloActivity.this.getString(R.string.play));
                    break;
                case STATE_PLAY: // start new game
                    loloView.discardGame();
                    score = 0;
                    status.setText(score.toString());
                    loloView.invalidate();
            } /*
                state = 1-state;
                btn1.setText(LoloActivity.this.getString(state==STATE_PLAN ? R.string.plan : R.string.play));
                if (state == 0) {
                    loloView.initTiles(0);
                    loloView.invalidate();
                } */
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
                if (x>=LoloView.SIZE || y>=LoloView.SIZE)
                    return super.onTouchEvent(event);
                short oldcolor = loloView.getColorAtXY(x, y);
                if (state == 0) {
                    loloView.setColorAtXY(x, y,
                        (oldcolor == 3) ? 1 : (short) (oldcolor + 1)
                    );
                }
                else {
                    int this_sore = loloView.spread(x, y, oldcolor);
                    //Log.d(TAG,String.format("This score %d", this_sore));
                    score += this_sore;
                    status.setText(score.toString());
                    loloView.initTiles(0);
                }
                loloView.invalidate();

                //status.setText(String.format("%d, %d", loloView.getValueAtXY(x,y), 0));
                return true;
            default :
                return super.onTouchEvent(event);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "Save score: "+score);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putInt(SCORE, score);
        editor.commit();
        // save game
        try {
            ObjectOutputStream os = new ObjectOutputStream(openFileOutput(LoloView.TAG, MODE_PRIVATE));
            loloView.saveState(os);
            os.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        score = preferences.getInt(SCORE, 0);
        Log.i(TAG, "Restored score");
        // restore game
        try {
            ObjectInputStream is = new ObjectInputStream(openFileInput(LoloView.TAG));
            loloView.restoreState(is);
            is.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

}
