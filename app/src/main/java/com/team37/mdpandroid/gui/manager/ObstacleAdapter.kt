package com.team37.mdpandroid.gui.manager

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.team37.mdpandroid.R
import com.team37.mdpandroid.gui.util.ObstacleStatusUtil
import kotlin.math.acos

class ObstacleAdapter(val context: Context):RecyclerView.Adapter<ObstacleAdapter.ViewHolder>() {
    val obstacles = ObstacleStatusUtil.directions.values.toList()
    var activity: Activity? = null
    init{
        activity = context as Activity
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObstacleAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.obstacle_element, parent, false))
    }

    override fun onBindViewHolder(holder: ObstacleAdapter.ViewHolder, position: Int) {
//        holder.image.text = obstacles[position].image
//        holder.image.setTextColor(obstacles[position].color)
        when (obstacles[position].direction){
            ObstacleStatusUtil.up -> {
                holder.upLine.visibility = View.VISIBLE
            }
            ObstacleStatusUtil.left -> {
                holder.leftLine.visibility = View.VISIBLE
            }
            ObstacleStatusUtil.right -> {
                holder.rightLine.visibility = View.VISIBLE
            }
            ObstacleStatusUtil.down -> {
                holder.downLine.visibility = View.VISIBLE
            }
        }
        holder.view.setOnLongClickListener {
            val builder = View.DragShadowBuilder(it)
            it.startDragAndDrop(null, builder, holder.image.text, 0)
            val drawerLayout: DrawerLayout = activity!!.findViewById(R.id.mainPageDrawerLayout)
            drawerLayout!!.closeDrawer(Gravity.RIGHT)
            true
        }
    }

    override fun getItemCount(): Int {
        return obstacles.size
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<TextView>(R.id.obstacleImage)
        val upLine = view.findViewById<View>(R.id.upLine)
        val leftLine = view.findViewById<View>(R.id.leftLine)
        val rightLine = view.findViewById<View>(R.id.rightLine)
        val downLine = view.findViewById<View>(R.id.downLine)
    }
}