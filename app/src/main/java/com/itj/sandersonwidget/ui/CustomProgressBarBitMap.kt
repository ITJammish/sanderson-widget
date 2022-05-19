package com.itj.sandersonwidget.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.content.res.ResourcesCompat

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

    val backgroundColor = ResourcesCompat.getColor(context.resources, android.R.color.transparent, null)
    val bitMap = Bitmap.createBitmap(frameSize, frameSize, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitMap)
    canvas.drawColor(backgroundColor)

    val outlineCircleColor = ResourcesCompat.getColor(context.resources, android.R.color.black, null)
    val outLineCirclePaint = Paint().apply {
        color = outlineCircleColor
        strokeWidth = 10f
        isAntiAlias = true
    }
    canvas.drawCircle(
        canvasCenter.toFloat(),
        canvasCenter.toFloat(),
        canvasCenter.toFloat(),
        outLineCirclePaint
    )

    val progressNegativeCircleColor = ResourcesCompat.getColor(context.resources, android.R.color.white, null)
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

    val topCircleColor = ResourcesCompat.getColor(context.resources, android.R.color.white, null)
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
    val textColor = ResourcesCompat.getColor(context.resources, android.R.color.black, null)
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
