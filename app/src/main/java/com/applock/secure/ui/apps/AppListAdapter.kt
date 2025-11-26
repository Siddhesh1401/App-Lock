package com.applock.secure.ui.apps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.applock.secure.R
import com.applock.secure.util.AppInfo
import com.google.android.material.switchmaterial.SwitchMaterial

class AppListAdapter(
    private val onAppLockChanged: (AppInfo, Boolean) -> Unit
) : RecyclerView.Adapter<AppListAdapter.AppViewHolder>() {
    
    private var apps = listOf<AppInfo>()
    private var lockedPackages = setOf<String>()
    
    fun submitList(newApps: List<AppInfo>, locked: Set<String>) {
        apps = newApps
        lockedPackages = locked
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_app, parent, false)
        return AppViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.bind(apps[position])
    }
    
    override fun getItemCount() = apps.size
    
    inner class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val icon: ImageView = itemView.findViewById(R.id.appIcon)
        private val name: TextView = itemView.findViewById(R.id.appName)
        private val packageText: TextView = itemView.findViewById(R.id.packageName)
        private val lockSwitch: SwitchMaterial = itemView.findViewById(R.id.lockSwitch)
        
        fun bind(appInfo: AppInfo) {
            icon.setImageDrawable(appInfo.icon)
            name.text = appInfo.appName
            packageText.text = appInfo.packageName
            
            // Set switch state without triggering listener
            lockSwitch.setOnCheckedChangeListener(null)
            lockSwitch.isChecked = appInfo.packageName in lockedPackages
            
            lockSwitch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    lockedPackages = lockedPackages + appInfo.packageName
                } else {
                    lockedPackages = lockedPackages - appInfo.packageName
                }
                onAppLockChanged(appInfo, isChecked)
            }
        }
    }
}
