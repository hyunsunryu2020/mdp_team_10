package com.team37.mdpandroid.gui.data

class GridElement (var y: Int, var x: Int){
    var coordinateX = x
    var coordinateY = 21-y
    var item: Object? = null
    var isOccupied: Boolean = false
        get() = item == null
    val id: Long = (coordinateX*14+coordinateY).toLong()
    init {

    }
}