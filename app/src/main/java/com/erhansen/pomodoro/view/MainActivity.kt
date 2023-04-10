package com.erhansen.pomodoro.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.erhansen.pomodoro.R
import com.erhansen.pomodoro.adapter.RecyclerAdapter
import com.erhansen.pomodoro.database.DatabaseHandler
import com.erhansen.pomodoro.databinding.ActivityMainBinding
import com.erhansen.pomodoro.model.ItemModal
import com.erhansen.pomodoro.model.TaskModal

class MainActivity : AppCompatActivity() {

    private var time = 0
    private var mode = "Pomodoro"
    private var maxProgress = 0
    private val countDownTime = 60
    private var isTimerRunning = false
    private lateinit var newTask: NewTask
    private lateinit var customRecyclerAdapter: RecyclerAdapter
    private lateinit var alertDialogBuilder: AlertDialog.Builder
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var databaseHandler: DatabaseHandler
    private lateinit var activityMainBinding: ActivityMainBinding
    private var taskArrayList: ArrayList<TaskModal> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)


        with(activityMainBinding) {

            databaseHandler = DatabaseHandler(this@MainActivity)
            databaseHandler.getAllData()
            newTask = NewTask(this@MainActivity, recyclerView)
            alertDialogBuilder = AlertDialog.Builder(this@MainActivity)
            customRecyclerAdapter = RecyclerAdapter(context = applicationContext, activity = this@MainActivity,
                mode = mode, taskTextView = taskText, taskArrayList = taskArrayList)


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
                                time = 3000 // 300.000
                                taskText.visibility = View.GONE
                                customRecyclerAdapter.changeMode(mode)
                            }
                        }
                        R.id.pomodoroButton -> {
                            if (!isTimerRunning) {
                                checkPomodoroButton()
                                mode = "Pomodoro"
                                taskText.visibility = View.VISIBLE
                                customRecyclerAdapter.changeMode(mode)
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
                                time = 3000 // 900.000
                                taskText.visibility = View.GONE
                                customRecyclerAdapter.changeMode(mode)
                            }
                        }
                    }
                }

            }

            startButton.setOnClickListener {
                isTimerRunning = true
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
                if (!isTimerRunning) {
                    newTask.showBottomSheet()
                    recyclerView.adapter?.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@MainActivity, "You can't do this while the timer is running.", Toast.LENGTH_SHORT).show()
                }
            }

            refresh.setOnClickListener {
                refreshTimer(mode)
                startButton.visibility = View.VISIBLE
                pauseButton.visibility = View.INVISIBLE
            }

            taskArrayList = newTask.loadData()
            customRecyclerAdapter = RecyclerAdapter(context = applicationContext, activity = this@MainActivity,
                mode = mode, taskTextView = taskText, taskArrayList = taskArrayList)
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
                    val alertDialog = alertDialogBuilder.create()
                    var view: View ?= null
                    var button: Button ?= null

                    if (mode == "Pomodoro") {
                        view = layoutInflater.inflate(R.layout.congrats_dialog, null)
                        button = view.findViewById<Button>(R.id.completeButton)
                    } else {
                        view = layoutInflater.inflate(R.layout.break_dialog, null)
                        button = view.findViewById<Button>(R.id.letsButton)
                    }

                    chronometer.text = "00:00"
                    settings()
                    alertDialog.setView(view)
                    alertDialog.setCancelable(false)
                    button.setOnClickListener {
                        if (mode == "Pomodoro") {
                            if (activityMainBinding.taskText.text != "") {
                                val taskObject = findObject(taskText.text.toString(), taskArrayList)
                                taskObject?.let {
                                    val increaseGoal = taskObject.taskModal.doneGoal + 1
                                    val newObject = TaskModal(taskObject.taskModal.userTask, increaseGoal, taskObject.taskModal.studyNumber, taskObject.taskModal.check)
                                    databaseHandler.updateData(newObject)
                                    taskArrayList[taskObject.position] = newObject
                                    recyclerView.adapter?.notifyItemChanged(taskObject.position)
                                }
                            }
                        }
                        activityMainBinding.startButton.visibility = View.VISIBLE
                        activityMainBinding.pauseButton.visibility = View.GONE
                        toggleGroup.check(R.id.pomodoroButton)
                        checkPomodoroButton()
                        progressBar.progress = 0
                        pauseButton.visibility = View.GONE
                        startButton.visibility = View.VISIBLE
                        alertDialog.dismiss()
                    }
                    alertDialog.show()

                }
            }.start()

        }
    }

    private fun pauseTimer() {
        countDownTimer.cancel()
        settings()
        val currentTime = activityMainBinding.chronometer.text.split(":")
        val minute = currentTime[0].toInt()
        val second = currentTime[1].toInt()
        time = if (currentTime[0] != "00") ((minute * 60) + second) * 1000
        else second * 1000
    }

    private fun refreshTimer(mode: String) {
        countDownTimer.cancel()
        settings()
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
        time = 5000 //1500000
    }

    private fun findObject(task: String, array: ArrayList<TaskModal>): ItemModal? {
        for (taskObject in array) {
            if  (taskObject.userTask == task) {
                val index = array.indexOf(taskObject)
                return ItemModal(taskObject, index)
            }
        }
        return null
    }

    private fun settings() {
        isTimerRunning = false
        activityMainBinding.shortBreakButton.isClickable = true
        activityMainBinding.pomodoroButton.isClickable = true
        activityMainBinding.longBreakButton.isClickable = true
    }

}

