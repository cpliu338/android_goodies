package hk.org.woodland.mytestbed;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;

/**
 * Created by cp_liu on 6/22/17.
 */

public class SlideByClickOrTilt implements View.OnClickListener, SensorEventListener {
    private int value, minValue, maxValue, delta;
    private long lastCheckedTime;
    private ClickOrTiltListener container;

    public SlideByClickOrTilt(ClickOrTiltListener activity) {
        value=0; minValue = 0; maxValue = 100;
        delta = 1;
        lastCheckedTime = SystemClock.uptimeMillis();
        container = activity;
        Button btnMinus = (Button)container.findViewById(R.id.minus);
        Button btnPlus = (Button)container.findViewById(R.id.plus);
        btnPlus.setOnClickListener(this);
        btnMinus.setOnClickListener(this);


    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // x>3 means minus; x<-3 means plus
        float x = event.values[0];
        if (x>3.0f) {
            decrease();
            Log.i(SlideByClickOrTilt.class.getName(), "value " + x);
        }
        else if (x<-3.0f) {
            increase();
            Log.i(SlideByClickOrTilt.class.getName(), "value " + x);
        }
    }

    private void increase() {
        value = (value < maxValue - delta) ? value+delta : maxValue;
        container.onClickorTilt(value);
    }

    private void decrease() {
        value = (value > minValue + delta) ? value-delta : minValue;
        container.onClickorTilt(value);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing
    }
    public void onClick(View v) {
        lastCheckedTime = SystemClock.uptimeMillis();
        switch (v.getId()) {
            case R.id.plus:
                increase();
                break;
            case R.id.minus:
                decrease();
                break;
            default: // impossible
                value = Integer.MIN_VALUE;
        }
    }

}
