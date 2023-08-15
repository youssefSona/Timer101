package com.example.timer101

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.timer101.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var b: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.textView.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.facebook.com/Gumball.Code/")
                )
            )
        }

//        startService(Intent(this@MainActivity, OverlayService::class.java))

        if (!isOverlayPermissionGranted()) {
            AlertDialog.Builder(this)
                .setTitle("Requesting Permission")
                .setMessage("This app uses overlay display permission to work!")
                .setPositiveButton("Grant Permission") { dialogInterface: DialogInterface, _: Int ->
                    // Action to perform when "OK" button is clicked
                    requestOverlayPermission()
                    dialogInterface.dismiss()
                }
                .setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int ->
                    // Action to perform when "Cancel" button is clicked
                    dialogInterface.dismiss()
                }.show()
        } else {
            startOverlayService()
        }
    }

    private fun isOverlayPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            // Overlay permission is granted automatically on versions below Android M
            true
        }
    }

    private val REQUEST_OVERLAY_PERMISSION = 123

    private fun requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_OVERLAY_PERMISSION) {
            if (isOverlayPermissionGranted()) {
                startOverlayService()
            }
        }
    }

    private fun startOverlayService() {
        startService(Intent(this@MainActivity, OverlayService::class.java))
    }
}