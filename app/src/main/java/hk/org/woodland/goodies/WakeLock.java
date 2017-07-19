package hk.org.woodland.goodies;

import android.content.Context;
import android.os.PowerManager;

/**
 * Created by cp_liu on 14/8/2016.
 */
public abstract class WakeLock {
    private static PowerManager.WakeLock wakeLock;

    public static void acquire(Context ctx, String tag) {
        if (wakeLock != null) wakeLock.release();

        PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, tag);
        wakeLock.acquire();
    }

    public static void release() {
        if (wakeLock != null) wakeLock.release(); wakeLock = null;
    }
}
