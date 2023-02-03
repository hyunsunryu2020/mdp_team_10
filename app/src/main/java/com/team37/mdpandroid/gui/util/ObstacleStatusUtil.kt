package com.team37.mdpandroid.gui.util

import com.team37.mdpandroid.R
import com.team37.mdpandroid.gui.data.Obstacle

object ObstacleStatusUtil {
    val up = 1
    val right = 2
    val down = 3
    val left = 4
    val one = 11
    val two = 12

    val directions = mutableMapOf<Int, Obstacle>()
    val obstacles = mutableMapOf<Int, String>()
    val colors = listOf<Int>(R.color.white, R.color.green, R.color.blue, R.color.red, R.color.yellow)

    init{
        for (i in 11..19)
            obstacles[i] = ('0'.toByte().toInt()+(i-10)).toChar().toString()
        for (i in 20..27)
            obstacles[i] = ('A'.toByte().toInt()+(i-20)).toChar().toString()
        for (i in 28..35)
            obstacles[i] = ('S'.toByte().toInt()+(i-28)).toChar().toString()
        obstacles[36] = "↑"
        obstacles[37] = "↓"
        obstacles[38] = "→"
        obstacles[39] = "←"
        obstacles[40] = "⚪"

        directions[up] = Obstacle(direction = up)
        directions[left] = Obstacle(direction = left)
        directions[down] = Obstacle(direction = down)
        directions[right] = Obstacle(direction = right)
    }

    fun adjustDirection(direction: Int): String{
        return when(direction){
            up -> "N"
            left -> "W"
            right -> "E"
            down -> "S"
            else -> {""}
        }
    }
}