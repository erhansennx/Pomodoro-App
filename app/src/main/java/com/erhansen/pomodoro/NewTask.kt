package com.erhansen.pomodoro

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.erhansen.pomodoro.model.TaskModal
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class NewTask(private val context: Context, private val recyclerView: RecyclerView) {

    private var userTask = ""
    private var currentValue = 1
    //private val pomodoroDatabase: PomodoroDatabase? = PomodoroDatabase.getPomodoroDB(context)
    private val taskArrayList: ArrayList<TaskModal> = arrayListOf()

    @SuppressLint("MissingInflatedId") // ignores errors
    fun showBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
        val layoutInflater = LayoutInflater.from(context)
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        val upButton = bottomSheetView.findViewById<MaterialButton>(R.id.upButton)
        val downButton = bottomSheetView.findViewById<MaterialButton>(R.id.downButton)
        val saveButton = bottomSheetView.findViewById<Button>(R.id.saveButton)
        val valueText = bottomSheetView.findViewById<TextView>(R.id.valueText)
        val taskEditText = bottomSheetView.findViewById<TextInputEditText>(R.id.taskEditText)
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()

        upButton.setOnClickListener {
            currentValue++
            valueText.text = "$currentValue"
        }

        downButton.setOnClickListener {
            if (valueText.text.toString().toInt() > 1) {
                currentValue--
                valueText.text = "$currentValue"
            }
        }

        saveButton.setOnClickListener {
            userTask = taskEditText.text.toString()
            if  (userTask.isNotEmpty()) {
                val taskModal = TaskModal(userTask, currentValue)
                taskArrayList.add(taskModal)
                recyclerView.adapter?.notifyDataSetChanged()
                saveData(userTask, currentValue)
            }
            currentValue = 1
            bottomSheetDialog.cancel()
        }

    }

    fun loadData(): ArrayList<TaskModal> {
        return taskArrayList
    }

    private fun saveData(taskName: String, taskStudyNumber: Int) {
        //pomodoroDatabase?.pomodoroDao()?.saveTask(PomodoroEntity(taskName = taskName, taskStudyNumber = taskStudyNumber))
    }

}