package com.team37.mdpandroid.gui.activity

import android.Manifest
import android.animation.ObjectAnimator
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.team37.mdpandroid.R
import com.team37.mdpandroid.gui.data.GridElement
import com.team37.mdpandroid.gui.manager.GridAdapter
import com.team37.mdpandroid.gui.manager.ObstacleAdapter
import com.team37.mdpandroid.gui.util.ConfigUtil
import com.team37.mdpandroid.gui.util.JSONBuilder
import com.team37.mdpandroid.gui.util.ObstacleStatusUtil
import java.util.*


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
    private var x_Input: EditText?=null
    private var y_Input: EditText?=null

    private var finalPosition = ""
    private var flButton: ImageView? = null
    private var fcButton: ImageView? = null
    private var frButton: ImageView? = null
    private var blButton: ImageView? = null
    private var bcButton: ImageView? = null
    private var brButton: ImageView? = null
//    private var testArenaButton: Button? = null
    private var sendMessage: Button? = null
    private var cancelButton: Button? = null
    private var continueButton: Button? = null
    private var cancelClearButton: Button? = null
    private var yesButton: Button? = null
    private var speechButton: Button? = null
    private var speechRecognizer: SpeechRecognizer? = null

    private var started = false


    private val obstaclesQueue = mutableListOf<GridAdapter.ViewHolder>()


    companion object {
        private val TAG = MainPage.javaClass.simpleName
        private var algoPath = mutableListOf<String>()
        const val RecordAudioRequestCode = 1
    }

    private val handler = Handler(
        Looper.myLooper()!!
    ) { msg ->
        when (msg.what) {
            ConfigUtil.MESSAGE_STATE_CHANGED -> when (msg.arg1) {
//                BtConnector.STATE_NONE, BtConnector.STATE_LISTEN -> {
//                    textBtConnState.setText("Not connected")
//                    if (mBtConnector.wasConnected() && !hasReconnQueued) {
//                        hasReconnQueued = true
//                        reconnectionHandler.postDelayed(reconnectionRunnable, 5000)
//                    }
//                }
//                BtConnector.STATE_CONNECTING -> textBtConnState.setText("Connecting")
//                BtConnector.STATE_CONNECTED -> textBtConnState.setText("Connected to " + mBtConnector.getDeviceName())
            }
            ConfigUtil.MESSAGE_READ -> {
                val string = msg.data.getString(ConfigUtil.MESSAGE_BODY)!!
                msg.data.getString(ConfigUtil.MESSAGE_BODY)?.let { Log.e("hello", it)}
                //val msgData = JSONObject(string)
                val arrOfmsg = string.split(",").toTypedArray()
                val header = arrOfmsg[0]
                Log.e("header",arrOfmsg[0])
                println(1)
                when (header) {
                    "TARGET" -> {
                        val imageId = arrOfmsg[2]
                        val obstacleId = arrOfmsg[1]
                        val holder = GridAdapter.obstacles[Integer.parseInt(obstacleId)]
                        holder!!.number = Integer.parseInt(imageId)
                        holder!!.image!!.text = holder!!.number.toString()
                        holder!!.image!!.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                        holder!!.image!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20F)
                    }
                    "ROBOT" -> {
                        val x: Int = ((Integer.parseInt(arrOfmsg[1])-2) * 33 + 68).toInt()
                        var y: Int = (655 - (Integer.parseInt(arrOfmsg[2])-2) * 33).toInt()
                        val lp = robotLayout!!.getLayoutParams() as RelativeLayout.LayoutParams
                        lp.leftMargin = x
                        lp.topMargin = y
                        robotLayout!!.layoutParams = lp
                        when (arrOfmsg[3]) {
                            "N" -> robotIcon!!.setImageResource(R.drawable.robot_up)
                            "E" -> robotIcon!!.setImageResource(R.drawable.robot_right)
                            "S" -> robotIcon!!.setImageResource(R.drawable.robot_down)
                            "W" -> robotIcon!!.setImageResource(R.drawable.robot_left)
                            else -> {}
                        }
                    }
                    "DONE" -> {
                        if (algoPath.size != 0) {
                            Log.e("Path", algoPath[0])
                            val info = algoPath[0].split(",")
                            val x: Int = ((Integer.parseInt(info[0]) - 1) * 33 + 68).toInt()
                            var y: Int = (655 - (Integer.parseInt(info[1]) - 1) * 33).toInt()
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
                            if (algoPath.size != 0) {
                                statusText!!.text =
                                    "Moving to position (" + (Integer.parseInt(algoPath[0].split(",")[0]) + 1).toString() + "," + (Integer.parseInt(
                                        algoPath[0].split(",")[1]
                                    ) + 1).toString() + ")"
                                statusText!!.setTextColor(Color.BLUE)
                            } else {

                                statusText!!.text = "Moving done"
                                statusText!!.setTextColor(Color.BLUE)
                            }
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
        initBlueTooth()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED){
            checkPermissions()
        }
        speechButton = findViewById(R.id.speechButton)
        statusText = findViewById(R.id.status)
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        speechRecognizer!!.setRecognitionListener(object : RecognitionListener{
            override fun onReadyForSpeech(p0: Bundle?) {
            }

            override fun onBeginningOfSpeech() {
                statusText!!.setText("")
                statusText!!.setHint("Listening...")
            }

            override fun onRmsChanged(p0: Float) {}

            override fun onBufferReceived(p0: ByteArray?) {}

            override fun onEndOfSpeech() {}

            override fun onError(p0: Int) {}

            override fun onResults(bundle: Bundle?) {
                speechButton!!.setText("Speech")
                val data = bundle!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (data!![0]=="move forward"){
                    moveForward("N")
                }
                if (data!![0]=="move forward left"){
                    moveForward("N")
                    rotate(0f,90f)
                    moveForward("E")
                }
                statusText!!.setText(data!![0])

            }

            override fun onPartialResults(p0: Bundle?) {
            }

            override fun onEvent(p0: Int, p1: Bundle?) {
            }

        }

        )
        speechButton!!.setOnTouchListener{view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP){
                speechRecognizer!!.stopListening()
                speechButton!!.setText("Speech")
            }

            if (motionEvent.action == MotionEvent.ACTION_DOWN){
                speechButton!!.setText("Stop")
                speechRecognizer!!.startListening(
                    speechRecognizerIntent
                )
            }

        false}

    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer!!.destroy()
    }

    private fun checkPermissions() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            ActivityCompat.requestPermissions(
                this,arrayOf(Manifest.permission.RECORD_AUDIO),
                RecordAudioRequestCode
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RecordAudioRequestCode && grantResults.isNotEmpty()){
            Toast.makeText(this,"Permission Granted", Toast.LENGTH_SHORT).show()
        }
    }

    fun moveForward(direction:String) {
        if (direction == "N") {
            val objectAnimator1 = ObjectAnimator.ofFloat(robotLayout, "translationY", robotLayout!!.translationY,robotLayout!!.translationY-35f)
            objectAnimator1.duration = 1000
            objectAnimator1.start()
        }
        if (direction =="E"){
            val objectAnimator2=  ObjectAnimator.ofFloat(robotLayout, "translationX", robotLayout!!.translationX,robotLayout!!.translationX+35f)
            objectAnimator2.duration = 1000
            objectAnimator2.start()
        }
        if (direction=="W"){
            val objectAnimator3=  ObjectAnimator.ofFloat(robotLayout, "translationX",  robotLayout!!.translationX,robotLayout!!.translationX-35f)
            objectAnimator3.duration = 1000
            objectAnimator3.start()
        }
        if (direction=="S"){
            val objectAnimator4 = ObjectAnimator.ofFloat(robotLayout, "translationY", robotLayout!!.translationY,robotLayout!!.translationY+35f)
            objectAnimator4.duration = 1000
            objectAnimator4.start()
        }

    }

    fun moveBackward(direction:String){
        if (direction=="N"){
            val objectAnimator4 = ObjectAnimator.ofFloat(robotLayout, "translationY", robotLayout!!.translationY,robotLayout!!.translationY+35f)
            objectAnimator4.duration = 1000
            objectAnimator4.start()
        }
        if (direction=="E"){
            val objectAnimator2=  ObjectAnimator.ofFloat(robotLayout, "translationX", robotLayout!!.translationX,robotLayout!!.translationX-35f)
            objectAnimator2.duration = 1000
            objectAnimator2.start()
        }
        if (direction=="W"){
            val objectAnimator3=  ObjectAnimator.ofFloat(robotLayout, "translationX",  robotLayout!!.translationX,robotLayout!!.translationX+35f)
            objectAnimator3.duration = 1000
            objectAnimator3.start()
        }
        if (direction=="S"){
            val objectAnimator4 = ObjectAnimator.ofFloat(robotLayout, "translationY", robotLayout!!.translationY,robotLayout!!.translationY-35f)
            objectAnimator4.duration = 1000
            objectAnimator4.start()
        }

    }

    fun rotate(degree:Float,angle: Float){
        val objectAnimator = ObjectAnimator.ofFloat(robotIcon,"rotation",degree,degree+angle)
        objectAnimator.duration=1000
        objectAnimator.start()
    }



    override fun onResume() {
        super.onResume()
        loadObstacles()
        statusText!!.text = status
        initBlueTooth()
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
        /*testArenaButton = findViewById(R.id.testArena)*/
        sendMessage = findViewById((R.id.sendAndReceive))
        val gridList = mutableListOf<GridElement>()
        for (i in 1..20) {
            for (j in 1..20) {
                gridList.add(GridElement(i, j))
            }
        }
        adapter = GridAdapter(this, gridList)
        gridView?.adapter = adapter
//        initGridView()
        initRecyclerView()

        flButton = findViewById(R.id.flButton)
        frButton = findViewById(R.id.frButton)
        fcButton = findViewById(R.id.fcButton)
        blButton = findViewById(R.id.blButton)
        bcButton = findViewById(R.id.bcButton)
        brButton = findViewById(R.id.brButton)
        var x=1f
        var y=1f
        var direction="N"
        var angle=0f



        blueToothConnection!!.setOnClickListener {
            if (!currentPage.equals(ConfigUtil.BT_CONNECTION_PAGE)) {
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
                statusText!!.setText("Going Forward + Left")
                btConnector!!.write("f")
                Thread.sleep(1_000)
                btConnector!!.write("tl")
                Thread.sleep(1_000)
                btConnector!!.write("f")
                if (direction =="N"){
                    btConnector!!.write("f")
                    moveForward(direction)
                    rotate(angle,-90f)
                    btConnector!!.write("tl")
                    angle -= 90f
                    direction="W"
                    moveForward(direction)
                    btConnector!!.write("f")
                    }
                else if (direction=="W"){
                    moveForward(direction)
                    rotate(angle,-90f)
                    angle -=90f
                    direction="S"
                    moveForward(direction)
                }
                else if (direction=="E"){
                    moveForward(direction)
                    rotate(angle,-90f)
                    angle -=90f
                    direction="N"
                    moveForward(direction)
                }
            }
        }

        fcButton!!.setOnClickListener {
            if (checkBtStatus()) {
                btConnector!!.write(
                    "f"
                )
                statusText!!.setText("Going Forward")
                moveForward(direction)


                }


            }


        frButton!!.setOnClickListener {
            if (checkBtStatus()) {
                statusText!!.setText("Going Forward + Right")
                btConnector!!.write("f")
                Thread.sleep(1_000)
                btConnector!!.write("tr")
                Thread.sleep(1_000)
                btConnector!!.write("f")
                if (direction =="N"){
                    moveForward(direction)
                    rotate(angle,90f)
                    angle += 90f
                    direction="E"
                    moveForward(direction)
                    }
                else if (direction=="W"){
                    moveForward(direction)
                    rotate(angle,90f)
                    angle +=90f
                    direction="N"
                    moveForward(direction)
                }
                else if (direction=="E"){
                    moveForward(direction)
                    rotate(angle,90f)
                    angle +=90f
                    direction="S"
                    moveForward(direction)
                }



            }}

        blButton!!.setOnClickListener {
            if (checkBtStatus()) {
                statusText!!.setText("Going Backward + Left")
                btConnector!!.write("bl")
            }
        }

        bcButton!!.setOnClickListener {
            if (checkBtStatus()) {
                statusText!!.setText("Going Backward")
                btConnector!!.write("b")
                moveBackward(direction)
            }
        }

        brButton!!.setOnClickListener {
            if (checkBtStatus()) {
                statusText!!.setText("Going Backward + Right")
                btConnector!!.write("br"
                )
            }
        }



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
            showCustomDialog()

        }

        sendArena!!.setOnClickListener {
            if (checkBtStatus()) {
                val list = mutableListOf<String>()
                var string: String = ""
                for (item: GridAdapter.ViewHolder in GridAdapter.obstacles.values) {
                    var tmpx = item.x * 10 - 5
                    var tmpy = item.y * 10 - 5
                    var tmpdirection = ObstacleStatusUtil.adjustDirection(
                        item.direction!!
                    )
                    var tmpdegree = 0
                    if (tmpdirection == "N") {
                        tmpdegree = 90
                    }
                    else if (tmpdirection == "S") {
                        tmpdegree = -90
                    }
                    else if (tmpdirection == "E") {
                        tmpdegree = 0
                    }
                    else if (tmpdirection == "W") {
                        tmpdegree = 180
                    }
                    string = "[" + tmpx + "," + tmpy + "," + tmpdegree + "," + item.number + "]"
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
            clearObstacleDialog()
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

    private fun showCustomDialog(){
        val builder = AlertDialog.Builder(this)
        val customView= LayoutInflater.from(this).inflate(R.layout.custom_dialog,null)
        builder.setView(customView)
        val dialog=builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val xInput = customView.findViewById<EditText>(R.id.xInput)
        setEditTextRange(xInput, 0, 20)
        val yInput = customView.findViewById<EditText>(R.id.yInput)
        setEditTextRange(yInput, 0, 20)
        val continueButton=customView.findViewById<Button>(R.id.continueButton)
        val cancelButton=customView.findViewById<Button>(R.id.cancelButton)
        continueButton.setOnClickListener {
            val index =
                Integer.parseInt(xInput.text.toString()) + Integer.parseInt(yInput.text.toString()) * 20
            adapter!!.addObstacle(adapter!!.holders[index]!!)
            dialog.dismiss()
        }
        cancelButton.setOnClickListener{
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun clearObstacleDialog(){
        val builder = AlertDialog.Builder(this)
        val customView= LayoutInflater.from(this).inflate(R.layout.clear_obstacle_dialog,null)
        builder.setView(customView)
        val dialog=builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val yesButton=customView.findViewById<Button>(R.id.yesButton)
        val cancelClearButton=customView.findViewById<Button>(R.id.cancelClearButton)
        yesButton.setOnClickListener {
            var holder: GridAdapter.ViewHolder? = null
            for (index: Int in GridAdapter.obstacles.keys) {
                holder = GridAdapter.obstacles[index]
                GridAdapter!!.clearObstacle(holder!!)
                    }
            GridAdapter.obstacles = mutableMapOf<Int, GridAdapter.ViewHolder>()
            dialog.dismiss()}

        cancelClearButton.setOnClickListener{
            dialog.dismiss()
        }

        dialog.show()

    }

    private fun initBlueTooth(){
        btConnector!!.setHandler(handler)
    }
}


