package hk.org.woodland.mytestbed;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
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
        element = new SlideByClickOrTilt(this);
        feedback = (TextView)findViewById(R.id.feedback);

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gravity != null)
            sensorManager.registerListener(element, gravity, SensorManager.SENSOR_DELAY_NORMAL);
        else
            feedback.setText("No gravity sensor" + getString(R.string.minus));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gravity != null)
            sensorManager.unregisterListener(element);
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

