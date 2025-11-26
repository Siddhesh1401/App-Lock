package com.applock.secure.ui.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.applock.secure.R
import com.applock.secure.util.SecurityPreferences
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    
    private lateinit var securityPrefs: SecurityPreferences
    
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            findViewById<SwitchMaterial>(R.id.switchIntruderSelfie)?.isChecked = false
            Toast.makeText(this, R.string.permission_camera_message, Toast.LENGTH_LONG).show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.settings_title)
        
        securityPrefs = SecurityPreferences(this)
        
        setupSettings()
    }
    
    private fun setupSettings() {
        // Intruder Selfie
        findViewById<SwitchMaterial>(R.id.switchIntruderSelfie)?.apply {
            isChecked = securityPrefs.intruderSelfieEnabled
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked && ContextCompat.checkSelfPermission(
                        this@SettingsActivity,
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                } else {
                    securityPrefs.intruderSelfieEnabled = isChecked
                }
            }
        }
        
        // Fake Crash
        findViewById<SwitchMaterial>(R.id.switchFakeCrash)?.apply {
            isChecked = securityPrefs.fakeCrashEnabled
            setOnCheckedChangeListener { _, isChecked ->
                securityPrefs.fakeCrashEnabled = isChecked
            }
        }
        
        // Biometric
        findViewById<SwitchMaterial>(R.id.switchBiometric)?.apply {
            isChecked = securityPrefs.biometricEnabled
            setOnCheckedChangeListener { _, isChecked ->
                securityPrefs.biometricEnabled = isChecked
                if (isChecked) {
                    securityPrefs.authMethod = SecurityPreferences.AUTH_METHOD_BIOMETRIC
                }
            }
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
