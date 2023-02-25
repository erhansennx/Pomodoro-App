package com.erhansen.pomodoro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import androidx.core.content.ContextCompat
import com.erhansen.pomodoro.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val countDownTime = 60
    private lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        with(activityMainBinding) {


            toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->

                if (isChecked) {
                    when (checkedId) {
                        R.id.shortBreakButton -> {
                            linearLayout.setBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.light_blue))
                            chronometer.text = "05:00"
                            startButton.setOnClickListener {
                                startTimer(300000)
                            }
                        }
                        R.id.pomodoroButton -> {
                            linearLayout.setBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.red))
                            chronometer.text = "25:00"
                            startButton.setOnClickListener {
                                startTimer(1500000)
                            }
                        }
                        R.id.longBreakButton -> {
                            linearLayout.setBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.dark_blue))
                            chronometer.text = "15:00"
                            startButton.setOnClickListener {
                                startTimer(900000)
                            }
                        }
                    }
                }

            }



        }


    }


    private fun startTimer(time: Long) {
        with(activityMainBinding) {
            chronometer.base = SystemClock.elapsedRealtime() + (countDownTime * 1000)
            chronometer.format = "mm:ss"
            val endTime = SystemClock.elapsedRealtime() + (countDownTime * 1000)
            val countDownTimer = object : CountDownTimer(time, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val seconds = (millisUntilFinished / 1000).toInt()
                    val minutes = seconds / 60
                    val remainderSeconds = seconds % 60
                    if (minutes < 10 && remainderSeconds < 10) chronometer.text = "0$minutes:0$remainderSeconds"
                    else if (minutes < 10 && remainderSeconds > 10) chronometer.text = "0$minutes:$remainderSeconds"
                    else if (minutes > 10 && remainderSeconds < 10) chronometer.text = "$minutes:0$remainderSeconds"
                    else chronometer.text = "$minutes:$remainderSeconds"

                }

                override fun onFinish() {
                    chronometer.text = "00:00"
                }
            }.start()
        }
    }




}