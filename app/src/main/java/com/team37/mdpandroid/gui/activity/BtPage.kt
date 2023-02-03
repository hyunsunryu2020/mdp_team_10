package com.team37.mdpandroid.gui.activity

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.rahatarmanahmed.cpv.CircularProgressView
import com.suke.widget.SwitchButton
import com.team37.mdpandroid.R
import com.team37.mdpandroid.bt.BtConnector
import com.team37.mdpandroid.bt.BtManager
import com.team37.mdpandroid.bt.BtManager.getBTMajorDeviceClass
import com.team37.mdpandroid.gui.data.BtDevice
import com.team37.mdpandroid.gui.util.ConfigUtil

class BtPage : BasicActivity() {

    private var switchButton: SwitchButton? = null
    private var deviceName: TextView? = null
    private var refresh: TextView? = null
    private var refreshProgress: CircularProgressView? = null

    private var pairedDevices = mutableListOf<BtDevice>()
    private val availableDevices = mutableListOf<BtDevice>()

    private var pairedDeviceView: RecyclerView? = null
    private var availableDeviceView: RecyclerView? = null

    private var isChecked = false

    private val handler = Handler(
        Looper.myLooper()!!
    ) { msg ->
        when (msg.what) {
            ConfigUtil.MESSAGE_STATE_CHANGED -> when (msg.arg1) {
                BtConnector.STATE_NONE -> {
                    Toast.makeText(this, "Not connected", Toast.LENGTH_SHORT).show()
                    status = "IDLE"
                }
                BtConnector.STATE_LISTEN -> Toast.makeText(this, "Not connected", Toast.LENGTH_SHORT).show()
                BtConnector.STATE_CONNECTING -> Toast.makeText(this, "Connecting", Toast.LENGTH_SHORT).show()
                BtConnector.STATE_CONNECTED -> {
                    BtDeviceAdapter.connectedDevice!!.connectionPoint!!.setBackgroundResource(R.drawable.green_point)
                    BtDeviceAdapter.connectedDevice!!.connectionStatus!!.setTextColor(Color.parseColor("#4CAF50"))
                    BtDeviceAdapter.connectedDevice!!.connectionStatus!!.text = "Connected"
                    BtDeviceAdapter.connectedDevice!!.string!!.visibility = View.GONE
                    status = "Bluetooth Connected"
                    Toast.makeText(this, "Connected to " + btConnector!!.getDeviceName(), Toast.LENGTH_SHORT).show()
                }
            }
            ConfigUtil.MESSAGE_READ -> {
                msg.data.getString(ConfigUtil.MESSAGE_BODY)?.let { Log.e("test", it) }
            }
            ConfigUtil.MESSAGE_WRITE -> {}
//            ConfigUtil.MESSAGE_TOAST -> showToast(msg.data.getString(TOAST))
        }
        false
    }

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                val device = intent
                    .getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                val newAvailableDeviceInfo = device!!.name + ", " + BtManager.getBTMajorDeviceClass(
                    device.bluetoothClass.majorDeviceClass
                ) + "\n" + device.address
                if (device!!.name != null)
                    availableDevices.add(BtDevice(device.name, device.address))
                else
                    availableDevices.add(BtDevice("Unknown Name", device.address))
                availableDeviceView!!.adapter = BtDeviceAdapter(availableDevices, btConnector!!, btManager!!)
                availableDeviceView!!.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                Log.i("BT", newAvailableDeviceInfo)
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
                refreshProgress!!.visibility = View.GONE
                refresh!!.visibility = View.VISIBLE
                refreshProgress!!.stopAnimation()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bluetooth_activity)
        currentPage = ConfigUtil.BT_CONNECTION_PAGE
        initBlueTooth()
        initView()
