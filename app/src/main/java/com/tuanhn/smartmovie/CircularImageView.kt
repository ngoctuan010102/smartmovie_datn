package com.tuanhn.smartmovie

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
class CircularImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val path = Path()
    private val paint = Paint()

    override fun onDraw(canvas: Canvas) {
        // Vẽ hình tròn
        val radius = Math.min(width, height) / 2f
        path.reset()
        path.addCircle(width / 2f, height / 2f, radius, Path.Direction.CCW)
        canvas.clipPath(path)

        // Vẽ hình ảnh
        super.onDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredSize = Math.min(measuredWidth, measuredHeight)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(widthMeasureSpec)
            MeasureSpec.AT_MOST -> Math.min(desiredSize, MeasureSpec.getSize(widthMeasureSpec))
            else -> desiredSize
        }

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(heightMeasureSpec)
            MeasureSpec.AT_MOST -> Math.min(desiredSize, MeasureSpec.getSize(heightMeasureSpec))
            else -> desiredSize
        }

        setMeasuredDimension(width, height)
    }
}