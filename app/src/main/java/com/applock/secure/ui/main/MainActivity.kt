package com.applock.secure.ui.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.applock.secure.R
import com.applock.secure.data.AppLockDatabase
import com.applock.secure.ui.apps.AppSelectorActivity
import com.applock.secure.ui.intruder.IntruderGalleryActivity
import com.applock.secure.ui.settings.SettingsActivity
import com.applock.secure.ui.setup.SetupAuthActivity
import com.applock.secure.util.PermissionUtils
import com.applock.secure.util.SecurityPreferences
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    private lateinit var securityPrefs: SecurityPreferences
    private lateinit var database: AppLockDatabase
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        securityPrefs = SecurityPreferences(this)
        database = AppLockDatabase.getDatabase(this)
        
        // Check if setup is needed
        if (!securityPrefs.setupComplete) {
            startActivity(Intent(this, SetupAuthActivity::class.java))
        }
        
        setupUI()
        checkPermissions()
    }
    
    private fun setupUI() {
        findViewById<MaterialButton>(R.id.btnLockApps)?.setOnClickListener {
            startActivity(Intent(this, AppSelectorActivity::class.java))
        }
        
        findViewById<MaterialButton>(R.id.btnSettings)?.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        
        findViewById<MaterialButton>(R.id.btnIntruderLogs)?.setOnClickListener {
            startActivity(Intent(this, IntruderGalleryActivity::class.java))
        }
        
        findViewById<MaterialCardView>(R.id.serviceStatusCard)?.setOnClickListener {
            if (!PermissionUtils.isAccessibilityServiceEnabled(this)) {
                showAccessibilityDialog()
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        updateServiceStatus()
    }
    
    private fun updateServiceStatus() {
        val statusText = findViewById<android.widget.TextView>(R.id.statusText)
        val statusIcon = findViewById<android.widget.ImageView>(R.id.statusIcon)
        
        if (PermissionUtils.isAccessibilityServiceEnabled(this)) {
            statusText?.text = getString(R.string.status_service_running)
            statusIcon?.setImageResource(android.R.drawable.ic_dialog_info)
            statusText?.setTextColor(getColor(R.color.success))
        } else {
            statusText?.text = getString(R.string.status_service_stopped)
            statusIcon?.setImageResource(android.R.drawable.ic_dialog_alert)
            statusText?.setTextColor(getColor(R.color.error))
        }
        
        // Update locked apps count
        lifecycleScope.launch {
            val count = database.lockedAppDao().getLockedAppsCount().firstOrNull() ?: 0
            findViewById<android.widget.TextView>(R.id.lockedAppsCount)?.text = 
                getString(R.string.locked_apps_count, count)
        }
    }
    
    private fun checkPermissions() {
        if (!PermissionUtils.hasOverlayPermission(this)) {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.permission_overlay_title)
                .setMessage(R.string.permission_overlay_message)
                .setPositiveButton(R.string.grant_permission) { _, _ ->
                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    startActivity(intent)
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }
    }
    
    private fun showAccessibilityDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.permission_accessibility_title)
            .setMessage(R.string.permission_accessibility_message)
            .setPositiveButton(R.string.grant_permission) { _, _ ->
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}
