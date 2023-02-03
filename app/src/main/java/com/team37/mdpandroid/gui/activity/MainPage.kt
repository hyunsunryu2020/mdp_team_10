package com.team37.mdpandroid.gui.activity

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.getbase.floatingactionbutton.FloatingActionsMenu
import com.team37.mdpandroid.R
import com.team37.mdpandroid.bt.BtConnector
import com.team37.mdpandroid.gui.data.GridElement
import com.team37.mdpandroid.gui.manager.GridAdapter
import com.team37.mdpandroid.gui.manager.ObstacleAdapter
import com.team37.mdpandroid.gui.util.ConfigUtil
import com.team37.mdpandroid.gui.util.JSONBuilder
import com.team37.mdpandroid.gui.util.ObstacleStatusUtil
import org.json.JSONObject


class MainPage : BasicActivity() {

    private var gridView: GridView? = null
    private var adapter: GridAdapter? = null
    private var obstacleRecyclerView: RecyclerView? = null
    private var obstacleAdapter: ObstacleAdapter? = null
    private var listButton: ImageView? = null
    private var obstacleListView: RelativeLayout? = null
    private var drawerLayout: DrawerLayout? = null
    private var statusText: TextView? = null
    private var robotLayout: RelativeLayout? = null
    private var robotIcon: ImageView? = null
    private var sendArena: Button? = null
    private var start: Button? = null
    private var addObstacle: Button? = null
    private var clearObstacle: Button? = null
    private var blueToothConnection: Button? = null

    private var finalPosition = ""
    private var flButton: ImageView? = null
    private var fcButton: ImageView? = null
    private var frButton: ImageView? = null
    private var blButton: ImageView? = null
    private var bcButton: ImageView? = null
    private var brButton: ImageView? = null
    private var testArenaButton: Button? = null
    private var sendMessage: Button? = null

    private var started = false

    private val obstaclesQueue = mutableListOf<GridAdapter.ViewHolder>()


    companion object {
        private val TAG = MainPage.javaClass.simpleName
        private var algoPath = mutableListOf<String>()
    }

