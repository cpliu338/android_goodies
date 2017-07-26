package hk.org.woodland.mytestbed;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import hk.org.woodland.goodies.ClickOrTiltListener;
import hk.org.woodland.goodies.SlideByClickOrTilt;
import hk.org.woodland.lolo.LoloActivity;

public class MainActivity extends Activity implements ClickOrTiltListener {

    private Sensor gravity;
    private SensorManager sensorManager;
    private Button btn1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        setContentView(R.layout.activity_main);
        btn1 = (Button)findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, LoloActivity.class);
            startActivity(intent);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.fakecall:
                //SlideByClickOrTilt.lockActivityOrientation(this);
                setFakeTime();
                break;
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setFakeTime() {
        SlideByClickOrTilt element;
        element = SlideByClickOrTilt
                .withClickOrTiltListener(MainActivity.this)
                .withMaxValue(200).withMaxDelta(16)
                .withMinValue(1).withInitValue(5)
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
            /*
        Toast toast = Toast.makeText(this, MainActivity.class.getSimpleName()+value, Toast.LENGTH_SHORT);
        toast.show();
        */
                AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(this, FakeCallReceiver.class);
                //intent.putExtra(FakeCallReceiver.FAKENAME, getString(R.string.app_name));
                //intent.putExtra(FakeCallReceiver.FAKENUMBER, getString(R.string.plus)+value);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + value*60000L, pendingIntent);
                Toast.makeText(MainActivity.this, getString(R.string.call_at, new java.util.Date(System.currentTimeMillis()+value*60000L)), Toast.LENGTH_SHORT).show();
        /*
        Snackbar.make(findViewById(android.R.id.content), "Replace with your own action"+value, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
                        */
    }

    @Override
    public String getCustomText(int type) {
        /*
        switch (type) {
            case SlideByClickOrTilt.MINUS: return getString(R.string.minus);
            case SlideByClickOrTilt.PLUS: return getString(R.string.plus);
            case SlideByClickOrTilt.HINT: return getString(R.string.hint);
        }
        */
        return null;
    }
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String syncConnPref = sharedPref.getString("carrier", "Hello World");
        btn1.setText(syncConnPref);
    }

/*
    @Override
    protected void onPause() {
        super.onPause();
        element.cleanUp();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }
         */

}

