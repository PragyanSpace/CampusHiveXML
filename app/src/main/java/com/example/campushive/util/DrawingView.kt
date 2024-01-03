package com.example.campushive.util

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {
    private val paint: Paint = Paint()
    private val path: Path = Path()
    private var drawingListener: DrawingListener? = null
    private var isDrawingEnabled = true
    private var drawingBitmap: Bitmap? = null
    private var scaledBitmap: Bitmap? = null

    init {
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f
    }

    fun setToPen() {
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f
    }

    fun setToEraser() {
        paint.color = Color.WHITE
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 20f
    }

    fun setDrawingListener(listener: DrawingListener?) {
        drawingListener = listener
    }

    fun enableDrawing() {
        isDrawingEnabled = true
    }

    fun disableDrawing() {
        isDrawingEnabled = false
    }

    fun setDrawingBitmap(bitmap: Bitmap?) {
        drawingBitmap = bitmap?.copy(Bitmap.Config.ARGB_8888, true)
        drawingBitmap?.let {
            scaledBitmap = Bitmap.createScaledBitmap(it, width, height, true)
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        scaledBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, paint)
        }
        canvas.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isDrawingEnabled) {
            return false
        }

        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(x, y)
                return true
            }
            MotionEvent.ACTION_MOVE -> path.lineTo(x, y)
            MotionEvent.ACTION_UP -> {
                drawingListener?.onDrawingFinished(drawingAsBitmap())
            }
            else -> return false
        }
        invalidate()
        return true
    }

    private fun drawingAsBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        return bitmap
    }

    fun clearCanvas() {
        path.reset()
        drawingBitmap?.eraseColor(Color.WHITE)
        invalidate()
        drawingListener?.onDrawingFinished(drawingAsBitmap())
    }

    interface DrawingListener {
        fun onDrawingFinished(drawingBitmap: Bitmap?)
    }
}
