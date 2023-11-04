package com.example.a3dforge.base

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

class AvatarProcessor {
    fun addBorderWithScale(originalBitmap: Bitmap, borderWidth: Int, borderColor: Int, avatarScale: Float): Bitmap {
        val frameSize = (originalBitmap.width + 2 * borderWidth).toFloat()
        val avatarSize = frameSize * avatarScale

        val resultBitmap = Bitmap.createBitmap(frameSize.toInt(), frameSize.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(resultBitmap)

        val borderPaint = Paint().apply {
            color = borderColor
            style = Paint.Style.STROKE
            strokeWidth = borderWidth.toFloat()
        }

        val borderRect = RectF(
            borderWidth / 2f,
            borderWidth / 2f,
            frameSize - borderWidth / 2f,
            frameSize - borderWidth / 2f
        )

        canvas.drawOval(borderRect, borderPaint)

        val avatarRect = RectF(
            (frameSize - avatarSize) / 2f,
            (frameSize - avatarSize) / 2f,
            (frameSize + avatarSize) / 2f,
            (frameSize + avatarSize) / 2f
        )

        val avatarPaint = Paint()
        canvas.drawBitmap(originalBitmap, null, avatarRect, avatarPaint)

        return resultBitmap
    }
}
