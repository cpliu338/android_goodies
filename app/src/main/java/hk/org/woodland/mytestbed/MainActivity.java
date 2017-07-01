package hk.org.woodland.mytestbed;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity { //implements ClickOrTiltListener {

    private SlideByClickOrTilt element;
    private Sensor gravity;
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn1 = (Button)findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                element = //new SlideByClickOrTilt.Builder().withMaxValue(200).withMaxDelta(16).build();
                        SlideByClickOrTilt.withLabelPlus(getString(R.string.plus))
                                .withLabelMinus(getString(R.string.minus))
                                .withButtonPlus((Button)findViewById(R.id.plus))
                                .withButtonMinus((Button)findViewById(R.id.minus))
                                .withMaxValue(200).withMaxDelta(16)
                                .build();
                element.show(MainActivity.this.getFragmentManager(), "SlideByClickOrTilt");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SlideByClickOrTilt.lockActivityOrientation(this);
        /*element.runInActivity(this);
        This should be triggered in button click
         */
    }

    @Override
    protected void onPause() {
        super.onPause();
        //element.cleanUp();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

}

