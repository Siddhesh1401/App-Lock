package com.applock.secure.manager

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.applock.secure.data.AppLockDatabase
import com.applock.secure.data.entity.IntruderPhoto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class IntruderManager(private val context: Context) {
    
    private val database = AppLockDatabase.getDatabase(context)
    private val scope = CoroutineScope(Dispatchers.IO)
    
    fun captureIntruderPhoto(
        packageName: String,
        appName: String,
        lifecycleOwner: LifecycleOwner,
        onComplete: () -> Unit = {}
    ) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) 
            != PackageManager.PERMISSION_GRANTED) {
            onComplete()
            return
        }
        
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                val imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()
                
                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    imageCapture
                )
                
                val photoFile = createPhotoFile()
                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                
                imageCapture.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            scope.launch {
                                database.intruderPhotoDao().insertIntruderPhoto(
                                    IntruderPhoto(
                                        packageName = packageName,
                                        appName = appName,
                                        photoPath = photoFile.absolutePath
                                    )
                                )
                            }
                            cameraProvider.unbindAll()
                            onComplete()
                        }
                        
                        override fun onError(exception: ImageCaptureException) {
                            cameraProvider.unbindAll()
                            onComplete()
                        }
                    }
                )
            } catch (e: Exception) {
                onComplete()
            }
        }, ContextCompat.getMainExecutor(context))
    }
    
    private fun createPhotoFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = File(context.filesDir, "intruder_photos")
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        return File(storageDir, "INTRUDER_$timestamp.jpg")
    }
}
