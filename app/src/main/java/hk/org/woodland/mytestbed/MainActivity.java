package hk.org.woodland.mytestbed;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements ClickOrTiltListener {

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
                                .withClickOrTiltListener(MainActivity.this)
                                .withMaxValue(200).withMaxDelta(16)
                                .build();
                element.show(MainActivity.this.getFragmentManager(), "SlideByClickOrTilt");
            }
        });
    }

    @Override
    public String getFeedbackFromValue(int value) {
        return getString(R.string.feedback, value, new java.util.Date(System.currentTimeMillis()+value*60000L));
    }

    public void onConfirmWithValue(int value) {
        Toast toast = Toast.makeText(this, MainActivity.class.getSimpleName()+value, Toast.LENGTH_SHORT);
        toast.show();
    }

}

