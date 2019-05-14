package com.tpalayda.talkless

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.tpalayda.talkless.settings.SettingsActivity
import com.tpalayda.talkless.presentation.PresentationActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DecimalFormat


//TODO save state when rotate
//TODO preference fragment (choosing vibration time, color of the background)
//TODO countdownTimer to another file
//TODO refresh button/play button/pause button centered below text?

class MainActivity : AppCompatActivity() {
    companion object {
        const val MY_PERMISSIONS_VIBRATE = 1
    }

    private var totalTime : Long = 0
    private var vibrationTime : Long = 0
    private lateinit var countdownTimer : CountDownTimer
    private var i = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermissions()

        setNumberPickerFormatters()
        setNumberPickerValues()

        countdownButton.setOnClickListener {
            totalTime = 0
            totalTime += hoursNumberPicker.value * 3600000
            totalTime += minutesNumberPicker.value * 60000
            totalTime += secondsNumberPicker.value * 1000

            hideNumberPickers()

            countdownText.visibility = View.VISIBLE
            pauseButton.visibility = View.VISIBLE
            countdownButton.visibility = View.GONE

            countdownTimer = object : CountDownTimer(totalTime, 100) {
                override fun onTick(millisUntilFinished: Long) {
                    val minutes = millisUntilFinished / 60000
                    val seconds = millisUntilFinished % 60000 / 1000

                    var timeLeftText = "" + minutes + ":"
                    if(seconds < 10)
                        timeLeftText += "0"
                    timeLeftText += seconds
                    ++i
                    countdownText.text = timeLeftText

                    val fraction : Float = millisUntilFinished/totalTime.toFloat()
                    progressBar.progress = (fraction*100).toInt()
                }

                override fun onFinish() {
                    progressBar.progress = 0

                    if(vibrationTime > 0) {
                        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                        if(Build.VERSION.SDK_INT >= 26) {
                            vibrator.vibrate(VibrationEffect.createOneShot(vibrationTime, 10))
                        } else {
                            vibrator.vibrate(vibrationTime);
                        }
                    }

                    countdownText.visibility = View.GONE
                    pauseButton.visibility = View.GONE
                    countdownButton.visibility = View.VISIBLE
                    showNumberPickers()
                }
            }.start()
        }

        pauseButton.setOnClickListener {
            countdownTimer.cancel()
            pauseButton.visibility = View.GONE
            continueButton.visibility = View.VISIBLE
        }

        continueButton.setOnClickListener {
            countdownTimer.start()
            pauseButton.visibility = View.VISIBLE
            continueButton.visibility = View.GONE
        }
    }

    private fun checkPermissions() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission()
        } else {
            Log.wtf("123", "permission granted")
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.activity -> {
                val intent = Intent(this, PresentationActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return true
    }

    private fun setNumberPickerFormatters() {
        minutesNumberPicker.setFormatter {
            it->
            DecimalFormat("00").format(it)
        }
        secondsNumberPicker.setFormatter {
            it ->
            DecimalFormat("00").format(it)
        }
        hoursNumberPicker.setFormatter {
            it ->
            DecimalFormat("00").format(it)
        }
    }

    private fun setNumberPickerValues() {
        hoursNumberPicker.maxValue = 23
        hoursNumberPicker.minValue = 0

        minutesNumberPicker.maxValue = 59
        minutesNumberPicker.minValue = 0
        minutesNumberPicker.value = 0

        secondsNumberPicker.maxValue = 59
        secondsNumberPicker.minValue = 0
        secondsNumberPicker.value = 5
    }

    private fun hideNumberPickers() {
        hoursNumberPicker.visibility = View.GONE
        minutesNumberPicker.visibility = View.GONE
        secondsNumberPicker.visibility = View.GONE
    }

    private fun showNumberPickers() {
        hoursNumberPicker.visibility = View.VISIBLE
        minutesNumberPicker.visibility = View.VISIBLE
        secondsNumberPicker.visibility = View.VISIBLE
    }
}
