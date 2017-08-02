package hk.org.woodland.lolo;

import android.graphics.Canvas;
import android.util.Log;

/**
 * Created by cp_liu on 8/2/17.
 */

public class LoloDrawThread extends Thread {

    LoloView loloView;
    private boolean running = false;

    public LoloDrawThread(LoloView view) {
        loloView = view;
    }

    public void setRunning(boolean run) {
        running = run;
    }

    @Override
    public void run() {
        // Usually while running, but we draw game2, sleep, draw game1, end
        //Log.d("Thread", "Running");
        if(running){
            Canvas canvas = null;
            synchronized (loloView.getHolder()) {
                canvas = loloView.getHolder().lockCanvas();
                if (canvas == null) return;
                loloView.setUsingGame1(false);
                loloView.drawSomething(canvas);
                loloView.getHolder().unlockCanvasAndPost(canvas);
                try {
                    sleep(300);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                canvas = loloView.getHolder().lockCanvas();
                if (canvas == null) return;
                loloView.setUsingGame1(true);
                loloView.drawSomething(canvas);
                loloView.getHolder().unlockCanvasAndPost(canvas);
            }
            loloView.setUsingGame1(true); // just in case
        }
    }

}
