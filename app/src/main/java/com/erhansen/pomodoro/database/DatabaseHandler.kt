package com.erhansen.pomodoro.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import com.erhansen.pomodoro.model.TaskModal

class DatabaseHandler(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "Tasks"
        private const val TABLE_NAME = "task"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TASK = "task"
        private const val COLUMN_DONE_GOAL = "doneGoal"
        private const val COLUMN_GOAL = "goal"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE $TABLE_NAME($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_TASK VARCHAR, $COLUMN_DONE_GOAL INTEGER, $COLUMN_GOAL INTEGER)")
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addData(task: String, goal: Int) {
        try {
            val db = this.writableDatabase
            val values = ContentValues()
            values.put(COLUMN_TASK, task)
            values.put(COLUMN_DONE_GOAL, 0)
            values.put(COLUMN_GOAL, goal)
            db.insert(TABLE_NAME, null, values)
            db.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Tasks could not be added!", Toast.LENGTH_SHORT).show()
        }

    }

    @SuppressLint("Range")
    fun getAllData() : ArrayList<TaskModal> {
        try {
            val taskList = ArrayList<TaskModal>()
            val query = "SELECT * FROM $TABLE_NAME"
            val db = this.readableDatabase
            val cursor = db.rawQuery(query, null)

            while (cursor.moveToNext()) {
                val cursorTask = cursor.getString(cursor.getColumnIndex(COLUMN_TASK))
                val cursorDoneGoal = cursor.getInt(cursor.getColumnIndex(COLUMN_DONE_GOAL))
                val cursorGoal = cursor.getInt(cursor.getColumnIndex(COLUMN_GOAL))
                val task = TaskModal(cursorTask, cursorDoneGoal, cursorGoal,false)
                taskList.add(task)
            }

            cursor.close()
            db.close()

            return taskList
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Tasks could not be loaded!", Toast.LENGTH_SHORT).show()
            return arrayListOf()
        }
    }

    fun deleteData(task: TaskModal) {
        try {
            val db = this.writableDatabase
            db.delete(TABLE_NAME, "$COLUMN_TASK = ?", arrayOf(task.userTask))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Task could not be deleted!", Toast.LENGTH_SHORT).show()
        }
    }

    fun updateData(task: TaskModal) {
        try {
            val db = this.writableDatabase
            val values = ContentValues()
            values.put("task", task.userTask)
            values.put("doneGoal", task.doneGoal)
            values.put("goal", task.studyNumber)
            db.update(TABLE_NAME, values, "$COLUMN_TASK = ?", arrayOf(task.userTask))
            db.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Task could not be updated!", Toast.LENGTH_SHORT).show()
        }
    }



}