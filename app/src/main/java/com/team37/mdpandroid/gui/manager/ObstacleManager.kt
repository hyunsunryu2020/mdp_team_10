package com.team37.mdpandroid.gui.manager

import com.team37.mdpandroid.gui.data.Obstacle
import com.team37.mdpandroid.gui.util.ObstacleStatusUtil

class ObstacleManager private constructor() {
    companion object {
        val instance: ObstacleManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            ObstacleManager() }
        var flagNumber = 1;
        fun getObstacleWithDirection(direction: Int): Obstacle{
            var obstacle: Obstacle? = null
            when (direction){
                ObstacleStatusUtil.up -> obstacle = Obstacle(ObstacleStatusUtil.up, flagNumber++)
                ObstacleStatusUtil.right -> obstacle = Obstacle(ObstacleStatusUtil.right, flagNumber++)
                ObstacleStatusUtil.down -> obstacle = Obstacle(ObstacleStatusUtil.down, flagNumber++)
                ObstacleStatusUtil.left -> obstacle = Obstacle(ObstacleStatusUtil.left, flagNumber++)
            }
            return obstacle!!;
        }
    }

//    fun generateNewObstacle(direction: Int, image: Int): Obstacle{
//        return Obstacle(direction, image)
//    }

//    fun generateNewObstacleByInput(): Obstacle{
//
//    }
}