package hk.org.woodland.mytestbed;

import java.util.Objects;

/**
 * A callback interface from SlideByClickOrTilt, usually an Activity, with some onXXX listeners
 */

interface ClickOrTiltListener {
    public String getFeedbackFromValue(int value);
    public void onConfirmWithValue(int value);
}
