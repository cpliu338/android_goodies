package hk.org.woodland.goodies;

import java.util.Objects;

/**
 * A callback interface from SlideByClickOrTilt, usually implemented by the calling Activity
 */

public interface ClickOrTiltListener {
    public String getFeedbackFromValue(int value);

    /**
     * Let user override custom Strings, preferably as getString(R.string.xx)
     * @param type an integer type corresponding to constant int defined in SlideByClickOrTilt
     * @return the string (respecting locale), or null for default
     */
    public String getCustomText(int type);

    /**
     * Take the action with the chosen value, after the dialog is dismissed
     * @param value the chosen value
     */
    public void onConfirmWithValue(int value);
}
