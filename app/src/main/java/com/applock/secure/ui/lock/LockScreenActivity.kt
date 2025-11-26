package com.applock.secure.ui.lock

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.applock.secure.R
import com.applock.secure.data.AppLockDatabase
import com.applock.secure.manager.IntruderManager
import com.applock.secure.util.AppUtils
import com.applock.secure.util.HashUtils
import com.applock.secure.util.SecurityPreferences
import com.applock.secure.ui.lock.view.PatternLockView
import com.applock.secure.ui.lock.view.PinLockView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class LockScreenActivity : AppCompatActivity() {
    
    private lateinit var securityPrefs: SecurityPreferences
    private lateinit var intruderManager: IntruderManager
    private var packageName: String = ""
    private var appName: String = ""
    private var failedAttempts = 0
    private var shouldShowFakeCrash = false
    
    companion object {
        const val EXTRA_PACKAGE_NAME = "extra_package_name"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Security flags
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        
        securityPrefs = SecurityPreferences(this)
        intruderManager = IntruderManager(this)
        
        packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME) ?: ""
        appName = AppUtils.getAppInfo(this, packageName)?.appName ?: "App"
        
        shouldShowFakeCrash = securityPrefs.fakeCrashEnabled
        
        if (shouldShowFakeCrash && failedAttempts == 0) {
            showFakeCrashScreen()
        } else {
            showLockScreen()
        }
    }
    
    private fun showFakeCrashScreen() {
        setContentView(R.layout.activity_fake_crash)
        
        // Long press to reveal real lock screen
        findViewById<android.view.View>(R.id.crashContainer)?.setOnLongClickListener {
            shouldShowFakeCrash = false
            showLockScreen()
            true
        }
        
        findViewById<android.widget.Button>(R.id.btnOk)?.setOnClickListener {
            // Just close and return to home
            moveTaskToBack(true)
        }
    }
    
    private fun showLockScreen() {
        when (securityPrefs.authMethod) {
            SecurityPreferences.AUTH_METHOD_PIN -> showPinLock()
            SecurityPreferences.AUTH_METHOD_PATTERN -> showPatternLock()
            SecurityPreferences.AUTH_METHOD_BIOMETRIC -> showBiometricPrompt()
            else -> showPinLock()
        }
    }
    
    private fun showPinLock() {
        setContentView(R.layout.activity_pin_lock)
        
        val pinLockView = findViewById<PinLockView>(R.id.pinLockView)
        pinLockView?.setOnPinEnteredListener { enteredPin ->
            val hashedPin = HashUtils.hashString(enteredPin)
            if (hashedPin == securityPrefs.pin) {
                unlockApp()
            } else {
                handleFailedAttempt()
                pinLockView.clear()
                pinLockView.showError()
            }
        }
    }
    
    private fun showPatternLock() {
        setContentView(R.layout.activity_pattern_lock)
        
        val patternView = findViewById<PatternLockView>(R.id.patternLockView)
        patternView?.setOnPatternListener { pattern ->
            val hashedPattern = HashUtils.hashString(HashUtils.patternToString(pattern))
            if (hashedPattern == securityPrefs.pattern) {
                unlockApp()
            } else {
                handleFailedAttempt()
                patternView.clearPattern()
                patternView.showError()
            }
        }
    }
    
    private fun showBiometricPrompt() {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                val executor = ContextCompat.getMainExecutor(this)
                val biometricPrompt = BiometricPrompt(this, executor,
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            super.onAuthenticationSucceeded(result)
                            unlockApp()
                        }
                        
                        override fun onAuthenticationFailed() {
                            super.onAuthenticationFailed()
                            handleFailedAttempt()
                        }
                        
                        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                            super.onAuthenticationError(errorCode, errString)
                            // Fall back to PIN
                            showPinLock()
                        }
                    }
                )
                
                val promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle(getString(R.string.app_name))
                    .setSubtitle("Unlock $appName")
                    .setNegativeButtonText("Use PIN")
                    .build()
                
                biometricPrompt.authenticate(promptInfo)
            }
            else -> {
                // Biometric not available, fall back to PIN
                showPinLock()
            }
        }
    }
    
    private fun handleFailedAttempt() {
        failedAttempts++
        
        if (securityPrefs.intruderSelfieEnabled && failedAttempts >= 2) {
            intruderManager.captureIntruderPhoto(packageName, appName, this)
        }
        
        if (failedAttempts >= securityPrefs.maxAttempts) {
            // Too many attempts, close and go home
            moveTaskToBack(true)
        }
    }
    
    private fun unlockApp() {
        finish()
    }
    
    override fun onBackPressed() {
        // Prevent back button from closing lock screen
        moveTaskToBack(true)
    }
}
