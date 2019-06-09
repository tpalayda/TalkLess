package com.tpalayda.talkless.fragments

import android.content.Context
import android.os.Build
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import kotlinx.android.synthetic.main.fragment_timer.*
import kotlinx.android.synthetic.main.fragment_timer.view.*

class MyCountDownTimer : CountDownTimer {

    private var pause = false
    private var rootView : View
    private var ctx : Context
    private var vibrationTime = 0L
    private var vibrationAmplitude = 0L
    private var i = 0
    private var totalTime = 0L
    private var currentTime = 0L
    var timeRemaining : Long = 0L


    constructor(millisInFuture : Long, countDownInterval : Long, view : View,
                vibrationTime : Long, ctx : Context, totalTime : Long, vibrationAmplitude : Long) : super(millisInFuture, countDownInterval) {
        rootView = view
        this.vibrationTime = vibrationTime
        this.ctx = ctx
        this.totalTime = totalTime
        this.currentTime = millisInFuture
        this.vibrationAmplitude = vibrationAmplitude
    }

    fun pause() {
        pause = true
    }

    fun resume() {
        pause = false
    }

    override fun onFinish() {
        rootView.progressBar.progress = 0

        if(vibrationTime > 0) {
            val vibrator = ctx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if(Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(VibrationEffect.createOneShot(vibrationTime, 10))
            } else {
                vibrator.vibrate(vibrationTime);
            }
        }

        rootView.countdownText.visibility = View.GONE
        rootView.pauseButton.visibility = View.GONE
        rootView.stopButton.visibility = View.GONE
        rootView.startButton.visibility = View.VISIBLE
        showNumberPickers(rootView)
    }

    override fun onTick(millisUntilFinished: Long) {
        if(!pause) {

            val minutes = millisUntilFinished / 60000
            val seconds = millisUntilFinished % 60000 / 1000

            var timeLeftText = "" + minutes + ":"
            if(seconds < 10)
                timeLeftText += "0"
            timeLeftText += seconds
            ++i
            rootView.countdownText.text = timeLeftText

            val fraction : Float = millisUntilFinished/totalTime.toFloat()
            rootView.progressBar.progress = (fraction*100).toInt()
        } else {
            if(timeRemaining == 0L) {
                timeRemaining = millisUntilFinished
                cancel()
            }
        }
    }

    private fun showNumberPickers(rootView: View) {
        rootView.hoursNumberPicker.visibility = View.VISIBLE
        rootView.minutesNumberPicker.visibility = View.VISIBLE
        rootView.secondsNumberPicker.visibility = View.VISIBLE
    }

}