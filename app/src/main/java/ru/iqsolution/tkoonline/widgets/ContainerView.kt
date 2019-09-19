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
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.dip
import org.jetbrains.anko.sp

class ContainerView : View {

    var color = Color.WHITE

    private val colorWhiteAlpha = 0xe5ffffff

    private val colorInnerD = dip(33)

    private val colorOuterD = dip(42)

    private val circleOuterR = dip(50) / 2f

    private val textBoxHeight = dip(40)

    private val textMarginStart = dip(3)

    private val textMarginEnd = dip(16)

    private val textMarginVert = dip(12)

    private val textBoxR = dip(5)

    var text: String? = null

    private val textBounds = Rect()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = sp(14).toFloat()
        letterSpacing = sp(16f / 14).toFloat()
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

    fun setTextAndLayout(value: String?) {
        val height = circleOuterR.toInt() * 2
        var width = height
        value?.also {
            paint.getTextBounds(it, 0, it.length, textBounds)
            width += 2 * (textMarginStart + textBounds.width() + textMarginEnd)
        }
        layoutParams = ViewGroup.LayoutParams(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        val cX = width / 2f
        val cY = height / 2f
        canvas.drawColor(Color.RED)
        paint.color = Color.WHITE
        canvas.drawCircle(cX, cY, circleOuterR, paint)
        /*canvas.drawCircle(paint)
        canvas.drawCircle()
        canvas.drawBitmap()
        text?.let {
            canvas.drawRoundRect()
            canvas.drawText(it, )
            canvas.drawText(text, margin, (height + bounds.height()) / 2, paint)
        }*/
    }

    companion object {

        private const val HEIGHT = 0
    }
}