package com.team37.mdpandroid.gui.manager

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.team37.mdpandroid.gui.data.GridElement

class GridViewAdapter() : BaseAdapter() {
    val gridList = mutableListOf<GridElement>()
    init{
        for (i in 1..14) {
            for (j in 1..14) {
                gridList.add(GridElement(i, j))
            }
        }
    }

    override fun getCount(): Int {
        TODO("Not yet implemented")
    }

    override fun getItem(p0: Int): Any {
        TODO("Not yet implemented")
    }

    override fun getItemId(p0: Int): Long {
        TODO("Not yet implemented")
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        TODO("Not yet implemented")
    }
}