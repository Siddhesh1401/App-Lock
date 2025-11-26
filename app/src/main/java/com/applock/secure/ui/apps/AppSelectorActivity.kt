package com.applock.secure.ui.apps

import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.applock.secure.R
import com.applock.secure.data.AppLockDatabase
import com.applock.secure.data.entity.LockedApp
import com.applock.secure.util.AppInfo
import com.applock.secure.util.AppUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppSelectorActivity : AppCompatActivity() {
    
    private lateinit var database: AppLockDatabase
    private lateinit var adapter: AppListAdapter
    private var allApps = listOf<AppInfo>()
    private var lockedPackages = setOf<String>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_selector)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.apps_title)
        
        database = AppLockDatabase.getDatabase(this)
        setupRecyclerView()
        loadApps()
    }
    
    private fun setupRecyclerView() {
        adapter = AppListAdapter { appInfo, isLocked ->
            lifecycleScope.launch {
                if (isLocked) {
                    database.lockedAppDao().insertLockedApp(
                        LockedApp(appInfo.packageName, appInfo.appName)
                    )
                } else {
                    database.lockedAppDao().deleteByPackage(appInfo.packageName)
                }
            }
        }
        
        findViewById<RecyclerView>(R.id.recyclerView)?.apply {
            layoutManager = LinearLayoutManager(this@AppSelectorActivity)
            this.adapter = this@AppSelectorActivity.adapter
        }
        
        findViewById<SearchView>(R.id.searchView)?.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?) = false
                override fun onQueryTextChange(newText: String?): Boolean {
                    filterApps(newText ?: "")
                    return true
                }
            }
        )
    }
    
    private fun loadApps() {
        lifecycleScope.launch {
            val apps = withContext(Dispatchers.IO) {
                AppUtils.getInstalledApps(this@AppSelectorActivity)
            }
            
            val locked = database.lockedAppDao().getAllLockedApps().firstOrNull() ?: emptyList()
            lockedPackages = locked.map { it.packageName }.toSet()
            
            allApps = apps
            adapter.submitList(apps, lockedPackages)
        }
    }
    
    private fun filterApps(query: String) {
        val filtered = if (query.isEmpty()) {
            allApps
        } else {
            allApps.filter { it.appName.contains(query, ignoreCase = true) }
        }
        adapter.submitList(filtered, lockedPackages)
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
