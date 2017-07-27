package hk.org.woodland.lolo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import hk.org.woodland.mytestbed.MainActivity;
import hk.org.woodland.mytestbed.R;

public class LoloActivity extends Activity {

    private static final String TAG = "LoloAct";
    public static final int DEVICE_DENSITY_DPI;

    LoloView loloView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        DEVICE_DENSITY_DPI = metrics.densityDpi;
        setContentView(R.layout.activity_lolo);
        Button btn1 = (Button)findViewById(R.id.home);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoloActivity.this);
                String [] items = new String[4];
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                items[0] = Integer.valueOf(size.x).toString(); items[1] = Integer.valueOf(size.y).toString();
                if (loloView != null) {
                    items[2] = Float.valueOf(loloView.getWidth1()).toString();
                    items[3] = Float.valueOf(loloView.getHeight1()).toString();
                }
                else {
                    items[2] = LoloActivity.class.getName(); items[3] = LoloActivity.class.getName();
                }
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
        //getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loloView = (LoloView)findViewById(R.id.lolo);
        if (loloView != null) {
            Log.d(TAG, String.format("%d,%d,%d,%d", loloView.getLeft(), loloView.getTop(), loloView.getRight(), loloView.getBottom()));
            loloView.setMinimumHeight(loloView.getRight());
        }
    }

}
