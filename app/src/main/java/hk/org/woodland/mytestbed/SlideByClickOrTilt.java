package hk.org.woodland.mytestbed;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;

/**
 * Slider within a range using tilt, falls back to 2 buttons if no sensors
 * Lifecycle: onAttach -> onCreateDialog -> ... -> onDetach
 */

public class SlideByClickOrTilt extends DialogFragment implements View.OnClickListener, SensorEventListener {
    private int value, minValue, maxValue, delta, maxDelta, minDelta;
    private long lastCheckedTime;
    private ClickOrTiltListener container;
    private Sensor gravity;
    private SensorManager sensorManager;
    private String labelPlus, labelMinus;
    private Button buttonPlus, buttonMinus;
    private TextView feedback;

    public SlideByClickOrTilt() {
        /*
        These should be left as default and not brought forward to copy constructor
        value=0;
        gravity = null;
        delta = 0;
        */
        minValue = 0; maxValue = Integer.MAX_VALUE;
        minDelta = 1; maxDelta = 2;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.i(SlideByClickOrTilt.class.getName(), "dialog created");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.slide_by_click_or_tilt, null);
        feedback = (TextView)v.findViewById(R.id.feedback);
        feedback.setText("Test");
        buttonPlus = (Button) v.findViewById(R.id.plus);
        buttonMinus = (Button) v.findViewById(R.id.minus);
        buttonPlus.setOnClickListener(this);
        buttonMinus.setOnClickListener(this);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(v)
                // Add action buttons
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SlideByClickOrTilt.this.getDialog().cancel();
                    }
                });
        return builder.create();    }

    private static class Builder implements Ilabelplus, Ilabelminus, IBtnplus, IBtnminus, IBuild {
        /*
        See blog.crisp.se/2013/10/09/perlundholm/another-build-pattern-for-java
         */
        private SlideByClickOrTilt instance = new SlideByClickOrTilt();

        @Override
        public IBuild withMinValue(int minValue) {
            instance.minValue = minValue;
            if (minValue >= instance.maxValue) instance.maxValue=minValue+1;
            return this;
        }
        @Override
        public IBuild withMinDelta(int minDelta) {
            instance.minDelta = minDelta;
            if (minDelta >= instance.maxDelta) instance.maxDelta=minDelta+1;
            return this;
        }
        @Override
        public IBuild withMaxDelta(int maxDelta) {
            instance.maxDelta = maxDelta;
            if (maxDelta <= instance.minDelta) instance.minDelta=maxDelta-1;
            return this;
        }
        @Override
        public IBuild withMaxValue(int maxValue) {
            instance.maxValue = maxValue;
            if (maxValue <= instance.minValue) instance.minValue=maxValue-1;
            return this;
        }
        public Builder(String labelPlus) {
            instance.labelPlus = labelPlus;
        }
        @Override
        public Ilabelminus withLabelPlus(String labelPlus) {
            instance.labelPlus = labelPlus;
            return this;
        }
        @Override
        public IBtnplus withLabelMinus(String labelMinus) {
            instance.labelMinus = labelMinus;
            return this;
        }
        @Override
        public IBtnminus withButtonPlus(Button buttonPlus) {
            instance.buttonPlus = buttonPlus;
            return this;
        }
        @Override
        public IBuild withButtonMinus(Button buttonMinus) {
            instance.buttonMinus = buttonMinus;
            return this;
        }
        @Override
        public SlideByClickOrTilt build() {
            return instance;
        }
    }

    public interface Ilabelplus {
        Ilabelminus withLabelPlus(String labelPlus);
    }
    public interface Ilabelminus {
        IBtnplus withLabelMinus(String labelMinus);
    }
    public interface IBtnplus {
        IBtnminus withButtonPlus(Button buttonPlus);
    }
    public interface IBtnminus {
        IBuild withButtonMinus(Button buttonMinus);
    }
    public interface IBuild {
        IBuild withMinValue(int minValue);
        IBuild withMinDelta(int minDelta);
        IBuild withMaxDelta(int maxDelta);
        IBuild withMaxValue(int maxValue);
        SlideByClickOrTilt build();
    }

    public static Ilabelminus withLabelPlus(String labelPlus) {
        return new SlideByClickOrTilt.Builder(labelPlus);
    }

    @Override
    /*
    OK to call even if deprecated, see https://stackoverflow.com/questions/32258125
     */
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i(SlideByClickOrTilt.class.getName(), "activity attached");
        if (activity != null) {
            //ClickOrTiltListener listener = (ClickOrTiltListener)activity;
            this.runInActivity(activity);
        }
    }

    private void runInActivity(Activity activity) {
        lastCheckedTime = SystemClock.uptimeMillis();
        //container = activity;
        //buttonPlus.setOnClickListener(this);
        //buttonMinus.setOnClickListener(this);
        sensorManager = (SensorManager)activity.getSystemService(Context.SENSOR_SERVICE);
        gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        if (gravity != null)
            sensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_NORMAL);
        else
            Log.i(SlideByClickOrTilt.class.getName(), "No gravity sensor");
    }

    @Override
    public void onDetach() {
        Log.i(SlideByClickOrTilt.class.getName(), "activity detached");
        this.cleanUp();
        super.onDetach();
    }

    private void cleanUp() {
        if (gravity != null) {
            sensorManager.unregisterListener(this);
            gravity = null;
        }
    }

    @Override
    public void finalize() {
        Log.i(SlideByClickOrTilt.class.getName(), "garbage collected");
        cleanUp();
        try {
            super.finalize();
        } catch (Throwable ignored) {}
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // x>3 means minus; x<-3 means plus
        float x = event.values[0];
        if (x>3.0f) {
            if (SystemClock.uptimeMillis() - lastCheckedTime < 500) return;
            decrease();
            lastCheckedTime = SystemClock.uptimeMillis();
            Log.i(SlideByClickOrTilt.class.getName(), "value " + x);
        }
        else if (x<-3.0f) {
            if (SystemClock.uptimeMillis() - lastCheckedTime < 500) return;
            increase();
            lastCheckedTime = SystemClock.uptimeMillis();
            Log.i(SlideByClickOrTilt.class.getName(), "value " + x);
        }
    }

    private void increase() {
        if (delta <= 0) {// reverse tilt
            delta = minDelta;
        }
        else { // negative min is in fact max
            delta = Math.min(maxDelta, delta*2);
        }
        value = Math.min(value+delta, maxValue);
        this.onClickorTilt(value);
    }

    private void decrease() {
        if (delta >= 0) {// reverse tilt
            delta = 0-minDelta;
        }
        else { // negative min is in fact max
            delta = Math.max(0-maxDelta, delta*2);
        }
        value = Math.max(value+delta, minValue);
        this.onClickorTilt(value);
    }

    private void onClickorTilt(int value) {
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

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing
    }
    public void onClick(View v) {
        if (v.getId()==buttonPlus.getId())
            increase();
        else if (v.getId()==buttonMinus.getId())
            decrease();
        else
            value = Integer.MIN_VALUE;
        Log.i(SlideByClickOrTilt.class.getName(), "Delta is now "+delta);
    }

    /**
     * See stackoverflow.com/questions/3611457
     * @param activity The activity
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static void lockActivityOrientation(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        int rotation = display.getRotation();
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
        switch (rotation) {
            case Surface.ROTATION_90:
                if (width > height)
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                else
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                break;
            case Surface.ROTATION_180:
                if (height > width)
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                else
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                break;
            case Surface.ROTATION_270:
                if (width > height)
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                else
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            default :
                if (height > width)
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                else
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

}
