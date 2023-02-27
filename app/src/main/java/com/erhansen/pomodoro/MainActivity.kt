package com.erhansen.pomodoro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import com.erhansen.pomodoro.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {

    private var time = 0
    private var maxProgress = 0
    private val countDownTime = 60

    //private lateinit var dialog: BottomSheetDialog
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        //dialog = BottomSheetDialog(applicationContext)

        with(activityMainBinding) {

            pauseButton.visibility = View.INVISIBLE

            toggleGroup.check(R.id.pomodoroButton)
            checkPomodoroButton()

            toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->

                if (isChecked) {
                    when (checkedId) {
                        R.id.shortBreakButton -> {
                            linearLayout.setBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.light_blue))
                            chronometer.text = "05:00"
                            maxProgress = 300
                            time = 300000
                        }
                        R.id.pomodoroButton -> {
                            checkPomodoroButton()
                        }
                        R.id.longBreakButton -> {
                            linearLayout.setBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.dark_blue))
                            chronometer.text = "15:00"
                            maxProgress = 900
                            time = 900000
                        }
                    }
                }

            }

            startButton.setOnClickListener {
                startTimer(time.toLong(), maxProgress)
                pauseButton.visibility = View.VISIBLE
                startButton.visibility = View.INVISIBLE
            }

            pauseButton.setOnClickListener {
                startButton.visibility = View.VISIBLE
                pauseButton.visibility = View.INVISIBLE
                pauseTimer()
            }

            addTaskButton.setOnClickListener {
                /*val bottomSheetDialog = BottomSheetDialog(this@MainActivity)
                val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
                bottomSheetDialog.setContentView(bottomSheetView)
                bottomSheetDialog.show()*/
                val newTask = NewTask(this@MainActivity)
                newTask.showBottomSheet()
            }

        }


    }


    private fun startTimer(time: Long, maxProgress: Int) {
        with(activityMainBinding) {
            progressBar.max = maxProgress
            chronometer.base = SystemClock.elapsedRealtime() + (countDownTime * 1000)
            chronometer.format = "mm:ss"

            countDownTimer = object : CountDownTimer(time, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val seconds = (millisUntilFinished / 1000).toInt()
                    val minutes = seconds / 60
                    val remainderSeconds = seconds % 60
                    progressBar.progress = maxProgress - ((minutes * 60) + remainderSeconds)
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

    private fun pauseTimer() {
        countDownTimer.cancel()
        val currentTime = activityMainBinding.chronometer.text.split(":")
        val minute = currentTime[0].toInt()
        val second = currentTime[1].toInt()
        time = if (currentTime[0] != "00") ((minute * 60) + second) * 1000
        else second * 1000
    }

    private fun checkPomodoroButton() {
        activityMainBinding.linearLayout.setBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.red))
        activityMainBinding.chronometer.text = "25:00"
        maxProgress = 1500
        time = 1500000
    }

}