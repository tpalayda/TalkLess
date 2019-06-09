package com.tpalayda.talkless.fragments

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.tpalayda.talkless.R
import kotlinx.android.synthetic.main.fragment_timer.*
import kotlinx.android.synthetic.main.fragment_timer.view.*
import java.text.DecimalFormat

class TimerFragment : Fragment() {

    companion object {
        const val MY_PERMISSIONS_VIBRATE = 1
    }

    private var totalTime : Long = 0
    private var vibrationTime : Long = 100
    private lateinit var countdownTimer : MyCountDownTimer
    private var i = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView =  inflater.inflate(R.layout.fragment_timer, container, false)

        setNumberPickerFormatters(rootView)
        setNumberPickerValues(rootView)

        rootView.startButton.setOnClickListener {
            totalTime = 0
            totalTime += hoursNumberPicker.value * 3600000
            totalTime += minutesNumberPicker.value * 60000
            totalTime += secondsNumberPicker.value * 1000

            hideNumberPickers(rootView)

            rootView.countdownText.visibility = View.VISIBLE
            rootView.pauseButton.visibility = View.VISIBLE
            rootView.stopButton.visibility = View.VISIBLE
            rootView.startButton.visibility = View.GONE

            countdownTimer = MyCountDownTimer(totalTime, 100, rootView, vibrationTime, this.context!!, totalTime)
            countdownTimer.start()
        }

        rootView.pauseButton.setOnClickListener {
            countdownTimer.pause()
            rootView.pauseButton.visibility = View.GONE
            rootView.continueButton.visibility = View.VISIBLE
        }

        rootView.continueButton.setOnClickListener {
            countdownTimer.resume()
            val remainingTime = countdownTimer.timeRemaining
            countdownTimer = MyCountDownTimer(remainingTime, 100, rootView, vibrationTime, this.context!!, totalTime)
            countdownTimer.start()
            rootView.pauseButton.visibility = View.VISIBLE
            rootView.continueButton.visibility = View.GONE
        }

        rootView.stopButton.setOnClickListener {
            countdownTimer.cancel()
            rootView.continueButton.visibility = View.GONE
            rootView.stopButton.visibility = View.GONE
            rootView.pauseButton.visibility = View.GONE
            rootView.countdownText.visibility = View.GONE
            rootView.countdownText.text = ""
            progressBar.progress = 0
            rootView.startButton.visibility = View.VISIBLE
            showNumberPickers(rootView)
        }

        return rootView
    }

    private fun setNumberPickerFormatters(rootView : View) {
        rootView.minutesNumberPicker.setFormatter {
            it->
            DecimalFormat("00").format(it)
        }
        rootView.secondsNumberPicker.setFormatter {
            it ->
            DecimalFormat("00").format(it)
        }
        rootView.hoursNumberPicker.setFormatter {
            it ->
            DecimalFormat("00").format(it)
        }
    }

    private fun setNumberPickerValues(rootView: View) {
        rootView.hoursNumberPicker.maxValue = 23
        rootView.hoursNumberPicker.minValue = 0

        rootView.minutesNumberPicker.maxValue = 59
        rootView.minutesNumberPicker.minValue = 0
        rootView.minutesNumberPicker.value = 0

        rootView.secondsNumberPicker.maxValue = 59
        rootView.secondsNumberPicker.minValue = 0
        rootView.secondsNumberPicker.value = 5
    }

    private fun hideNumberPickers(rootView: View) {
        rootView.hoursNumberPicker.visibility = View.GONE
        rootView.minutesNumberPicker.visibility = View.GONE
        rootView.secondsNumberPicker.visibility = View.GONE
    }

    private fun showNumberPickers(rootView: View) {
        rootView.hoursNumberPicker.visibility = View.VISIBLE
        rootView.minutesNumberPicker.visibility = View.VISIBLE
        rootView.secondsNumberPicker.visibility = View.VISIBLE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        //outState.putInt("progressBarValue", progressBar.progress)
        //TODO visibility of buttons, etc.
        super.onSaveInstanceState(outState)
    }

}

