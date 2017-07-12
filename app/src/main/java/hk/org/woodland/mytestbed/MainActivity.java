package hk.org.woodland.mytestbed;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements ClickOrTiltListener {

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
            testSlideByClickOrTilt();/*
                AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(MainActivity.this, FakeCallReceiver.class);
                intent.putExtra(FakeCallReceiver.FAKENAME, getString(R.string.app_name));
                intent.putExtra(FakeCallReceiver.FAKENUMBER, getString(R.string.plus)+v.getId());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 100000L, pendingIntent);
                Toast.makeText(MainActivity.this, getString(R.string.app_name), Toast.LENGTH_SHORT).show();*/
            }
        });
    }

    private void testSlideByClickOrTilt() {
        SlideByClickOrTilt element;
        element = SlideByClickOrTilt
                .withClickOrTiltListener(MainActivity.this)
                .withMaxValue(200).withMaxDelta(16)
                .withMinValue(1).withInitValue(8)
                .build();
        element.show(MainActivity.this.getFragmentManager(), "SlideByClickOrTilt");
    }

    @Override
    public String getFeedbackFromValue(int value) {
        //Calendar c = Calendar.getInstance();
        //c.add(Calendar.MINUTE, value);
        //c.setTimeInMillis(prefs.getLong(MuteService.TARGET_MILLIS, System.currentTimeMillis()));
        return (getString(R.string.feedback, value, new java.util.Date(System.currentTimeMillis()+value*60000L)));
    }

    @Override
    public void onConfirmWithValue(int value) {
        long ms = value * 60000L;
        //this.onMuteSelected(ms);
        Toast toast = Toast.makeText(this, MainActivity.class.getSimpleName()+value, Toast.LENGTH_SHORT);
        toast.show();
        /*
        Snackbar.make(findViewById(android.R.id.content), "Replace with your own action"+value, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
                        */
    }

    @Override
    public String getCustomText(int type) {
        switch (type) {
            case SlideByClickOrTilt.MINUS: return getString(R.string.minus);
            case SlideByClickOrTilt.PLUS: return getString(R.string.plus);
            case SlideByClickOrTilt.HINT: return getString(R.string.hint);
        }
        return null;
    }
/*
    @Override
    protected void onResume() {
        super.onResume();
        SlideByClickOrTilt.lockActivityOrientation(this);
        element.runInActivity(this);
        This should be triggered in button click
    }

    @Override
    protected void onPause() {
        super.onPause();
        element.cleanUp();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }
         */

}

