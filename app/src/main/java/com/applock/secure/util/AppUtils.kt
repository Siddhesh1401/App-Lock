package com.applock.secure.util

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: Drawable,
    val isSystemApp: Boolean
)

object AppUtils {
    
    fun getInstalledApps(context: Context): List<AppInfo> {
        val packageManager = context.packageManager
        val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        
        return packages
            .filter { app ->
                // Filter out this app and system apps that shouldn't be locked
                app.packageName != context.packageName &&
                packageManager.getLaunchIntentForPackage(app.packageName) != null
            }
            .map { app ->
                AppInfo(
                    packageName = app.packageName,
                    appName = app.loadLabel(packageManager).toString(),
                    icon = app.loadIcon(packageManager),
                    isSystemApp = (app.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                )
            }
            .sortedBy { it.appName }
    }
    
    fun getAppInfo(context: Context, packageName: String): AppInfo? {
        return try {
            val packageManager = context.packageManager
            val app = packageManager.getApplicationInfo(packageName, 0)
            AppInfo(
                packageName = app.packageName,
                appName = app.loadLabel(packageManager).toString(),
                icon = app.loadIcon(packageManager),
                isSystemApp = (app.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            )
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }
}
