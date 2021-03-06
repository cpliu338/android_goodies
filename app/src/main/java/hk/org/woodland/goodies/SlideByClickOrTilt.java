package hk.org.woodland.goodies;

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
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A DialogFragment implementing a Slider whose value can be adjusted by tilt, falls back to 2 (+/-) buttons if no sensors
 * Example use:
     ClickOrTiltListener listener;
     SlideByClickOrTilt element = SlideByClickOrTilt
         .withClickOrTiltListener(MainActivity.this) // mandatory
         .withMaxValue(200) // mandatory
         .withMaxDelta(16) // defaults to 2
         .withMinDelta(1) // defaults to 1
         .withMinValue(1) // defaults to 0
         .build();
     element.show(MainActivity.this.getFragmentManager(), labelFragment);
 * Please use a builder to build an instance, and a ClickOrTiltListener to provide the feedback in the TextView
 * Lifecycle: onAttach -> onCreateDialog -> ... -> onDetach
 */

public class SlideByClickOrTilt extends DialogFragment implements View.OnClickListener, SensorEventListener {
    private int value, minValue, maxValue, delta, maxDelta, minDelta;
    private long lastCheckedTime;
    private Sensor gravity;
    private Activity activity;
    private ClickOrTiltListener listener;
    private SensorManager sensorManager;
    private Button buttonPlus, buttonMinus;
    private TextView feedback;

    public final String LABELPLUS = "+";
    public final String LABELMINUS = "-";
    public final String LABELHINT = "SlideByClickOrTilt";

    // CONSTANT types for custom strings
    public static final int PLUS = 1;
    public static final int MINUS = 2;
    public static final int HINT = 0;

    public static final int BASEID = 15434;

    public SlideByClickOrTilt() {
        /*
        These should be left as default and not brought forward to copy constructor
        value=0;
        gravity = null;
        delta = 0;
        */
        // maxValue is expected to be overridden
        maxValue = Integer.MAX_VALUE;
        minValue = 0; minDelta = 1; maxDelta = 2;
        //labelPlus = LABELPLUS; labelMinus = LABELMINUS;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Log.i(SlideByClickOrTilt.class.getName(), "dialog created");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final float scale = getActivity().getResources().getDisplayMetrics().density;
        /*
        LinearLayout v = new LinearLayout(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        v.setOrientation(LinearLayout.VERTICAL);
        */
        feedback = new TextView(getActivity());// (TextView)v.findViewById(R.id.feedback);
        feedback.setGravity(Gravity.CENTER);
        /*
        ViewGroup.LayoutParams vparams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        feedback.setLayoutParams(vparams);
         */

        //value = minValue;
        feedback.setText(
                listener == null ?
                        SlideByClickOrTilt.class.getSimpleName() :
                        listener.getFeedbackFromValue(value)
        );
        feedback.setSingleLine(false);
        feedback.setTextSize(TypedValue.COMPLEX_UNIT_PT, 14.0f);
        LinearLayout ll2 = new LinearLayout(getActivity());
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        ll2.setOrientation(LinearLayout.HORIZONTAL);
        buttonPlus = new Button(getActivity()); //v.findViewById(R.id.plus);
        LinearLayout.LayoutParams params_btn1 = new LinearLayout.LayoutParams(
                0, // 0 dp, refer layout weight
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0.15f // layout weight
        );
        buttonPlus.setLayoutParams(params_btn1);
        String s = listener.getCustomText(SlideByClickOrTilt.PLUS);
        buttonPlus.setText(s==null ? LABELPLUS : s);
        buttonPlus.setId(BASEID+0);
        buttonMinus = new Button(getActivity()); //v.findViewById(R.id.minus);
        LinearLayout.LayoutParams params_btn2 = new LinearLayout.LayoutParams(
                0, // 0 dp, refer layout weight
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0.15f // layout weight
        );
        buttonMinus.setLayoutParams(params_btn2);
        buttonMinus.setId(BASEID+1);
        s = listener.getCustomText(SlideByClickOrTilt.MINUS);
        buttonMinus.setText(s==null ? LABELMINUS : s);
        buttonPlus.setOnClickListener(this);
        buttonMinus.setOnClickListener(this);
        /*
        TextView hint = new TextView(getActivity());
        */
        LinearLayout.LayoutParams vparams2 = new LinearLayout.LayoutParams(
                0, // 0 dp, refer layout weight
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0.7f // layout weight
        );
        feedback.setLayoutParams(vparams2);
        /*hint.setGravity(Gravity.CENTER);
        s = listener.getCustomText(SlideByClickOrTilt.HINT);
        hint.setText(s==null ? LABELHINT : s);
        */
        ll2.addView(buttonMinus);
        ll2.addView(feedback);
        ll2.addView(buttonPlus);
        /*
        v.addView(feedback);
        v.addView(ll2);*/
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(ll2)
                // Add action buttons
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener!=null) listener.onConfirmWithValue(SlideByClickOrTilt.this.value);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SlideByClickOrTilt.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    private static class Builder implements IListener, IMaxValue, IBuild {
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
        @Override
        public IBuild withInitValue(int initValue) {
            if (instance.maxValue < initValue)
                instance.value=instance.maxValue;
            else if (initValue < instance.minValue)
                instance.value = instance.minValue;
            else
                instance.value = initValue;
            return this;
        }

        public Builder(ClickOrTiltListener listener) {
            instance.listener = listener;
        }
        /*
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
        */
        @Override
        public IMaxValue withClickOrTiltListener(ClickOrTiltListener listener) {
            instance.listener = listener;
            return this;
        }
        @Override
        public SlideByClickOrTilt build() {
            return instance;
        }
    }
/*
    public interface Ilabelplus {
        Ilabelminus withLabelPlus(String labelPlus);
    }
    public interface Ilabelminus {
        IBtnplus withLabelMinus(String labelMinus);
    }
    public interface IBtnplus {
        IBtnminus withButtonPlus(Button buttonPlus);
    }
    */
    public interface IMaxValue {
        IBuild withMaxValue(int maxValue);
    }
    public interface IListener {
        IMaxValue withClickOrTiltListener(ClickOrTiltListener listener);
    }
    public interface IBuild {
        IBuild withMinValue(int minValue);
        IBuild withMinDelta(int minDelta);
        IBuild withMaxValue(int maxValue);
        IBuild withMaxDelta(int maxDelta);
        IBuild withInitValue(int initValue);
        SlideByClickOrTilt build();
    }

