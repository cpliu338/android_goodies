package hk.org.woodland.mytestbed;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class FakeRingingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fake_ringing);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(FakeRingingActivity.class.getName(), "Resumed");
    }
}
