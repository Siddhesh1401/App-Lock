package com.applock.secure.ui.setup

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.applock.secure.R
import com.applock.secure.ui.lock.view.PatternLockView
import com.applock.secure.ui.lock.view.PinLockView
import com.applock.secure.util.HashUtils
import com.applock.secure.util.SecurityPreferences
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout

class SetupAuthActivity : AppCompatActivity() {
    
    private lateinit var securityPrefs: SecurityPreferences
    private var currentMode = MODE_PIN
    private var tempPin: String? = null
    private var tempPattern: List<Int>? = null
    private var isConfirmStep = false
    
    companion object {
        private const val MODE_PIN = 0
        private const val MODE_PATTERN = 1
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        securityPrefs = SecurityPreferences(this)
        
        setupTabs()
        showPinSetup()
    }
    
    private fun setupTabs() {
        findViewById<TabLayout>(R.id.tabLayout)?.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        0 -> {
                            currentMode = MODE_PIN
                            showPinSetup()
                        }
                        1 -> {
                            currentMode = MODE_PATTERN
                            showPatternSetup()
                        }
                    }
                    resetSetup()
                }
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            }
        )
    }
    
    private fun showPinSetup() {
        setContentView(R.layout.activity_setup_pin)
        setupTabs()
        
        val pinView = findViewById<PinLockView>(R.id.pinLockView)
        val titleText = findViewById<android.widget.TextView>(R.id.titleText)
        
        pinView?.setOnPinEnteredListener { pin ->
            if (!isConfirmStep) {
                tempPin = pin
                isConfirmStep = true
                titleText?.text = getString(R.string.confirm_pin)
                pinView.clear()
            } else {
                if (pin == tempPin) {
                    savePin(pin)
                } else {
                    Toast.makeText(this, R.string.pin_mismatch, Toast.LENGTH_SHORT).show()
                    resetSetup()
                    pinView.clear()
                }
            }
        }
    }
    
    private fun showPatternSetup() {
        setContentView(R.layout.activity_setup_pattern)
        setupTabs()
        
        val patternView = findViewById<PatternLockView>(R.id.patternLockView)
        val titleText = findViewById<android.widget.TextView>(R.id.titleText)
        
        patternView?.setOnPatternListener { pattern ->
            if (!isConfirmStep) {
                tempPattern = pattern
                isConfirmStep = true
                titleText?.text = getString(R.string.confirm_pattern)
                patternView.clearPattern()
            } else {
                if (pattern == tempPattern) {
                    savePattern(pattern)
                } else {
                    Toast.makeText(this, R.string.pattern_mismatch, Toast.LENGTH_SHORT).show()
                    resetSetup()
                    patternView.clearPattern()
                }
            }
        }
    }
    
    private fun savePin(pin: String) {
        securityPrefs.pin = HashUtils.hashString(pin)
        securityPrefs.authMethod = SecurityPreferences.AUTH_METHOD_PIN
        securityPrefs.setupComplete = true
        finish()
    }
    
     private fun savePattern(pattern: List<Int>) {
        securityPrefs.pattern = HashUtils.hashString(HashUtils.patternToString(pattern))
        securityPrefs.authMethod = SecurityPreferences.AUTH_METHOD_PATTERN
        securityPrefs.setupComplete = true
        finish()
    }
    
    private fun resetSetup() {
        isConfirmStep = false
        tempPin = null
        tempPattern = null
    }
}
