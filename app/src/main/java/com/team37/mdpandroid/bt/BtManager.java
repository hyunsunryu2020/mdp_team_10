package com.team37.mdpandroid.bt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.util.Collections;
import java.util.Set;

@SuppressLint("MissingPermission")
public class BtManager {
    private final int BT_CONNECT = 0;
    private final int BT_SCAN = 1;
    private final int BT_ADVERTISE = 2;
    private final int FINE_LOCATION = 3;
    private final int BACKGROUND_LOCATION = 4;

    public static final int BT_TURN_ON = 11;


    Context mBluetoothPage;
    Activity mBluetoothPageActivity;
    static BluetoothAdapter mBluetoothAdapter;
    BtConnector mBtConnector;

    private ArrayAdapter<String> adapterNearbyDevices;

    private static volatile BtManager btManager;

    private BtManager() {
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public static BtManager getBtManager(){
        if (btManager == null){
            synchronized (BtManager.class){
                if (btManager == null){
                    btManager = new BtManager();
                }
            }
        }
        return btManager;
    }

    public void setContext(Context context){
        mBluetoothPage = context;
        mBluetoothPageActivity = (Activity) context;
    }

    public boolean isBluetoothAvailable() {
        if (mBluetoothAdapter == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isBluetoothEnabled() {
        return mBluetoothAdapter.isEnabled();
    }

    public boolean isBluetoothDiscovering() {
        return mBluetoothAdapter.isDiscovering();
    }

    public void turnOn() {
        // Log.e("Bt", "Turned On")
        checkPermissions();

        if (!isBluetoothEnabled()) {
            showToast("Enabling Bluetooth");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mBluetoothPageActivity.startActivityForResult(enableBtIntent, BT_TURN_ON);
        } else {
            showToast("Bluetooth already enabled");
        }
    }

    public void turnOff() {
        //Log.e("Bt", "Turned Off")
        checkPermissions();

        if (isBluetoothEnabled()) {
            showToast("Disabling Bluetooth");
            mBluetoothAdapter.disable();
        } else {
            showToast("Bluetooth already disabled");
        }
    }

    public void makeDiscoverable() {
        checkPermissions();

        if (!mBluetoothAdapter.isDiscovering()) {
            showToast("Making discoverable");
            Intent makeDiscoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            mBluetoothPage.startActivity(makeDiscoverableIntent);
            mBluetoothPageActivity.startActivityForResult(makeDiscoverableIntent,1);
        } else {
            showToast("Bluetooth already discovering");
        }
    }

    public Set<BluetoothDevice> getPairedDevices(){
        checkPermissions();
        Set<BluetoothDevice> pairedDeviceSet;

        if (mBluetoothAdapter.isEnabled()) {
            pairedDeviceSet = mBluetoothAdapter.getBondedDevices();
        } else {
            pairedDeviceSet = Collections.<BluetoothDevice>emptySet();
            showToast("Cannot show paired devices when Bluetooth is disabled");
        }
        return pairedDeviceSet;
    }

    public void showNearbyDevices(){
        checkPermissions();
        if (mBluetoothAdapter.isEnabled()) {
            if (isBluetoothDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }

            if(mBluetoothAdapter.startDiscovery()) {
                showToast("Discovery has started");
            } else {
                showToast("Discovery has failed");
            }
        } else {
            showToast("Cannot show nearby devices when Bluetooth is disabled");
        }

    }

    public BluetoothDevice getDeviceFromAddress(String address) {
        return mBluetoothAdapter.getRemoteDevice(address);
    }

    public String getDeviceInfo() {
        // return "Galaxy Tab A (8.0\", 2019)";
        return mBluetoothAdapter.getName() + "\n" + mBluetoothAdapter.getAddress();
    }

    public static String getBTMajorDeviceClass(int major) {
        switch (major) {
            case BluetoothClass.Device.Major.AUDIO_VIDEO:
                return "AUDIO_VIDEO";
            case BluetoothClass.Device.Major.COMPUTER:
                return "COMPUTER";
            case BluetoothClass.Device.Major.HEALTH:
                return "HEALTH";
            case BluetoothClass.Device.Major.IMAGING:
                return "IMAGING";
            case BluetoothClass.Device.Major.MISC:
                return "MISC";
            case BluetoothClass.Device.Major.NETWORKING:
                return "NETWORKING";
            case BluetoothClass.Device.Major.PERIPHERAL:
                return "PERIPHERAL";
            case BluetoothClass.Device.Major.PHONE:
                return "PHONE";
            case BluetoothClass.Device.Major.TOY:
                return "TOY";
            case BluetoothClass.Device.Major.UNCATEGORIZED:
                return "UNCATEGORIZED";
            case BluetoothClass.Device.Major.WEARABLE:
                return "AUDIO_VIDEO";
            default:
                return "unknown!";
        }
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
//            if (ActivityCompat.checkSelfPermission(mBluetoothPage, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(mBluetoothPageActivity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, BT_CONNECT);
//            }
//            if (ActivityCompat.checkSelfPermission(mBluetoothPage, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(mBluetoothPageActivity, new String[]{Manifest.permission.BLUETOOTH_SCAN}, BT_SCAN);
//            }
//            if (ActivityCompat.checkSelfPermission(mBluetoothPage, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(mBluetoothPageActivity, new String[]{Manifest.permission.BLUETOOTH_ADVERTISE}, BT_ADVERTISE);
//            }
            if (ActivityCompat.checkSelfPermission(mBluetoothPage, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mBluetoothPageActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION);
            }
            if (ActivityCompat.checkSelfPermission(mBluetoothPage, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mBluetoothPageActivity, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION);
            }
        }
    }

    private void showToast(String msg) {
        Toast.makeText(mBluetoothPage, msg, Toast.LENGTH_SHORT).show();
    }
}
