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

class ContainerView : View {

    private var icon: Bitmap? = null

    private var ringColor = Color.WHITE

    private val colorWhiteAlpha = 0xe5ffffff.toInt()

    private val ringInnerR = dip(33) / 2f

    private val ringOuterR = dip(42) / 2f

    private val circleOuterR = dip(44) / 2f

    private val textBoxHeight = dip(32)

    private val textMarginStart = dip(3)

    private val textMarginEnd = dip(12)

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

    fun init(color: Int, caption: String?, bitmap: Bitmap?) {
        ringColor = ContextCompat.getColor(context, color)
        text = caption
        icon = bitmap
        val height = circleOuterR.toInt() * 2
        var width = height
        text?.also {
            paint.getTextBounds(it, 0, it.length, textBounds)
            width += 2 * (textMarginStart + textBounds.width() + textMarginEnd)
        }
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
            style = Paint.Style.FILL
            if (!TextUtils.isEmpty(text)) {
                // drawing rect
                color = colorWhiteAlpha
                canvas.drawRoundRect(cX, textPadding, w, h - textPadding, textBoxR, textBoxR, this)
                color = Color.BLACK
                // drawing text
                canvas.drawText(
                    text!!, cX + circleOuterR + textMarginStart, (h + textBounds.height() - textBounds.bottom) / 2, this
                )
            }
            // drawing circle
            color = colorWhiteAlpha
            canvas.drawCircle(cX, cY, circleOuterR, this)
            // drawing ring
            color = ringColor
            style = Paint.Style.STROKE
            canvas.drawCircle(cX, cY, ringInnerR, this)
            // drawing icon
            icon?.let {
                canvas.drawBitmap(it, cX - it.width / 2, cY - it.height / 2, this)
            }
        }
    }

    override fun hasOverlappingRendering() = false
}