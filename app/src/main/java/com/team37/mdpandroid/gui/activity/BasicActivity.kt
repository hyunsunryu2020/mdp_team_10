package com.team37.mdpandroid.gui.activity

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.getbase.floatingactionbutton.FloatingActionButton
import com.team37.mdpandroid.R
import com.team37.mdpandroid.bt.BtConnector
import com.team37.mdpandroid.bt.BtManager
import com.team37.mdpandroid.gui.util.ConfigUtil

open class BasicActivity : AppCompatActivity(){

    companion object{

        var btManager: com.team37.mdpandroid.bt.BtManager = BtManager.getBtManager()
        private var bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        var btConnector: BtConnector = BtConnector.getBtConnectorInstance()
        private val activities: MutableList<Activity> = ArrayList()
        var currentPage = ConfigUtil.MAIN_PAGE
        var status = "IDLE"

        private var hasReconnQueued = false
        private var context: Context? = null

        private val reconnectionHandler = Handler(Looper.myLooper()!!)

        var reconnectionRunnable: Unit = object : Runnable {
            override fun run() {
                try {
                    Log.e("BtStatus", btConnector.state.toString())
                    if (btConnector.wasConnected && (btConnector.state == BtConnector.STATE_LISTEN || btConnector.state == BtConnector.STATE_NONE)) {
                        Thread.sleep(1000)
                        status = "Trying to Reconnect"
                        Toast.makeText(context, "Trying to Reconnect", Toast.LENGTH_SHORT).show()
                        btConnector.reconnectToMostRecentDevice()
                    }
                    reconnectionHandler.postDelayed(this, 1000)
                } catch (e: Exception) {
                    Log.e("Reconnection runnable", "Reconnecting False")
                    e.printStackTrace()
                    Toast.makeText(context, "Failed to reconnect, trying in 5 seconds", Toast.LENGTH_SHORT).show()
                    hasReconnQueued = true
                    reconnectionHandler.postDelayed(this, 7000)
                }
            }
        }.run()

        fun addActivity(activity: Activity) {
            activities.add(activity)
        }

        fun removeActivity(activity: Activity) {
            activities.remove(activity)
        }

        fun getTotalActivities(): Int {
            return activities.size
        }

        fun finishAll() {
            for (activity in activities) {
                if (!activity.isFinishing) {
                    activity.finish()
                }
            }
        }
    }



    fun checkBtStatus(): Boolean{
        if (btConnector!!.state == BtConnector.STATE_CONNECTED)
            return true
        if (btConnector!!.state ==BtConnector.STATE_NONE)
            Toast.makeText(this, "Bluetooth not connected!", Toast.LENGTH_SHORT).show()
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        addActivity(this)
        context = applicationContext
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        removeActivity(this)
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (getTotalActivities() == 1) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure to exit?")
                .setPositiveButton("Yes") { _, _ -> finish() }
                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            val dialog = builder.create()
            dialog.show()
        }
        else
            finish()
    }


}