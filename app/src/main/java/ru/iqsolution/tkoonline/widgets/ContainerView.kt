/*
 * Copyright (c) 2019. Vlad Kalyuzhnyu <vladkalyuzhnyu@gmail.com>
 */

@file:Suppress("unused")

package ru.iqsolution.tkoonline.widgets

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.view.View
import org.jetbrains.anko.dip

class ContainerView : View {

    var text: String? = null

    var color = 0

    private val colorInnerD = context.dip(33)

    private val colorOuterD = context.dip(42)

    private val textBoxHeight = context.dip(40)

    private val textBoxR = context.dip(5)

    private val textBoxColor = 0xe5ffffff

    private val borderOuterD = context.dip(50)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {

    }

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(
        context,
        attrs,
        defStyleAttr
    )

    @Suppress("unused")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    override fun onDraw(canvas: Canvas) {
        paint.color = Color.WHITE
        /*canvas.drawCircle(paint)
        canvas.drawCircle()
        canvas.drawBitmap()
        text?.let {
            canvas.drawRoundRect()
            canvas.drawText(it, )
        }*/
    }

    companion object {

        private const val HEIGHT = 0
    }
}