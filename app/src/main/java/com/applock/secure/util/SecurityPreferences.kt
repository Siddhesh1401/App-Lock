package com.applock.secure.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecurityPreferences(context: Context) {
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "security_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    companion object {
        private const val KEY_PIN = "pin"
        private const val KEY_PATTERN = "pattern"
        private const val KEY_AUTH_METHOD = "auth_method"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEY_INTRUDER_SELFIE = "intruder_selfie"
        private const val KEY_FAKE_CRASH = "fake_crash"
        private const val KEY_MAX_ATTEMPTS = "max_attempts"
        private const val KEY_SETUP_COMPLETE = "setup_complete"
        
        const val AUTH_METHOD_PIN = "pin"
        const val AUTH_METHOD_PATTERN = "pattern"
        const val AUTH_METHOD_BIOMETRIC = "biometric"
    }
    
    var pin: String?
        get() = prefs.getString(KEY_PIN, null)
        set(value) = prefs.edit().putString(KEY_PIN, value).apply()
    
    var pattern: String?
        get() = prefs.getString(KEY_PATTERN, null)
        set(value) = prefs.edit().putString(KEY_PATTERN, value).apply()
    
    var authMethod: String
        get() = prefs.getString(KEY_AUTH_METHOD, AUTH_METHOD_PIN) ?: AUTH_METHOD_PIN
        set(value) = prefs.edit().putString(KEY_AUTH_METHOD, value).apply()
    
    var biometricEnabled: Boolean
        get() = prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, value).apply()
    
    var intruderSelfieEnabled: Boolean
        get() = prefs.getBoolean(KEY_INTRUDER_SELFIE, true)
        set(value) = prefs.edit().putBoolean(KEY_INTRUDER_SELFIE, value).apply()
    
    var fakeCrashEnabled: Boolean
        get() = prefs.getBoolean(KEY_FAKE_CRASH, false)
        set(value) = prefs.edit().putBoolean(KEY_FAKE_CRASH, value).apply()
    
    var maxAttempts: Int
        get() = prefs.getInt(KEY_MAX_ATTEMPTS, 3)
        set(value) = prefs.edit().putInt(KEY_MAX_ATTEMPTS, value).apply()
    
    var setupComplete: Boolean
        get() = prefs.getBoolean(KEY_SETUP_COMPLETE, false)
        set(value) = prefs.edit().putBoolean(KEY_SETUP_COMPLETE, value).apply()
    
    fun clear() {
        prefs.edit().clear().apply()
    }
}
