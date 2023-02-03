package com.team37.mdpandroid.gui.util

import com.team37.mdpandroid.bt.BtConnector

class SendMessageUtil {

    companion object{
        val connector = BtConnector.getBtConnectorInstance()
        fun setObstacle(x: Int, y: Int, number: Int){
            var msg = "o_add ($x,$y) $number"
            connector.write(msg)
        }
        fun setObstacleDirection(number: Int, direction: Int){
            var face = when(direction){
                ObstacleStatusUtil.up -> "N"
                ObstacleStatusUtil.right -> "E"
                ObstacleStatusUtil.down -> "S"
                ObstacleStatusUtil.left -> "W"
                else -> {""}
            }
            var msg = "o_fac $face $number"
            connector.write(msg)
        }
        fun setObstacleWithDirection(x: Int, y: Int, number: Int, direction: Int){
            setObstacle(x, y, number)
            setObstacleDirection(number, direction)
        }
        fun deleteObstacle(number: Int){
            var msg = "o_del $number"
            connector.write(msg)
        }
    }

}