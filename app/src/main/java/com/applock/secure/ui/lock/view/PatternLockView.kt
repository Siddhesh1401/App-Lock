package com.applock.secure.ui.lock.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.applock.secure.R
import kotlin.math.pow
import kotlin.math.sqrt

class PatternLockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    
    private val dots = mutableListOf<Dot>()
    private val selectedDots = mutableListOf<Int>()
    private var currentX = 0f
    private var currentY = 0f
    private var isDrawing = false
    
    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.primary_light)
        style = Paint.Style.FILL
    }
    
    private val selectedDotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.primary)
        style = Paint.Style.FILL
    }
    
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.primary)
        strokeWidth = 8f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    
    private val errorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.error)
        style = Paint.Style.FILL
    }
    
    private var isError = false
    private var onPatternListener: ((List<Int>) -> Unit)? = null
    
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setupDots()
    }
    
    private fun setupDots() {
        dots.clear()
        val padding = 80f
        val size = minOf(width, height) - padding * 2
        val spacing = size / 2
        val startX = (width - size) / 2
        val startY = (height - size) / 2
        
        for (row in 0..2) {
            for (col in 0..2) {
                val x = startX + col * spacing
                val y = startY + row * spacing
                dots.add(Dot(x, y, row * 3 + col))
            }
        }
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Draw lines between selected dots
        if (selectedDots.size > 1) {
            val path = Path()
            val firstDot = dots[selectedDots[0]]
            path.moveTo(firstDot.x, firstDot.y)
            
            for (i in 1 until selectedDots.size) {
                val dot = dots[selectedDots[i]]
                path.lineTo(dot.x, dot.y)
            }
            
            if (isDrawing && selectedDots.isNotEmpty()) {
                path.lineTo(currentX, currentY)
            }
            
            canvas.drawPath(path, if (isError) errorPaint else linePaint)
        }
        
        // Draw dots
        dots.forEach { dot ->
            val isSelected = selectedDots.contains(dot.id)
            val paint = when {
                isError && isSelected -> errorPaint
                isSelected -> selectedDotPaint
                else -> dotPaint
            }
            canvas.drawCircle(dot.x, dot.y, if (isSelected) 30f else 20f, paint)
        }
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isDrawing = true
                isError = false
                selectedDots.clear()
                checkDotSelection(event.x, event.y)
                invalidate()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (isDrawing) {
                    currentX = event.x
                    currentY = event.y
                    checkDotSelection(event.x, event.y)
                    invalidate()
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                isDrawing = false
                if (selectedDots.size >= 4) {
                    onPatternListener?.invoke(selectedDots.toList())
                } else {
                    clearPattern()
                }
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }
    
    private fun checkDotSelection(x: Float, y: Float) {
        dots.forEach { dot ->
            if (!selectedDots.contains(dot.id)) {
                val distance = sqrt((x - dot.x).pow(2) + (y - dot.y).pow(2))
                if (distance < 50f) {
                    selectedDots.add(dot.id)
                }
            }
        }
    }
    
    fun clearPattern() {
        selectedDots.clear()
        isError = false
        invalidate()
    }
    
    fun showError() {
        isError = true
        invalidate()
        postDelayed({
            clearPattern()
        }, 1000)
    }
    
    fun setOnPatternListener(listener: (List<Int>) -> Unit) {
        onPatternListener = listener
    }
    
    private data class Dot(val x: Float, val y: Float, val id: Int)
}
