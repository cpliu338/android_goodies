package hk.org.woodland.mytestbed;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import hk.org.woodland.goodies.WakeLock;

public class FakeRingingActivity extends Activity {

    private String networkCarrier;
    private MediaPlayer mp;

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
        networkCarrier = "Smartone";
        TextView titleBar = (TextView)findViewById(R.id.textView1);
        titleBar.setText(networkCarrier);
        fakeName.setText(getIntent().getStringExtra(FakeCallReceiver.FAKENAME));
        fakeNumber.setText(getIntent().getStringExtra(FakeCallReceiver.FAKENUMBER));
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        mp = MediaPlayer.create(getApplicationContext(), notification);
        mp.start();

        Button answerCall = (Button)findViewById(R.id.answercall);
        Button rejectCall = (Button)findViewById(R.id.rejectcall);

        answerCall.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mp.stop();
            }
        });
        rejectCall.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mp.stop();
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
