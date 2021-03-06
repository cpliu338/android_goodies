package hk.org.woodland.mytestbed;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import hk.org.woodland.goodies.WakeLock;

public class FakeRingingActivity extends Activity {

    private String networkCarrier;
    private MediaPlayer mp;
    private Button answerCall;
    private boolean muted;
    public static final String CARRIER = "carrier";
    public static final String FAKENAME = "fake_name";
    public static final String FAKENUMBER = "fake_number";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fake_ringing);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        TextView fakeName = (TextView)findViewById(R.id.chosenfakename);
        TextView fakeNumber = (TextView)findViewById(R.id.chosenfakenumber);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //networkCarrier = "Smartone";
        TextView titleBar = (TextView)findViewById(R.id.textView1);
        titleBar.setText(prefs.getString(CARRIER, CARRIER));
        fakeName.setText(prefs.getString(FAKENAME, FAKENAME));
                //getIntent().getStringExtra(FakeCallReceiver.FAKENAME));
        fakeNumber.setText(prefs.getString(FAKENUMBER, FAKENUMBER));
                //getIntent().getStringExtra(FakeCallReceiver.FAKENUMBER));
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        mp = MediaPlayer.create(getApplicationContext(), notification);
        mp.start();

        answerCall = (Button)findViewById(R.id.answercall);
			Button rejectCall = (Button)findViewById(R.id.rejectcall);

        answerCall.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (muted) {
                    Intent startMain = new Intent(FakeRingingActivity.this, MainActivity.class);
                    startActivity(startMain);
                }
                else {
                    muted = true;
                    answerCall.setBackgroundDrawable(FakeRingingActivity.this.getResources().getDrawable(R.drawable.ic_micoff));
                    mp.stop();
                }
            }
        });
        rejectCall.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mp.stop();
				Intent startMain = new Intent(Intent.ACTION_MAIN);
				startMain.addCategory(Intent.CATEGORY_HOME);
				startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(startMain);
            }
        });

        WakeLock.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(FakeRingingActivity.class.getName(), "Resumed");
    }
}
