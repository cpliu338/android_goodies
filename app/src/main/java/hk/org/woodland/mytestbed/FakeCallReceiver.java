package hk.org.woodland.mytestbed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import hk.org.woodland.goodies.WakeLock;

/**
 * Based on https://inducesmile.com/android/android-fake-call-application-tutorial
 */

public class FakeCallReceiver extends BroadcastReceiver {

    public static final String FAKENAME = "fakename";
    public static final String FAKENUMBER = "fakenumber";
    @Override
    public void onReceive(Context context, Intent intent) {
        WakeLock.acquire(context, FakeCallReceiver.class.getName());
        Intent i = new Intent(context.getApplicationContext(), FakeRingingActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra(FAKENAME, intent.getStringExtra(FAKENAME));
        i.putExtra(FAKENUMBER, intent.getStringExtra(FAKENUMBER));
        context.startActivity(i);
    }
}
