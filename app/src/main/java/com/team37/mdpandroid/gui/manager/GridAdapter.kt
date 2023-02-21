package com.team37.mdpandroid.gui.manager

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.*
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.team37.mdpandroid.R
import com.team37.mdpandroid.gui.data.GridElement
import com.team37.mdpandroid.gui.data.Obstacle
import com.team37.mdpandroid.gui.util.ObstacleStatusUtil
import com.team37.mdpandroid.gui.util.SquareLayout
import android.view.View


class GridAdapter(private val context: Context, private val gridList: List<GridElement>): BaseAdapter() {
    val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    val holders = mutableMapOf<Int, ViewHolder>()

    companion object{
        private val directions = arrayOf("Up", "Right", "Down", "Left")
        private var nums = mutableListOf<Boolean>(false, false, false, false, false, false, false, false)
        var obstacles = mutableMapOf<Int, ViewHolder>()
        fun clearObstacle(holder: ViewHolder){
            clearLines(holder)
            holder.image!!.text = ""
            holder.square!!.setBackgroundColor(Color.WHITE)
            nums[holder.number-1] = false
            holder.number = 0
        }
        private fun clearLines(holder: ViewHolder){
            holder.innerUpLine!!.visibility = View.INVISIBLE
            holder.innerRightLine!!.visibility = View.INVISIBLE
            holder.innerDownLine!!.visibility = View.INVISIBLE
            holder.innerLeftLine!!.visibility = View.INVISIBLE
        }
    }


    override fun getCount(): Int {
        return gridList.size
    }

    override fun getItem(position: Int): Any {
        return gridList[position]
    }

    override fun getItemId(position: Int): Long {
        return gridList.get(position).id;
    }

    fun addObstacleWithDirection(holder: ViewHolder, direction: Int){
        Log.e("?", holder!!.square!!.x.toString() +"," + holder!!.square!!.y)
        var curNum = getNum()
        var obstacle: Obstacle? = null
        clearLines(holder)
        holder.square!!.setBackgroundColor(Color.parseColor("#9E9A9A"))
        if (holder.image!!.text.equals("")){
            holder.number = curNum
            holder.image!!.text = holder.number.toString()
        }
        when (direction) {
            ObstacleStatusUtil.up -> {
                ObstacleManager.getObstacleWithDirection(ObstacleStatusUtil.up)
                holder.innerUpLine!!.visibility = View.VISIBLE
                holder.direction = ObstacleStatusUtil.up
            }
            ObstacleStatusUtil.right -> {
                ObstacleManager.getObstacleWithDirection(ObstacleStatusUtil.right)
                holder.innerRightLine!!.visibility = View.VISIBLE
                holder.direction = ObstacleStatusUtil.right
            }
            ObstacleStatusUtil.down -> {
                ObstacleManager.getObstacleWithDirection(ObstacleStatusUtil.down)
                holder.innerDownLine!!.visibility = View.VISIBLE
                holder.direction = ObstacleStatusUtil.down
            }
            ObstacleStatusUtil.left -> {
                ObstacleManager.getObstacleWithDirection(ObstacleStatusUtil.left)
                holder.innerLeftLine!!.visibility = View.VISIBLE
                holder.direction = ObstacleStatusUtil.left
            }
            else -> {}

        }

        obstacles[holder.number] = holder
    }

