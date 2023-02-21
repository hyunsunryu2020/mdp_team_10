package com.team37.mdpandroid.bt;

import android.widget.Toast;

public interface BtMessages {
    public static final int TOAST_DURATION = Toast.LENGTH_SHORT;
    public static final String BT_NOT_AVAILABLE = "Bluetooth is not available.";
    public static final String BT_AVAILABLE = "Bluetooth is available.";
    public static final String BT_NOT_ENABLED = "Bluetooth is not enabled/on.";
    public static final String BT_ENABLED = "Bluetooth is enabled/on.";
    String TURN_RIGHT = "tr";
    String TURN_LEFT = "tl";
    String FORWARD = "f";
}