    private val handler = Handler(
        Looper.myLooper()!!
    ) { msg ->
        when (msg.what) {
            ConfigUtil.MESSAGE_STATE_CHANGED -> when (msg.arg1) {
                BtConnector.STATE_NONE, BtConnector.STATE_LISTEN -> {
//                    textBtConnState.setText("Not connected")
//                    if (mBtConnector.wasConnected() && !hasReconnQueued) {
//                        hasReconnQueued = true
//                        reconnectionHandler.postDelayed(reconnectionRunnable, 5000)
//                    }
                }
//                BtConnector.STATE_CONNECTING -> textBtConnState.setText("Connecting")
//                BtConnector.STATE_CONNECTED -> textBtConnState.setText("Connected to " + mBtConnector.getDeviceName())
            }
            ConfigUtil.MESSAGE_READ -> {
                val string = msg.data.getString(ConfigUtil.MESSAGE_BODY)!!
                Log.e("JSONString", string)
                val msgData = JSONObject(string)
                val header = msgData["header"]
                when (header) {
//                    "TARGET" -> {
//                        val imageId = msgData[2]
//                        val obstacleId = msgData[1]
//                        val holder = GridAdapter.obstacles[Integer.parseInt(obstacleId)]
//                        holder!!.image!!.text =
//                            ObstacleStatusUtil.obstacles[Integer.parseInt(imageId)]
//                        holder!!.image!!.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
//                        holder!!.image!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20F)
//                    }
//                    "MSG" -> {
//                        when (msgData[1]) {
//                            "READY" -> {
//                                statusText!!.text = "Ready"
//                                statusText!!.setTextColor(Color.GREEN)
//                            }
//                            "FINDING" -> {
//                                val oId = msgData[2]
//                                statusText!!.text = "Moving to obstacle " + oId
//                                statusText!!.setTextColor(Color.BLUE)
//                            }
//                            "IDENTIFYING" -> {
//                                val oId = msgData[2]
//                                statusText!!.text = "Scanning obstacle " + oId
//                                statusText!!.setTextColor(Color.MAGENTA)
//                            }
//                        }
//                    }
//                    "ROBOT" -> {
//                        val x: Int = ((Integer.parseInt(msgData[1])-2) * 33 + 68).toInt()
//                        var y: Int = (655 - (Integer.parseInt(msgData[2])-2) * 33).toInt()
//                        val lp = robotLayout!!.getLayoutParams() as RelativeLayout.LayoutParams
//                        lp.leftMargin = x
//                        lp.topMargin = y
//                        robotLayout!!.layoutParams = lp
//                        when (msgData[3]) {
//                            "N" -> robotIcon!!.setImageResource(R.drawable.robot_up)
//                            "E" -> robotIcon!!.setImageResource(R.drawable.robot_right)
//                            "S" -> robotIcon!!.setImageResource(R.drawable.robot_down)
//                            "W" -> robotIcon!!.setImageResource(R.drawable.robot_left)
//                            else -> {}
//                        }
//                    }
                    "DONE" -> {
                        if (algoPath.size!=0){
                            Log.e("Path", algoPath[0])
                            val info = algoPath[0].split(",")
                            val x: Int = ((Integer.parseInt(info[0])-1) * 33 + 68).toInt()
                            var y: Int = (655 - (Integer.parseInt(info[1])-1) * 33).toInt()
                            val lp = robotLayout!!.getLayoutParams() as RelativeLayout.LayoutParams
                            lp.leftMargin = x
                            lp.topMargin = y
                            robotLayout!!.layoutParams = lp
                            when (info[2]) {
                                "N" -> robotIcon!!.setImageResource(R.drawable.robot_up)
                                "E" -> robotIcon!!.setImageResource(R.drawable.robot_right)
                                "S" -> robotIcon!!.setImageResource(R.drawable.robot_down)
                                "W" -> robotIcon!!.setImageResource(R.drawable.robot_left)
                                else -> {}
                            }
                            algoPath.removeAt(0)
                            if (algoPath.size!=0){
                                statusText!!.text = "Moving to position (" + (Integer.parseInt(algoPath[0].split(",")[0])+1).toString() + "," + (Integer.parseInt(algoPath[0].split(",")[1])+1).toString() + ")"
                                statusText!!.setTextColor(Color.BLUE)
                            }
                            else{

                                statusText!!.text = "Moving done"
                                statusText!!.setTextColor(Color.BLUE)
                            }
                        }
                    }
                    "POS" -> {
                        algoPath.clear()
                        val obstaclePaths = (msgData["body"] as String).split("\n")
                        var tempPath = ""
                        for (paths: String in obstaclePaths){
                            if (paths.equals(""))
                                continue
                            val route = paths.split("],[")
                            tempPath = ""
                            for (path: String in route){
                                tempPath = path
                                var position = tempPath.indexOf("[")
                                if (position!=-1){
                                    tempPath = tempPath.substring(1, tempPath.length)
                                }
                                position = tempPath.indexOf("]")
                                if (position!=-1){
                                    tempPath = tempPath.substring(0, tempPath.length-1)
                                }
                                algoPath.add(tempPath)
                            }
                            val position = tempPath.split(",")
                            findObstacleId(Integer.parseInt(position[0]), Integer.parseInt(position[1]), position[2])
                        }
                        finalPosition = tempPath
                        algoPath.removeAt(0)
                        combineStraightLine()
                        statusText!!.text = "Moving to position (" + (Integer.parseInt(algoPath[0].split(",")[0])+1).toString() + "," + (Integer.parseInt(algoPath[0].split(",")[1])+1).toString() + ")"
                        statusText!!.setTextColor(Color.BLUE)
                    }
                    "IMAGE_ID" -> {
                        val imageId = msgData["body"] as String
                        val holder = obstaclesQueue[0]
                        if (!imageId.contains("No Detection")){
                            Log.e("ImageDetected", "Image detected")
                            holder!!.image!!.text = imageId
                            holder!!.image!!.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                            holder!!.image!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20F)
                        }
                        obstaclesQueue.removeAt(0)
                        if (obstaclesQueue.size==0){
                            Log.e("Path", finalPosition)
                            val info = finalPosition.split(",")
                            val x: Int = ((Integer.parseInt(info[0])-1) * 33 + 68).toInt()
                            var y: Int = (655 - (Integer.parseInt(info[1])-1) * 33).toInt()
                            val lp = robotLayout!!.getLayoutParams() as RelativeLayout.LayoutParams
                            lp.leftMargin = x
                            lp.topMargin = y
                            robotLayout!!.layoutParams = lp
                            when (info[2]) {
                                "N" -> robotIcon!!.setImageResource(R.drawable.robot_up)
                                "E" -> robotIcon!!.setImageResource(R.drawable.robot_right)
                                "S" -> robotIcon!!.setImageResource(R.drawable.robot_down)
                                "W" -> robotIcon!!.setImageResource(R.drawable.robot_left)
                                else -> {}
                            }
                            statusText!!.text = "Complete!"
                            statusText!!.setTextColor(Color.GREEN)
                            start!!.text = "START"
                            started = false
                        }
                    }
                }
            }
            ConfigUtil.MESSAGE_WRITE -> {}
//            ConfigUtil.MESSAGE_TOAST -> showToast(msg.data.getString(MainActivity.TOAST))
        }
        false
    }

    private fun loadObstacles() {
        for (holder: GridAdapter.ViewHolder in GridAdapter.obstacles.values) {
            adapter!!.addObstacleWithDirection(holder, holder.direction!!)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        setContentView(R.layout.main_page_activity)
        currentPage = ConfigUtil.MAIN_PAGE
        initView()
//        initFloatingButtons(this)
        btConnector.setHandler(handler)
        val lp = robotLayout!!.getLayoutParams() as RelativeLayout.LayoutParams
        lp.leftMargin = 68
        lp.topMargin = 655
        robotLayout!!.layoutParams = lp
    }

    override fun onResume() {
        super.onResume()
        loadObstacles()
        statusText!!.text = status
    }

    private fun initView(): Unit {
        gridView = findViewById(R.id.gridView)
//        listButton = findViewById(R.id.listButton)
        obstacleListView = findViewById(R.id.obstacleListView)
        drawerLayout = findViewById(R.id.mainPageDrawerLayout)
        statusText = findViewById(R.id.status)
        robotLayout = findViewById(R.id.robotLayout)
        robotIcon = findViewById(R.id.robotIcon)
        sendArena = findViewById(R.id.arenaButton)
        start = findViewById(R.id.startButton)
        addObstacle = findViewById(R.id.addArena)
        clearObstacle = findViewById(R.id.clearButton)
        blueToothConnection = findViewById(R.id.blueToothConnection)
        testArenaButton = findViewById(R.id.testArena)
        sendMessage = findViewById((R.id.sendAndReceive))
        val gridList = mutableListOf<GridElement>()
        for (i in 1..20) {
            for (j in 1..20) {
                gridList.add(GridElement(i, j))
            }
        }
        adapter = GridAdapter(this, gridList)
        gridView?.adapter = adapter
        initGridView()
        initRecyclerView()
//        listButton!!.setOnClickListener {
//            drawerLayout!!.openDrawer(Gravity.RIGHT)
//        }

        flButton = findViewById(R.id.flButton)
        frButton = findViewById(R.id.frButton)
        fcButton = findViewById(R.id.fcButton)
        blButton = findViewById(R.id.blButton)
        bcButton = findViewById(R.id.bcButton)
        brButton = findViewById(R.id.brButton)

        blueToothConnection!!.setOnClickListener {
            if (!currentPage.equals(ConfigUtil.BT_CONNECTION_PAGE)){
                val intent = Intent(applicationContext, BtPage::class.java)
//                finish()
                startActivity(intent)
            }
        }

        sendMessage!!.setOnClickListener {
            if (!currentPage.equals(ConfigUtil.BT_MESSAGE_PAGE)) {
                val intent = Intent(applicationContext, MessagePage::class.java)
                startActivity(intent)
            }
        }

        flButton!!.setOnClickListener {
            if (checkBtStatus()) {
                btConnector!!.write(
                    JSONBuilder.refreshJson()
                        .addParameter("to", "STM")
                        .addParameter("header", "TEST_MOVEMENT")
                        .addParameter("body", "[FL,10000]")
                        .getJsonObject()
                        .toString()
                )
            }
        }

        fcButton!!.setOnClickListener {
            if (checkBtStatus()) {
                btConnector!!.write(
                    JSONBuilder.refreshJson()
                        .addParameter("to", "STM")
                        .addParameter("header", "TEST_MOVEMENT")
                        .addParameter("body", "[FC,10000]")
                        .getJsonObject()
                        .toString()
                )
            }
        }

        frButton!!.setOnClickListener {
            if (checkBtStatus()) {
                btConnector!!.write(
                    JSONBuilder.refreshJson()
                        .addParameter("to", "STM")
                        .addParameter("header", "TEST_MOVEMENT")
                        .addParameter("body", "[FR,10000]")
                        .getJsonObject()
                        .toString()
                )
            }
        }

        blButton!!.setOnClickListener {
            if (checkBtStatus()) {
                btConnector!!.write(
                    JSONBuilder.refreshJson()
                        .addParameter("to", "STM")
                        .addParameter("header", "TEST_MOVEMENT")
                        .addParameter("body", "[BL,10000]")
                        .getJsonObject()
                        .toString()
                )
            }
        }

        bcButton!!.setOnClickListener {
            if (checkBtStatus()) {
                btConnector!!.write(
                    JSONBuilder.refreshJson()
                        .addParameter("to", "STM")
                        .addParameter("header", "TEST_MOVEMENT")
                        .addParameter("body", "[BC,10000]")
                        .getJsonObject()
                        .toString()
                )
            }
        }

        brButton!!.setOnClickListener {
            if (checkBtStatus()) {
                btConnector!!.write(
                    JSONBuilder.refreshJson()
                        .addParameter("to", "STM")
                        .addParameter("header", "TEST_MOVEMENT")
                        .addParameter("body", "[BR,10000]")
                        .getJsonObject()
                        .toString()
                )
            }
        }

//        val steeringButton = findViewById<Button>(R.id.steeringButton)
//        steeringButton.setOnClickListener {
//            val intent = Intent(this, SteeringPage::class.java)
//            startActivity(intent)
//        }


        drawerLayout!!.setOnDragListener { view, event ->
            Log.i("Drag", event.action.toString())
            when (event.action) {
                DragEvent.ACTION_DRAG_ENDED -> {}
                DragEvent.ACTION_DROP -> {
                    if (view != gridView) {
                        var holder = event.localState as GridAdapter.ViewHolder
//                        SendMessageUtil.deleteObstacle(holder.number)
                        GridAdapter.obstacles.remove(holder.number)
                        GridAdapter.clearObstacle(holder)
                    }
                }
                else -> {
                }
            }
            true
        }

        addObstacle!!.setOnClickListener {
            val factory = LayoutInflater.from(this)
            val view: RelativeLayout =
                factory.inflate(R.layout.input_dialog, null) as RelativeLayout
            val xInput = view.findViewById<EditText>(R.id.xInput)
            setEditTextRange(xInput, 0, 20)
            val yInput = view.findViewById<EditText>(R.id.yInput)
            setEditTextRange(yInput, 0, 20)
            val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
                .setTitle("Please input obstacle location")
                .setView(view)
                .setPositiveButton("Continue", DialogInterface.OnClickListener { dialog, i ->
                    val index =
                        Integer.parseInt(xInput.text.toString()) + Integer.parseInt(yInput.text.toString()) * 20
                    adapter!!.addObstacle(adapter!!.holders[index]!!)
                })
                .setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { dialog, i ->
                        dialog.dismiss()
                    })
            dialog.show()

        }

        sendArena!!.setOnClickListener {
            if (checkBtStatus()) {
                val list = mutableListOf<String>()
                var string: String = ""
                for (item: GridAdapter.ViewHolder in GridAdapter.obstacles.values) {
                    string =
                        "[" + item.number + "," + item.x + "," + item.y + "," + ObstacleStatusUtil.adjustDirection(
                            item.direction!!
                        ) + "]"
                    list.add(string)
                }
                string = ""
                for (item: String in list) {
                    string += (item + ",")
                }
                string = string.subSequence(0, string.length - 1) as String
                val message = JSONBuilder.refreshJson()
                    .addParameter("to", "PC")
                    .addParameter("header", "OBS")
                    .addParameter("body", string)
                    .getJsonObject()
                btConnector.write(message.toString())
            }
        }

        start!!.setOnClickListener {
            if (!started) {
                if (checkBtStatus()) {
                    btConnector!!.write(
                        JSONBuilder.refreshJson()
                            .addParameter("to", "RPI")
                            .addParameter("header", "START")
                            .addParameter("body", "START")
                            .getJsonObject()
                            .toString()
                    )
                    start!!.text = "STOP"
                    started = true
                }
            } else {
                start!!.text = "START"
                started = false
            }

        }

        clearObstacle!!.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure to clear all obstacles?")
                .setPositiveButton("Yes") { _, _ ->
                    var holder: GridAdapter.ViewHolder? = null
                    for (index: Int in GridAdapter.obstacles.keys) {
                        holder = GridAdapter.obstacles[index]
                        GridAdapter!!.clearObstacle(holder!!)
                    }
                    GridAdapter.obstacles = mutableMapOf<Int, GridAdapter.ViewHolder>()
                }
                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            val dialog = builder.create()
            dialog.show()
        }

        testArenaButton!!.setOnClickListener {
            adapter!!.addObstacleWithDirection(adapter!!.holders[382]!!, ObstacleStatusUtil.down)
            adapter!!.addObstacleWithDirection(adapter!!.holders[267]!!, ObstacleStatusUtil.up)
            adapter!!.addObstacleWithDirection(adapter!!.holders[171]!!, ObstacleStatusUtil.right)
            adapter!!.addObstacleWithDirection(adapter!!.holders[356]!!, ObstacleStatusUtil.left)
            adapter!!.addObstacleWithDirection(adapter!!.holders[220]!!, ObstacleStatusUtil.left)
            adapter!!.addObstacleWithDirection(adapter!!.holders[74]!!, ObstacleStatusUtil.right)
        }
    }

    private fun setEditTextRange(editText: EditText, min: Int, max: Int) {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!s.toString().isEmpty()) {
                    val value = Integer.valueOf(s.toString())
                    if (value < min) {
                        editText.setText(min.toString() + "")
                    } else if (value > max) {
                        editText.setText(max.toString() + "")
                    }
                } else {
                    editText.setText(min.toString() + "")
                }
            }

            override fun afterTextChanged(s: Editable) {}
        }
        editText.addTextChangedListener(watcher)
    }

    private fun initRecyclerView(): Unit {
        obstacleAdapter = ObstacleAdapter(this)
        obstacleRecyclerView = findViewById(R.id.obstacleRecyclerView)
        val layoutManager = LinearLayoutManager(this)
        obstacleRecyclerView!!.layoutManager = layoutManager
        obstacleRecyclerView!!.adapter = obstacleAdapter
    }

    private fun initGridView(): Unit {
//        val mainPageMenu = findViewById<FloatingActionsMenu>(R.id.mainPageMenu)
//        val test = findViewById<TextView>(R.id.test)
//        test.setOnLongClickListener{
//            val builder = View.DragShadowBuilder(it)
//            it.startDragAndDrop(null, builder, it, 0)
//            true
//        }
        val gridView = findViewById<GridView>(R.id.gridView)

    }

    private fun findItemInGridView(x: Int, y: Int) {
        var item: GridElement? = null
        for (i in 1..14) {
            for (j in 1..14) {
//                item = gridView.get()
            }
        }
    }

    private fun findObstacleId(x: Int, y: Int, direction: String){
        var targetPosition: List<Int>? = null
        targetPosition = when(direction){
            "N" -> listOf(x, y+5)
            "E" -> listOf(x+5, y)
            "S" -> listOf(x, y-5)
            "W" -> listOf(x-5, y)
            else -> listOf<Int>()
        }
        Log.e("???", targetPosition.toString())
        for (holder: GridAdapter.ViewHolder in GridAdapter.obstacles.values){
            if (holder.x == targetPosition[0]+1 && holder.y == targetPosition[1]+1){
                Log.e("Obstacles", targetPosition[0].toString() + ", " + targetPosition[1])
                obstaclesQueue.add(holder)
                break
            }
        }
    }

    private fun combineStraightLine(){
        var j = 0;
        var isIncrease = true
        var isFollowingIncrease = true
        val size = algoPath.size
        var isJustTurn = false
        for (i in 0..size-2){
            Log.e("PATH", algoPath[j])
            isFollowingIncrease = (Integer.parseInt(algoPath[j+1].split(",")[1])-Integer.parseInt(algoPath[j].split(",")[1])) + (Integer.parseInt(algoPath[j+1].split(",")[0])-Integer.parseInt(algoPath[j].split(",")[0]))>=0
            if (isJustTurn){
                isIncrease = isFollowingIncrease
                isJustTurn = false
                j++
                continue
            }
            if (algoPath[j].split(",")[2].equals(algoPath[j+1].split(",")[2]) && isFollowingIncrease == isIncrease){
                algoPath.removeAt(j)
            }
            else{
                if (!algoPath[j].split(",")[2].equals(algoPath[j+1].split(",")[2])) {
                    isJustTurn = true
                }
                j++
            }
            isIncrease = isFollowingIncrease
        }
    }
}


