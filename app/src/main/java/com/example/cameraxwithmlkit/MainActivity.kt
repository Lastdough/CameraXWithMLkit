package com.example.cameraxwithmlkit

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var timerScan = System.currentTimeMillis()
    var timerArr = System.currentTimeMillis()
    val arrStr: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        val txt = getString(R.string.decoded_text)


        if (isAllPermissionsGranted) startCamera() else requestPermissions()
    }

    private val isAllPermissionsGranted get() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() = ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (isAllPermissionsGranted) {
                startCamera()
            } else {
                Snackbar.make(preview_view, "Camera permission not granted. \nCannot perform magic ritual.", Snackbar.LENGTH_LONG).setAction("Retry") {
                    requestPermissions()
                }.show()
            }
        }
    }

    fun mostFrequent(arr: MutableList<String>): String {
        val mostFreq = arr.groupingBy { it }.eachCount()
        return mostFreq.maxBy { it.value }.key
    }

    private val cameraAdapter = CameraAdapter {
        arrStr.add(it)
        val decodedText: TextView = findViewById(R.id.decoded_text)

        if (timerScan + 2000 < System.currentTimeMillis()) {
            Log.d(TAG, "Text Found: $it")
            decodedText.text = mostFrequent(arrStr)
            timerScan = System.currentTimeMillis()
        }
        if (timerArr + 4000 < System.currentTimeMillis()){
            arrStr.clear()
            timerArr = System.currentTimeMillis()
        }
    }

    private fun startCamera() {
        cameraAdapter.startCamera(this, this, preview_view.surfaceProvider)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraAdapter.shutdown()
    }


    companion object {
        private val TAG = MainActivity::class.java.name
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}