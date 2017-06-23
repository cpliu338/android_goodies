package hk.org.woodland.mytestbed;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements ClickOrTiltListener {

    private SlideByClickOrTilt element;
    private TextView feedback;
    private Sensor gravity;
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slide_by_click_or_tilt);
        element = //new SlideByClickOrTilt.Builder().withMaxValue(200).withMaxDelta(16).build();
                SlideByClickOrTilt.withLabelPlus(getString(R.string.plus))
                    .withLabelMinus(getString(R.string.minus))
                    .withButtonPlus((Button)findViewById(R.id.plus))
                    .withButtonMinus((Button)findViewById(R.id.minus))
                    .withMaxValue(200).withMaxDelta(16)
                .build();
        feedback = (TextView)findViewById(R.id.feedback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SlideByClickOrTilt.lockActivityOrientation(this);
        element.runInActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        element.cleanUp();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @Override
    public void onClickorTilt(int value) {
        switch (value) {
            case Integer.MAX_VALUE:
                feedback.setText(getString(R.string.app_name));
                break;
            case Integer.MIN_VALUE:
                feedback.setText(getString(R.string.hint));
                break;
            default:
                feedback.setText(getString(R.string.feedback, value, new java.util.Date(System.currentTimeMillis()+value*60000L)));
        }
    }

}

