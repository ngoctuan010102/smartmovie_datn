package com.tuanhn.smartmovie.viewmodels

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class CustomVIew(context: Context, attrs: AttributeSet) : View(context, attrs) {
    // Dữ liệu động cho biểu đồ
    var data : List<Pair<String, Float>> = emptyList()
        set(value) {
            field = value
            invalidate() // Vẽ lại view khi có dữ liệu mới
        }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBarChart(canvas)
    }
    fun formatNumber(value: Float): String {
        return when {
            value >= 1_000_000_000 -> String.format("%.1fB", value / 1_000_000_000) // Từ 1 tỷ trở lên
            value >= 1_000_000 -> String.format("%.1fM", value / 1_000_000) // Từ 1 triệu trở lên
            value >= 1_000 -> String.format("%.1fk", value / 1_000) // Từ 1 nghìn trở lên
            else -> value.toString() // Dưới 1 nghìn
        }
    }

    private fun drawBarChart(canvas: Canvas) {
        val barPaint = Paint().apply {
            color = Color.RED
            style = Paint.Style.FILL
        }
        val labelPaint = Paint().apply {
            color = Color.BLACK
            textSize = 20f
            textAlign = Paint.Align.CENTER
        }
        val valuePaint = Paint().apply {
            color = Color.GRAY
            textSize = 20f
            textAlign = Paint.Align.RIGHT
        }
        val barValuePaint = Paint().apply {
            color = Color.BLACK
            textSize = 20f
            textAlign = Paint.Align.CENTER
        }

        // Kích thước và khoảng cách cần thiết
        val paddingLeft = 100f // Khoảng trống dành cho các giá trị mốc bên trái
        val paddingBottom = 70f // Khoảng trống dành cho nhãn dưới thanh
        val maxHeight = height - paddingBottom - 50f // Dư ra cho trục Y và nhãn
        var maxValue = data.maxOfOrNull { it.second } ?: 0f // Giá trị lớn nhất trong dữ liệu
        if (data.isNotEmpty()) {
            val barWidth = (width - paddingLeft) / (data.size * 2) // Chia đôi để dành chỗ cho khoảng cách giữa các thanh

            // Vẽ các mốc giá trị (ticks) trên trục Y
            val numTicks = 5 // Số lượng mốc giá trị cần hiển thị
            val tickSpacing = maxHeight / numTicks
            for (i in 0..numTicks) {
                val y = height - paddingBottom - (i * tickSpacing)
                val tickValue = (i * maxValue / numTicks)


                canvas.drawText(formatNumber(tickValue), paddingLeft - 20f, y + 15f, valuePaint)

                // Vẽ đường ngang (line) cho các mốc giá trị
                canvas.drawLine(paddingLeft, y, width.toFloat(), y, valuePaint)
            }

            // Vẽ các thanh (bars) và nhãn
            data.forEachIndexed { index, (label, value) ->
                val barHeight = (value / maxValue) * maxHeight
                val left = paddingLeft + index * 2 * barWidth + barWidth / 2
                val right = left + barWidth
                val top = height - paddingBottom - barHeight
                val bottom = height - paddingBottom

                // Vẽ thanh
                barPaint.color = when (index) {
                    0 -> Color.parseColor("#FF5733")
                    1 -> Color.parseColor("#FF5F57")
                    2 -> Color.parseColor("#C70039")
                    3 -> Color.parseColor("#2C3E50")
                    else -> Color.RED
                }
                canvas.drawRect(left, top, right, bottom, barPaint)

                // Vẽ nhãn dưới thanh
                canvas.drawText(label, (left + right) / 2, height.toFloat() - 20f, labelPaint)

                // Vẽ giá trị trên đỉnh thanh
                canvas.drawText(formatNumber(value), (left + right) / 2, top - 10f, barValuePaint)
            }
        }
    }
}