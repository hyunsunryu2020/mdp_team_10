package com.team37.mdpandroid.gui.data

import android.util.Log

class Robot() : Object{
    var x: Int
    var y: Int
    var status: String? = null
    var direction = 'N'

    init {
        x = -1
        y = -1
    }

    fun setCoordinates(x: Int, y: Int) {
        this.x = x
        this.y = y
    }
    fun containsCoordinate(x: Int, y: Int): Boolean {
        Log.d("ROBOT:", "" + this.x + ", " + this.y)
        return if (this.x <= x && x <= this.x + 2 && this.y <= y && y <= this.y + 2) {
            true
        } else false
    }

    fun moveRobotForward() {
        val robotDir = direction
        if (x != -1 && y != -1) {
            if (robotDir == 'N') {
                val newY = y + 1
                if (newY <= 17) {
                    y = newY
                }
            } else if (robotDir == 'S') {
                val newY = y - 1
                if (newY >= 0) {
                    y = newY
                }
            } else if (robotDir == 'E') {
                val newX = x + 1
                if (newX <= 17) {
                    x = newX
                }
            } else {
                //W
                val newX = x - 1
                if (newX >= 0) {
                    x = newX
                }
            }
        }
    }

    fun moveRobotTurnLeft() {
        if (x != -1 && y != -1) {
            val robotDir = direction
            if (robotDir == 'N') {
                direction = 'W'
            } else if (robotDir == 'S') {
                direction = 'E'
            } else if (robotDir == 'E') {
                direction = 'N'
            } else {
                //W
                direction = 'S'
            }
        }
    }

    fun moveRobotTurnRight() {
        if (x != -1 && y != -1) {
            val robotDir = direction
            if (robotDir == 'N') {
                direction = 'E'
            } else if (robotDir == 'S') {
                direction = 'W'
            } else if (robotDir == 'E') {
                direction = 'S'
            } else {
                //W
                direction = 'N'
            }
        }
    }

    fun reset() {
        setCoordinates(-1, -1)
        direction = 'N'
    }
}