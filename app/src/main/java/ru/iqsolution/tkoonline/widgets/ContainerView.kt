/*
 * Copyright (c) 2019. Vlad Kalyuzhnyu <vladkalyuzhnyu@gmail.com>
 */

@file:Suppress("unused")

package ru.iqsolution.tkoonline.widgets

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import org.jetbrains.anko.dip
import org.jetbrains.anko.sp
import timber.log.Timber

class ContainerView : View {

    private var icon: Bitmap? = null

    private var ringColor = Color.WHITE

    private val colorWhiteAlpha = 0xe5ffffff.toInt()

    private val ringInnerR = dip(33) / 2f

    private val ringOuterR = dip(42) / 2f

    private val circleOuterR = dip(50) / 2f

    private val textBoxHeight = dip(40)

    private val textMarginStart = dip(3)

    private val textMarginEnd = dip(16)

    private val textMarginVert = dip(12)

    private val textBoxR = dip(5).toFloat()

    private var text: String? = null

    private val textBounds = Rect()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = sp(14).toFloat()
        strokeWidth = ringOuterR - ringInnerR
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

    fun init(color: Int, text: String?, icon: Bitmap?) {
        ringColor = ContextCompat.getColor(context, color)
        this.text = text
        this.icon = icon
        val height = circleOuterR.toInt() * 2
        var width = height
        text?.also {
            paint.getTextBounds(it, 0, it.length, textBounds)
            Timber.d("textBounds " + textBounds.toString())
            width += 2 * (textMarginStart + textBounds.width() + textMarginEnd)
        }
        Timber.d("height " + height)
        Timber.d("width " + width)
        layoutParams = ViewGroup.LayoutParams(width, height)
        minimumHeight = height
        minimumWidth = width
    }

    override fun onDraw(canvas: Canvas) {
        val w = width.toFloat()
        val h = height.toFloat()
        val cX = w / 2
        val cY = h / 2
        val textPadding = (h - textBoxHeight) / 2
        paint.apply {
            color = colorWhiteAlpha
            style = Paint.Style.FILL
            if (!TextUtils.isEmpty(text)) {
                canvas.drawRoundRect(cX, textPadding, w, h - textPadding, textBoxR, textBoxR, this)
                color = Color.BLACK
                canvas.drawText(
                    text!!, cX + circleOuterR + textMarginStart,
                    (circleOuterR + textBounds.height()) / 2 + textPadding, this
                )
            }
            color = colorWhiteAlpha
            canvas.drawCircle(cX, cY, circleOuterR, this)
            color = ringColor
            style = Paint.Style.STROKE
            canvas.drawCircle(cX, cY, ringInnerR, this)
        }
    }

    companion object {

        private const val HEIGHT = 0
    }
}