package com.tpalayda.talkless

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    //private lateinit var button : Button
    companion object {
        const val MY_PERMISSIONS_VIBRATE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermissions()
        test_button.setOnClickListener {
            val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

            if(Build.VERSION.SDK_INT >= 26) {
                v.vibrate(VibrationEffect.createOneShot(1000, 1))
            } else {
                v.vibrate(1000);
            }
        }
    }

    private fun checkPermissions() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission()
        } else {
            Log.wtf("123", "permission granted");
        }
    }

    private fun requestPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.VIBRATE)) {
            Log.wtf("123", "text here");
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.VIBRATE), MY_PERMISSIONS_VIBRATE)
            Log.wtf("123", "request permission")
        }
    }
}
