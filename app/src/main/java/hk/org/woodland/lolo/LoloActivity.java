package hk.org.woodland.lolo;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import hk.org.woodland.mytestbed.MainActivity;
import hk.org.woodland.mytestbed.R;

public class LoloActivity extends Activity {

    private static final String TAG = "LoloAct";

    LoloView loloView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lolo);
//        loloView = new LoloView(this);
//        loloView.requestFocus();
        Button btn1 = (Button)findViewById(R.id.home);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoloActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        //getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loloView = (LoloView)findViewById(R.id.lolo);
        if (loloView != null) {
            Log.d(TAG, String.format("%d,%d,%d,%d", loloView.getLeft(), loloView.getTop(), loloView.getRight(), loloView.getBottom()));
            loloView.setMinimumHeight(loloView.getRight());
        }
    }

}
