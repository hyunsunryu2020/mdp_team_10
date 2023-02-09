package com.team37.mdpandroid.bt;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.team37.mdpandroid.gui.util.ConfigUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressLint("MissingPermission")
public class BtConnector extends Activity {
    private static final String SERVICE_UUID_STRING = "00001101-0000-1000-8000-00805F9B34FB";
    private static final UUID SERVICE_UUID = UUID.fromString(SERVICE_UUID_STRING);
    private static final String SERVICE_NAME = "MDP-Group-37";

    private static volatile BtConnector btConnectorInstance;

    private Context context;
    private Handler handler;
    private BluetoothAdapter btAdapter;
    private BluetoothDevice lastConnectedDevice;

    private AcceptThread acceptThread;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;

    private static final int REQUEST_ENABLE_BT = 101;

    private boolean wasConnected = false;

    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;
    private int state;

    private BtConnector() {
        // Initialise the bluetooth adapter for the device
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        setState(STATE_NONE);
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public boolean getWasConnected(){
        return wasConnected;
    }


    public static BtConnector getBtConnectorInstance() {
        if (btConnectorInstance == null) {
            btConnectorInstance = new BtConnector();
        }

        return btConnectorInstance;
    }

    public synchronized void start() {
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (acceptThread == null) {
            acceptThread = new AcceptThread();
            acceptThread.start();
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        setState(STATE_LISTEN);
    }

    public synchronized void stop() {
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        setState(STATE_NONE);
    }

    public void connect(BluetoothDevice device) {
        if (state == STATE_CONNECTING) {
            connectThread.cancel();
            connectThread = null;
        }

        connectThread = new ConnectThread(device);
        connectThread.start();

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        setState(STATE_CONNECTING);
    }

    public int getState() {
        return state;
    }

    private synchronized void setState(int state) {
        this.state = state;
        if (handler != null)
            handler.obtainMessage(ConfigUtil.MESSAGE_STATE_CHANGED, state, -1).sendToTarget();
    }

    public String getDeviceName() {
        return lastConnectedDevice.getName();
    }

    public void setBtEnabledStatus(boolean enable) {
        if (enable) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    // Class for connecting Server-side
    private class AcceptThread extends Thread {
        private final BluetoothServerSocket serverSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = btAdapter.listenUsingRfcommWithServiceRecord(SERVICE_NAME, SERVICE_UUID);
            } catch (IOException e) {
                 Log.e("AcceptThread", "Socket's listen() method failed", e);
            }
            serverSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;

            while (true) {
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                     Log.e("AcceptThread", "Socket's accept() method failed", e);
                    try {
                        serverSocket.close();
                    } catch (IOException e1) {
                        Log.e("Accept->Close", e.toString());
                    }
                    break;
                }

                if (socket != null) {
                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.
                    manageMyConnectedSocket(socket);
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                         e.printStackTrace();
                    }
                    break;
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            try {
                serverSocket.close();
            } catch (IOException e) {
                 Log.e("AcceptThread", "Could not close the connect socket", e);
            }
        }
    }

    // Start thread listening as Server using AcceptThread
    public void startServerThread() {
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (acceptThread == null) {
            acceptThread = new AcceptThread();
            acceptThread.start();
        }
    }

    // Class for connecting Client-side
    private class ConnectThread extends Thread {
        private final BluetoothSocket socket;
        private final BluetoothDevice device;

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            this.device = device;

            try {
                tmp = device.createRfcommSocketToServiceRecord(SERVICE_UUID);
            } catch (IOException e) {
                Log.e("ConnectThread", "Socket's create() method failed", e);
            }
            socket = tmp;
        }

        public void run() {
            btAdapter.cancelDiscovery();

            try {
                socket.connect();
            } catch (IOException connectException) {
                Log.e("ConnectThread", "Could not connect to the client socket " + socket.getRemoteDevice());
                connectException.printStackTrace();
                try {
                    socket.close();
                } catch (IOException closeException) {
                     Log.e("ConnectThread", "Could not close the client socket", closeException);
                }
                connectionFailed();
                return;
            }

            synchronized (BtConnector.this) {
                connectThread = null;
            }

            manageMyConnectedSocket(socket);
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                 Log.e("ConnectThread", "Could not close the client socket", e);
            }
        }
    }

    // Reconnect to same device after getting disconnected
    public void reconnectToMostRecentDevice() {
        if (state != STATE_CONNECTED) {
            connectThread = new ConnectThread(lastConnectedDevice);
            connectThread.start();
            Log.e("BtReconnect", "Trying to reconnect");
        }
    }

    // Start thread after device connected
    public synchronized void manageMyConnectedSocket(BluetoothSocket socket) {
        lastConnectedDevice = socket.getRemoteDevice();

        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        connectedThread = new ConnectedThread(socket);
        connectedThread.start();

        Message message = handler.obtainMessage(ConfigUtil.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(ConfigUtil.TOAST, "Connected to " + getDeviceName());
        message.setData(bundle);
        handler.sendMessage(message);

        wasConnected = true;
        setState(STATE_CONNECTED);
    }

    // Class for transferring data
    // Use after establishing connection
    private class ConnectedThread extends Thread {
        private final BluetoothSocket socket;
        private final InputStream inStream;
        private final OutputStream outStream;
        private byte[] streamBuffer;

        public ConnectedThread(BluetoothSocket socket) {
            this.socket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                 Log.e("ConnectedThread", "Error occurred when creating input stream", e);
            }

            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                 Log.e("ConnectedThread", "Error occurred when creating output stream", e);
            }

            inStream = tmpIn;
            outStream = tmpOut;
        }

        public void run() {
            streamBuffer = new byte[1024];
            int numBytes;

            while (true) {
                try {
                    numBytes = inStream.read(streamBuffer);
                    String incomingmessage = new String(streamBuffer, 0, numBytes);

                    Message message = handler.obtainMessage(ConfigUtil.MESSAGE_READ);
                    Bundle bundle = new Bundle();
                    bundle.putString(ConfigUtil.MESSAGE_BODY, incomingmessage);
                    message.setData(bundle);
                    handler.sendMessage(message);
                } catch (IOException e) {
                    Log.d("ConnectedThread", "Input stream was disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                outStream.write(bytes);

                String outgoingmessage = new String(bytes);
                Message message = handler.obtainMessage(ConfigUtil.MESSAGE_WRITE);
                Bundle bundle = new Bundle();
                bundle.putString(ConfigUtil.MESSAGE_BODY, outgoingmessage);
                message.setData(bundle);
                handler.sendMessage(message);
            } catch (IOException e) {
                 Log.e("ConnectedThread", "Error occurred when sending data", e);
            }
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e("ConnectedThread", "Error occurred when cancelling thread", e);
            }
        }
    }

    public void write(byte[] out) {
        ConnectedThread connectedThread1;
        synchronized (this) {
            if (state != STATE_CONNECTED) {
                return;
            }

            connectedThread1 = connectedThread;
        }

        connectedThread1.write(out);
    }

    public void write(String message) {
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        ConnectedThread connectedThread1;
        synchronized (this) {
            if (state != STATE_CONNECTED) {
                return;
            }

            connectedThread1 = connectedThread;
        }

        connectedThread1.write(messageBytes);
    }

    private void connectionLost() {
        Message message = handler.obtainMessage(ConfigUtil.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(ConfigUtil.TOAST, "Connection Lost");
        message.setData(bundle);
        handler.sendMessage(message);

        BtConnector.this.start();
    }

    private synchronized void connectionFailed() {
        Message message = handler.obtainMessage(ConfigUtil.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(ConfigUtil.TOAST, "Can't connect to the device");
        message.setData(bundle);
        handler.sendMessage(message);

        BtConnector.this.start();
    }
}
