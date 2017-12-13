package hk.org.woodland.mytestbed;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

public class MarqueeActivity extends Activity {

    public static final String SCROLLING_TEXT = "scrolling_text";
    private TextView marque;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marquee);

        marque = (TextView) this.findViewById(R.id.marque_scrolling_text);
    }

    @Override
    protected void onResume() {
        super.onResume();
        marque.setSelected(true);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        marque.setText(prefs.getString(SCROLLING_TEXT, getString(R.string.marquee)));
    }
}
