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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        SCALE_DPI = (float)(metrics.densityDpi)/(float)(DisplayMetrics.DENSITY_DEFAULT);
        setContentView(R.layout.activity_lolo);
        status = (TextView) findViewById(R.id.status);
        Button btn1 = (Button)findViewById(R.id.home);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoloActivity.this);
                String [] items = new String[5];
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                items[0] = Integer.valueOf(size.x).toString(); items[1] = Integer.valueOf(size.y).toString();
                if (loloView != null) {
                    items[2] = Float.valueOf(loloView.getWidth1()).toString();
                    items[3] = Integer.valueOf(loloView.getSize()).toString();
                }
                else {
                    items[2] = LoloActivity.class.getName(); items[3] = LoloActivity.class.getName();
                }
                items[4] = Float.valueOf(SCALE_DPI).toString();
                builder.setMultiChoiceItems(items,null, null).setPositiveButton(getString(R.string.plus), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(LoloActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
                builder.create().show();
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
                        //(int)(event.getY()) / (int)loloView.getWidth1();
                short oldcolor = loloView.getColorAtXY(x, y);
                short newcolor = (oldcolor==3) ? 1 : (short) (oldcolor + 1);
                loloView.setColorAtXY(x, y, newcolor);
                loloView.invalidate();
                status.setText(String.format("%d, %d", x, y));
                Log.d(TAG,String.format("Action was DOWN at %f, %f", event.getX(), event.getY()));
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
