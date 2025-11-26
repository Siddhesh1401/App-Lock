package com.applock.secure.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.applock.secure.data.AppLockDatabase
import com.applock.secure.ui.lock.LockScreenActivity
import kotlinx.coroutines.*

class AppMonitorService : AccessibilityService() {
    
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private lateinit var database: AppLockDatabase
    private val lockedPackages = mutableSetOf<String>()
    
    override fun onServiceConnected() {
        super.onServiceConnected()
        database = AppLockDatabase.getDatabase(this)
        
        serviceScope.launch {
            database.lockedAppDao().getAllLockedApps().collect { apps ->
                lockedPackages.clear()
                lockedPackages.addAll(apps.map { it.packageName })
            }
        }
    }
    
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString() ?: return
            
            // Check if this package is locked
            if (packageName in lockedPackages && packageName != this.packageName) {
                showLockScreen(packageName)
            }
        }
    }
    
    private fun showLockScreen(packageName: String) {
        val intent = Intent(this, LockScreenActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            putExtra(LockScreenActivity.EXTRA_PACKAGE_NAME, packageName)
        }
        startActivity(intent)
    }
    
    override fun onInterrupt() {
        // Handle interruption
    }
    
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