//        initFloatingButtons(this)

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun initView(){
        switchButton = findViewById(R.id.switchButton)
        deviceName = findViewById(R.id.deviceName)
        switchButton!!.isChecked = btManager!!.isBluetoothEnabled
        isChecked = switchButton!!.isChecked
        switchButton!!.setOnClickListener{
            if (isChecked) {
                btManager!!.turnOff()
                switchButton!!.isChecked = false
                isChecked = false
            }
            else{
                btManager!!.turnOn()
            }
        }
        deviceName!!.text = btManager!!.deviceInfo
        refresh = findViewById(R.id.refresh)
        refresh!!.setOnClickListener{
            getAvailableDevices()
        }
        refreshProgress = findViewById(R.id.refreshProgress)

        pairedDeviceView= findViewById(R.id.pairedDeviceList)
        updatePairedDevices()

        availableDeviceView = findViewById(R.id.availableDeviceList)
        getAvailableDevices()
    }

    private fun initBlueTooth(){
        btManager!!.setContext(this)
        btConnector!!.setHandler(handler)
        // Register for broadcasts when a device is discovered.
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)
        val filter1 = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        registerReceiver(receiver, filter1)
        if (btManager!!.isBluetoothEnabled)
            pairedDevices = getPairedBTDeviceList(setToList(btManager!!.getPairedDevices()))
    }

    private fun getAvailableDevices(){
        refreshProgress!!.visibility = View.VISIBLE
        refresh!!.visibility = View.GONE
        refreshProgress!!.startAnimation()
        updateAvailableDevices()
    }

    private fun getPairedBTDeviceList(list: MutableList<BluetoothDevice>): MutableList<BtDevice>{
        val result = getBTDeviceList(list)
        for (device in result){
            device.paired = true
        }
        return result
    }

    @SuppressLint("MissingPermission")
    private fun getBTDeviceList(list: MutableList<BluetoothDevice>): MutableList<BtDevice>{
        val result = mutableListOf<BtDevice>()
        var btDevice: BtDevice? = null
        for (device in list){
            btDevice = BtDevice(device.name, device.address)
            result.add(btDevice)
        }
        return result
    }

    private fun setToList(set: MutableSet<BluetoothDevice>): MutableList<BluetoothDevice>{
        val list = mutableListOf<BluetoothDevice>()
        for (device in set){
            list.add(device)
        }
        return list
    }


    class BtDeviceHolder(val itemView: View, val btConnector: BtConnector, val btManager: BtManager): RecyclerView.ViewHolder(itemView){
        var wholeView: RelativeLayout? = null
        var deviceTypeImage: ImageView? = null
        var deviceName: TextView? = null
        var connectionPoint: ImageView? = null
        var connectionStatus: TextView? = null
        var string: TextView? = null
        var device: BtDevice? = null

        init{
            deviceTypeImage = itemView.findViewById(R.id.deviceType)
            deviceName = itemView.findViewById(R.id.deviceName)
            connectionPoint = itemView.findViewById(R.id.connectionPoint)
            connectionStatus = itemView.findViewById(R.id.connectionStatus)
            wholeView = itemView.findViewById(R.id.deviceItem)
            string = itemView.findViewById(R.id.statusString)
        }

        fun pairing(){
            string!!.text = "Pairing..."
            btConnector.connect(btManager.getDeviceFromAddress(device!!.address))
        }

        fun connecting(){
            string!!.text = "Connecting..."
            btConnector.connect(btManager.getDeviceFromAddress(device!!.address))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode){
            BtManager.BT_TURN_ON -> {
                if (resultCode == RESULT_OK) {
                    isChecked = true
                    switchButton!!.isChecked = true
                    updatePairedDevices()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun updatePairedDevices(){
        pairedDevices = getPairedBTDeviceList(setToList(btManager!!.getPairedDevices()))
        pairedDeviceView!!.adapter = BtDeviceAdapter(pairedDevices, btConnector!!, btManager!!)
        pairedDeviceView!!.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }

    private fun updateAvailableDevices(){
        availableDevices.clear()
        btManager!!.showNearbyDevices()
    }

    class BtDeviceAdapter(var deviceList: List<BtDevice> = emptyList<BtDevice>(), val btConnector: BtConnector, val btManager: BtManager): RecyclerView.Adapter<BtPage.BtDeviceHolder>(){

        companion object{
            var connectedDevice: BtDeviceHolder? = null
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BtDeviceHolder {
            return BtPage.BtDeviceHolder(LayoutInflater.from(parent.context).inflate(R.layout.bluetooth_device_item, parent, false), btConnector, btManager);
        }

        @SuppressLint("ResourceAsColor")
        override fun onBindViewHolder(holder: BtDeviceHolder, position: Int) {
            val device = deviceList[position]
            holder.device = device
            holder.deviceName!!.text = device.name
            if (!device.paired){
                holder.connectionPoint!!.visibility = View.GONE
                holder.connectionStatus!!.visibility = View.GONE
            }
            else if (!device.connected){
                holder.connectionPoint!!.setBackgroundResource(R.drawable.red_point)
                holder.connectionStatus!!.setTextColor(Color.parseColor("#C60748"))
                holder.connectionStatus!!.text = "Disconnected"
            }
            holder.wholeView!!.setOnClickListener{
                if (device.paired){
                    connectedDevice = holder
                    holder.connecting()
                }
                else{
                    holder.pairing()
                }
                holder.string!!.visibility = View.VISIBLE
                holder.deviceName!!.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
        }

        override fun getItemCount(): Int {
           return deviceList.size
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions!!, grantResults)
        if (requestCode == ConfigUtil.FINE_LOCATION) {
            if (grantResults.size > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                    this,
                    "FINE_LOCATION Permission Granted",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "FINE_LOCATION Permission Denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else if (requestCode == ConfigUtil.BACKGROUND_LOCATION) {
            if (grantResults.size > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                    this,
                    "BACKGROUND_LOCATION Permission Granted",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "BACKGROUND_LOCATION Permission Denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }



}