    public static IMaxValue withClickOrTiltListener(ClickOrTiltListener listener) {
        return new SlideByClickOrTilt.Builder(listener);
    }

    @Override
    /*
    OK to call even if deprecated, see https://stackoverflow.com/questions/32258125
     */
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        SlideByClickOrTilt.lockActivityOrientation(activity);
        if (activity != null) {
            lastCheckedTime = SystemClock.uptimeMillis();
            sensorManager = (SensorManager)activity.getSystemService(Context.SENSOR_SERVICE);
            gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
            if (gravity != null)
                sensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_NORMAL);
            else
                Log.i(SlideByClickOrTilt.class.getName(), "No gravity sensor");
        }
    }

    @Override
    public void onDetach() {
        //Log.i(SlideByClickOrTilt.class.getName(), "activity detached");
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        if (gravity != null) {
            sensorManager.unregisterListener(this);
            gravity = null;
        }
        super.onDetach();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // x>3 means minus; x<-3 means plus
        float x = event.values[0];
        if (x>3.0f) {
            if (SystemClock.uptimeMillis() - lastCheckedTime < 500) return;
            decrease();
            lastCheckedTime = SystemClock.uptimeMillis();
            //Log.i(SlideByClickOrTilt.class.getName(), "value " + x);
        }
        else if (x<-3.0f) {
            if (SystemClock.uptimeMillis() - lastCheckedTime < 500) return;
            increase();
            lastCheckedTime = SystemClock.uptimeMillis();
            //Log.i(SlideByClickOrTilt.class.getName(), "value " + x);
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
        feedback.setText(listener.getFeedbackFromValue(value));
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
        //Log.i(SlideByClickOrTilt.class.getName(), "Delta is now "+delta);
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