package com.erhansen.pomodoro.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.provider.ContactsContract.Data
import android.view.View
import androidx.core.content.ContextCompat
import com.erhansen.pomodoro.R
import com.erhansen.pomodoro.adapter.RecyclerAdapter
import com.erhansen.pomodoro.database.DatabaseHandler
import com.erhansen.pomodoro.databinding.ActivityMainBinding
import com.erhansen.pomodoro.model.TaskModal

class MainActivity : AppCompatActivity() {

    private var time = 0
    private var mode = "Pomodoro"
    private var maxProgress = 0
    private val countDownTime = 60
    private var isTimerRunning = false
    private lateinit var databaseHandler: DatabaseHandler
    private var taskArrayList: ArrayList<TaskModal> = arrayListOf()
    private lateinit var newTask: NewTask
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)


        with(activityMainBinding) {

            newTask = NewTask(this@MainActivity, recyclerView)
            databaseHandler = DatabaseHandler(this@MainActivity)
            databaseHandler.getAllData()

            pauseButton.visibility = View.INVISIBLE

            toggleGroup.check(R.id.pomodoroButton)
            checkPomodoroButton()

            toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->

                if (isChecked) {
                    when (checkedId) {
                        R.id.shortBreakButton -> {
                            if (!isTimerRunning) {
                                linearLayout.setBackgroundColor(ContextCompat.getColor(this@MainActivity,
                                    R.color.light_blue
                                ))
                                chronometer.text = "05:00"
                                mode = "Short Break"
                                maxProgress = 300
                                time = 300000
                            }
                        }
                        R.id.pomodoroButton -> {
                            if (!isTimerRunning) {
                                checkPomodoroButton()
                                mode = "Pomodoro"
                            }
                        }
                        R.id.longBreakButton -> {
                            if (!isTimerRunning) {
                                linearLayout.setBackgroundColor(ContextCompat.getColor(this@MainActivity,
                                    R.color.dark_blue
                                ))
                                chronometer.text = "15:00"
                                mode = "Long Break"
                                maxProgress = 900
                                time = 900000
                            }
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
                //val newTask = NewTask(this@MainActivity)
                newTask.showBottomSheet()
                recyclerView.adapter?.notifyDataSetChanged()
            }

            refresh.setOnClickListener {
                refreshTimer(mode)
                startButton.visibility = View.VISIBLE
                pauseButton.visibility = View.INVISIBLE
            }

            taskArrayList = newTask.loadData()
            val customRecyclerAdapter = RecyclerAdapter(context = applicationContext, taskArrayList)
            recyclerView.adapter = customRecyclerAdapter

        }


    }


    private fun startTimer(time: Long, maxProgress: Int) {
        with(activityMainBinding) {
            progressBar.max = maxProgress
            chronometer.base = SystemClock.elapsedRealtime() + (countDownTime * 1000)
            chronometer.format = "mm:ss"
            isTimerRunning = true
            activityMainBinding.shortBreakButton.isClickable = false
            activityMainBinding.pomodoroButton.isClickable = false
            activityMainBinding.longBreakButton.isClickable = false

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
        isTimerRunning = false
        activityMainBinding.shortBreakButton.isClickable = true
        activityMainBinding.pomodoroButton.isClickable = true
        activityMainBinding.longBreakButton.isClickable = true
        val currentTime = activityMainBinding.chronometer.text.split(":")
        val minute = currentTime[0].toInt()
        val second = currentTime[1].toInt()
        time = if (currentTime[0] != "00") ((minute * 60) + second) * 1000
        else second * 1000
    }

    private fun refreshTimer(mode: String) {
        countDownTimer.cancel()
        isTimerRunning = false
        activityMainBinding.shortBreakButton.isClickable = true
        activityMainBinding.pomodoroButton.isClickable = true
        activityMainBinding.longBreakButton.isClickable = true
        activityMainBinding.progressBar.progress = 0
        when(mode) {
            "Short Break" -> activityMainBinding.chronometer.text = "05:00"
            "Pomodoro" -> checkPomodoroButton()
            "Long Break" -> activityMainBinding.chronometer.text = "15:00"
        }
    }

    private fun checkPomodoroButton() {
        activityMainBinding.linearLayout.setBackgroundColor(ContextCompat.getColor(this@MainActivity,
            R.color.red
        ))
        activityMainBinding.chronometer.text = "25:00"
        maxProgress = 1500
        time = 1500000
    }

}

