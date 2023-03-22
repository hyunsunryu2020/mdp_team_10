package com.team37.mdpandroid.gui.activity

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.team37.mdpandroid.R
import com.team37.mdpandroid.bt.BtConnector
import com.team37.mdpandroid.bt.BtManager
import com.team37.mdpandroid.gui.util.ConfigUtil

class MessagePage: BasicActivity() {

    var messageToSend: EditText? = null
    var messageReceived: TextView? = null
    var clearButton: Button? = null
    var sendButton: Button? = null

//    companion object{
//
//    }

    private val handler = Handler(
        Looper.myLooper()!!
    ) { msg ->
        when (msg.what) {
//            ConfigUtil.MESSAGE_STATE_CHANGED -> when (msg.arg1) {
//            }
            ConfigUtil.MESSAGE_READ -> {
                Log.e("Message", msg.data.getString(ConfigUtil.MESSAGE_BODY)!!)
                messageReceived!!.text = msg.data.getString(ConfigUtil.MESSAGE_BODY)
            }
//            ConfigUtil.MESSAGE_WRITE -> {}
//            ConfigUtil.MESSAGE_TOAST -> showToast(msg.data.getString(TOAST))
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bluetooth_message_acitivity)
        currentPage = ConfigUtil.BT_MESSAGE_PAGE
//        initFloatingButtons(this)
        initView()
        initBlueTooth()
    }

    private fun initView(){
        clearButton = findViewById(R.id.clearButton)
        sendButton = findViewById(R.id.sendButton)
        messageToSend = findViewById(R.id.messageToSend)
        messageReceived = findViewById(R.id.messageToReceive)

        clearButton!!.setOnClickListener{
            messageToSend!!.text.clear()
        }


        sendButton!!.setOnClickListener{
            btConnector!!.write(messageToSend!!.text.toString())
            Log.e("BtMessage",messageToSend!!.text.toString())
            messageToSend!!.text.clear()
        }


    }

    private fun initBlueTooth(){
        btConnector!!.setHandler(handler)
    }
}