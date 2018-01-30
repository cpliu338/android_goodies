package hk.org.woodland.mytestbed;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.widget.TextView;

public class MarqueeActivity extends Activity {

    public static final String SCROLLING_TEXT = "scrolling_text";
    private TextView marquee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marquee);
        marquee = (TextView) this.findViewById(R.id.marque_scrolling_text);
        Display display = getWindowManager().getDefaultDisplay();
        int height;
        int width;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
            height = display.getHeight();
            width = display.getWidth();
        } else {
            Point size = new Point();
            display.getSize(size);
            height = size.y;
            width = size.x;
        }
        marquee.setTextSize(TypedValue.COMPLEX_UNIT_PX, width>height ? height*0.5f : width*0.5f);
        Log.i(MarqueeActivity.class.getName(), String.format("font size: %f in px", width>height ? height*0.5f : width*0.5f));
        //marquee.setTextSize(TypedValue.COMPLEX_UNIT_PT, 48.0f);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        /*
        Log.i(MarqueeActivity.class.getName(), "resumed");
        Log.i(MarqueeActivity.class.getName(), String.format("Text size: %f", marquee.getTextSize()));
        final float density = getResources().getDisplayMetrics().density;
        Log.i(MarqueeActivity.class.getName(), String.format("density: %f", density));
        */
        marquee.setSelected(true);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        marquee.setText(prefs.getString(SCROLLING_TEXT, getString(R.string.marquee)));
    }
}
