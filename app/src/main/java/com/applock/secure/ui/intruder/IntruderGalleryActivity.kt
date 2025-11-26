package com.applock.secure.ui.intruder

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.applock.secure.R
import com.applock.secure.data.AppLockDatabase
import com.applock.secure.data.entity.IntruderPhoto
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class IntruderGalleryActivity : AppCompatActivity() {
    
    private lateinit var database: AppLockDatabase
    private lateinit var adapter: IntruderAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intruder_gallery)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.intruder_title)
        
        database = AppLockDatabase.getDatabase(this)
        
        setupRecyclerView()
        loadIntruderPhotos()
    }
    
    private fun setupRecyclerView() {
        adapter = IntruderAdapter { photo ->
            showDeleteDialog(photo)
        }
        
        findViewById<RecyclerView>(R.id.recyclerView)?.apply {
            layoutManager = GridLayoutManager(this@IntruderGalleryActivity, 2)
            this.adapter = this@IntruderGalleryActivity.adapter
        }
    }
    
    private fun loadIntruderPhotos() {
        lifecycleScope.launch {
            val photos = database.intruderPhotoDao().getAllIntruderPhotos().firstOrNull() ?: emptyList()
            adapter.submitList(photos)
            
            findViewById<View>(R.id.emptyView)?.visibility = 
                if (photos.isEmpty()) View.VISIBLE else View.GONE
        }
    }
    
    private fun showDeleteDialog(photo: IntruderPhoto) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete_photo)
            .setMessage("Delete this intruder photo?")
            .setPositiveButton(R.string.delete) { _, _ ->
                lifecycleScope.launch {
                    database.intruderPhotoDao().deleteIntruderPhoto(photo)
                    File(photo.photoPath).delete()
                    loadIntruderPhotos()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

class IntruderAdapter(
    private val onPhotoClick: (IntruderPhoto) -> Unit
) : RecyclerView.Adapter<IntruderAdapter.PhotoViewHolder>() {
    
    private var photos = listOf<IntruderPhoto>()
    
    fun submitList(newPhotos: List<IntruderPhoto>) {
        photos = newPhotos
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_intruder_photo, parent, false)
        return PhotoViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(photos[position])
    }
    
    override fun getItemCount() = photos.size
    
    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val appName: TextView = itemView.findViewById(R.id.appName)
        private val timestamp: TextView = itemView.findViewById(R.id.timestamp)
        
        fun bind(photo: IntruderPhoto) {
            // Load image
            val file = File(photo.photoPath)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                imageView.setImageBitmap(bitmap)
            }
            
            appName.text = itemView.context.getString(R.string.attempted_app, photo.appName)
            
            val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            timestamp.text = dateFormat.format(Date(photo.timestamp))
            
            itemView.setOnLongClickListener {
                onPhotoClick(photo)
                true
            }
        }
    }
}
