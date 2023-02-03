package com.team37.mdpandroid.gui.util

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout

class SquareLayout: RelativeLayout{

    constructor(context: Context): super(context)

    constructor(context: Context, attrs: AttributeSet): super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int): super(context, attrs, defStyle)


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}