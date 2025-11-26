package com.applock.secure.ui.lock.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.applock.secure.R
import com.google.android.material.button.MaterialButton

class PinLockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    
    private val pinDisplay: TextView
    private var currentPin = StringBuilder()
    private var onPinEnteredListener: ((String) -> Unit)? = null
    private val pinLength = 4
    
    init {
        LayoutInflater.from(context).inflate(R.layout.view_pin_lock, this, true)
        
        pinDisplay = findViewById(R.id.pinDisplay)
        
        setupKeypad()
    }
    
    private fun setupKeypad() {
        val buttons = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        )
        
        buttons.forEach { btnId ->
            findViewById<MaterialButton>(btnId)?.setOnClickListener { button ->
                val digit = (button as MaterialButton).text.toString()
                addDigit(digit)
            }
        }
        
        findViewById<MaterialButton>(R.id.btnBackspace)?.setOnClickListener {
            removeDigit()
        }
    }
    
    private fun addDigit(digit: String) {
        if (currentPin.length < pinLength) {
            currentPin.append(digit)
            updateDisplay()
            
            if (currentPin.length == pinLength) {
                onPinEnteredListener?.invoke(currentPin.toString())
            }
        }
    }
    
    private fun removeDigit() {
        if (currentPin.isNotEmpty()) {
            currentPin.deleteCharAt(currentPin.length - 1)
            updateDisplay()
        }
    }
    
    private fun updateDisplay() {
        val dots = "•".repeat(currentPin.length) + "○".repeat(pinLength - currentPin.length)
        pinDisplay.text = dots
    }
    
    fun clear() {
        currentPin.clear()
        updateDisplay()
    }
    
    fun showError() {
        pinDisplay.animate()
            .translationX(-20f)
            .setDuration(50)
            .withEndAction {
                pinDisplay.animate()
                    .translationX(20f)
                    .setDuration(50)
                    .withEndAction {
                        pinDisplay.animate()
                            .translationX(0f)
                            .setDuration(50)
                            .start()
                    }
                    .start()
            }
            .start()
    }
    
    fun setOnPinEnteredListener(listener: (String) -> Unit) {
        onPinEnteredListener = listener
    }
}
