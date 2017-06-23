package hk.org.woodland.mytestbed;

import java.util.Objects;

/**
 * A callback interface from SlideByClickOrTilt, usually an Activity, with some onXXX listeners
 */

interface ClickOrTiltListener {
    void onClickorTilt(int value);
    Object getSystemService(String service);
}
