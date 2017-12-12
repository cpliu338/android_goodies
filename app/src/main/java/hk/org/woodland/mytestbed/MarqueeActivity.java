package hk.org.woodland.mytestbed;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MarqueeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marquee);

        TextView marque = (TextView) this.findViewById(R.id.marque_scrolling_text);
        marque.setSelected(true);

        TextView marque1 = (TextView) this.findViewById(R.id.sliding_text_marquee);
        marque1.setSelected(true);
    }
}