    fun addObstacle(holder: ViewHolder){
        val dialog = AlertDialog.Builder(context)
            .setTitle("Please select obstacle direction")
            .setItems(directions,DialogInterface.OnClickListener(){
                    dialogInterface: DialogInterface, i: Int ->
                run {

                    when (i) {
                        0 -> {
                            addObstacleWithDirection(holder, ObstacleStatusUtil.up)
                        }
                        1 -> {
                            addObstacleWithDirection(holder, ObstacleStatusUtil.right)
                        }
                        2 -> {
                            addObstacleWithDirection(holder, ObstacleStatusUtil.down)
                        }
                        3 -> {
                            addObstacleWithDirection(holder, ObstacleStatusUtil.left)
                        }
                        else -> {}

                        }
                    }
//                                SendMessageUtil.setObstacleWithDirection(gridList[position].coordinateX, gridList[position].coordinateY,
//                                    holder.number, holder.direction!!)
            }).show()
    }

    
    @SuppressLint("ResourceAsColor")
    override fun getView(position: Int, view: View?, group: ViewGroup?): View? {
        var holder = ViewHolder()
        var newView: View? = null
        newView = layoutInflater.inflate(R.layout.grid_element, null)
        holder.x = gridList[position].coordinateX
        holder.y = gridList[position].coordinateY
        holder.upLine = newView.findViewById(R.id.upLine) as View
        holder.downLine = newView.findViewById(R.id.downLine) as View
        holder.leftLine = newView.findViewById(R.id.leftLine) as View
        holder.rightLine = newView.findViewById(R.id.rightLine) as View
        holder.innerUpLine = newView.findViewById(R.id.innerUpLine) as View
        holder.innerDownLine = newView.findViewById(R.id.innerDownLine) as View
        holder.innerLeftLine = newView.findViewById(R.id.innerLeftLine) as View
        holder.innerRightLine = newView.findViewById(R.id.innerRightLine) as View
        holder.image = newView.findViewById(R.id.targetImage) as TextView
        newView.setTag(holder);
        val gridElement: GridElement = gridList[position]
        holder.square = newView.findViewById(R.id.squareElement) as SquareLayout
        holder.square!!.setOnClickListener{
            if (!isFull()){
                if (holder.number == 0){
                    addObstacle(holder)
                }
                else{
                    when (holder.direction){
                        ObstacleStatusUtil.up -> {
                            holder.direction = ObstacleStatusUtil.right
                            clearLines(holder)
                            holder.innerRightLine!!.visibility = View.VISIBLE
                        }
                        ObstacleStatusUtil.right -> {
                            holder.direction = ObstacleStatusUtil.down
                            clearLines(holder)
                            holder.innerDownLine!!.visibility = View.VISIBLE
                        }
                        ObstacleStatusUtil.down -> {
                            holder.direction = ObstacleStatusUtil.left
                            clearLines(holder)
                            holder.innerLeftLine!!.visibility = View.VISIBLE
                        }
                        ObstacleStatusUtil.left -> {
                            holder.direction = ObstacleStatusUtil.up
                            clearLines(holder)
                            holder.innerUpLine!!.visibility = View.VISIBLE
                        }
                    }
//                    SendMessageUtil.setObstacleDirection(holder.number, holder.direction!!)
                }
            }
            else{
                Toast.makeText(context, "There are already 8 images!", Toast.LENGTH_SHORT).show()
            }
        }
        holder.square!!.setOnLongClickListener {
            if (!holder.image!!.text.equals("")) {
                val builder = View.DragShadowBuilder(it)
                it.startDragAndDrop(null, builder, holder, 0)
            }
            true
        }
        holder.square!!.setOnDragListener{ view, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_ENDED -> Log.i("Drag", "End")
                DragEvent.ACTION_DROP -> {
                    if (holder.image!!.text.equals("")){
                        var localState = event.localState as ViewHolder
                        when (localState.direction){
                            ObstacleStatusUtil.up -> holder.innerUpLine!!.visibility = View.VISIBLE
                            ObstacleStatusUtil.right -> holder.innerRightLine!!.visibility = View.VISIBLE
                            ObstacleStatusUtil.down -> holder.innerDownLine!!.visibility = View.VISIBLE
                            ObstacleStatusUtil.left -> holder.innerLeftLine!!.visibility = View.VISIBLE
                        }
                        holder.number = localState.number
                        holder.direction = localState.direction
                        holder.square!!.setBackgroundColor(Color.parseColor("#9E9A9A"))
                        holder.image!!.text = localState.image!!.text
//                        SendMessageUtil.deleteObstacle(localState.number)
                        obstacles.remove(localState.number)
                        clearObstacle(localState)
//                        SendMessageUtil.setObstacleWithDirection(gridList[position].coordinateX,
//                            gridList[position].coordinateY, holder.number, holder.direction!!)
                        obstacles[holder.number] = holder
                    }
                }
            }
            true
        }
        val index = gridList[position].coordinateX + gridList[position].coordinateY * 20
        holders[index] = holder
        return newView;
    }


    private fun getNum(): Int{
        for (i in 0..nums.size-1){
            if (!nums[i]) {
                nums[i] = true
                return i + 1
            }
        }
        return 0
    }

    private fun isFull(): Boolean{
        for (i in 0..nums.size-1){
            if (!nums[i]) {
                return false
            }
        }
        return true
    }


    class ViewHolder(){
        var upLine: View? = null
        var downLine: View? = null
        var leftLine: View? = null
        var rightLine: View? = null
        var innerUpLine: View? = null
        var innerDownLine: View? = null
        var innerLeftLine: View? = null
        var innerRightLine: View? = null
        var image: TextView? = null
        var square: SquareLayout? = null
        var direction: Int? = null
        var number: Int = 0
        var x: Int = 0
        var y: Int = 0
    }
}