package com.itj.sandersonwidget.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.content.res.ResourcesCompat.getColor
import com.itj.sandersonwidget.R

/**
 * A custom vector graphic exported as a BitMap so it can be applied to a widget's ImageView.
 *
 * Since the set of RemoteViews is so limited: any custom view must be drawn using Canvas and exported as a flat
 * asset to be displayed as an Image. This specific image is the circular progress widget; it is formed by stacking
 * drawn circles on top of each other.
 */
internal fun getCustomProgressBarBitMap(
    context: Context,
    frameSize: Int,
    outlineWidth: Int,
    progressBarWidth: Int,
    progress: Int,
    progressColor: Int,
): Bitmap {
    val radiusPercentage = frameSize / 200
    val canvasCenter = (frameSize / 2)

    val backgroundColor = getColor(context.resources, android.R.color.transparent, null)
    val progressNegativeCircleColor = getColor(context.resources, R.color.transparent_black, null)
    val topCircleColor = getColor(context.resources, R.color.translucent_black, null)
    val textColor = getColor(context.resources, android.R.color.white, null)

    val bitMap = Bitmap.createBitmap(frameSize, frameSize, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitMap)
    canvas.drawColor(backgroundColor)

    val outLineCirclePaint = Paint().apply {
        color = progressColor // here
        strokeWidth = 10f
        isAntiAlias = true
    }
    canvas.drawCircle(
        canvasCenter.toFloat(),
        canvasCenter.toFloat(),
        canvasCenter.toFloat(),
        outLineCirclePaint
    )

    val progressNegativeCirclePaint = Paint().apply {
        color = progressNegativeCircleColor
        strokeWidth = 10f
        isAntiAlias = true
    }
    canvas.drawCircle(
        canvasCenter.toFloat(),
        canvasCenter.toFloat(),
        (canvasCenter - (radiusPercentage * outlineWidth)).toFloat(),
        progressNegativeCirclePaint
    )

    val progressPaint = Paint().apply {
        color = progressColor
        strokeWidth = 10f
        isAntiAlias = true
    }
    canvas.drawArc(
        (0 + (radiusPercentage * outlineWidth)).toFloat(),
        (0 + (radiusPercentage * outlineWidth)).toFloat(),
        (frameSize - (radiusPercentage * outlineWidth)).toFloat(),
        (frameSize - (radiusPercentage * outlineWidth)).toFloat(),
        270F, (3.6 * progress).toFloat(), true, progressPaint
    )

    // Inline circle
    canvas.drawCircle(
        canvasCenter.toFloat(),
        canvasCenter.toFloat(),
        (canvasCenter - (radiusPercentage * (outlineWidth + progressBarWidth))).toFloat(),
        outLineCirclePaint
    )

    val topCirclePaint = Paint().apply {
        color = topCircleColor
        strokeWidth = 10f
        isAntiAlias = true
    }
    canvas.drawCircle(
        canvasCenter.toFloat(),
        canvasCenter.toFloat(),
        (canvasCenter - (radiusPercentage * (outlineWidth + progressBarWidth + outlineWidth))).toFloat(),
        topCirclePaint
    )

    val textSizeRatio = 50
    val textPaint = Paint().apply {
        color = textColor
        strokeWidth = 10f
        textSize = (radiusPercentage * textSizeRatio).toFloat()
    }
    val progressText = "$progress%"
    val charCount = progressText.toCharArray().size
    canvas.drawText(
        progressText,
        (canvasCenter - (radiusPercentage * (textSizeRatio * (0.25 * charCount)))).toFloat(),
        (canvasCenter + (radiusPercentage * (textSizeRatio * 0.4))).toFloat(),
        textPaint
    )

    return bitMap
}
