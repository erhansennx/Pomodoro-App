package com.erhansen.pomodoro.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.erhansen.pomodoro.R
import com.erhansen.pomodoro.database.DatabaseHandler
import com.erhansen.pomodoro.model.TaskModal
import kotlinx.coroutines.NonDisposableHandle.parent

class RecyclerAdapter(private val context: Context, private val activity: Activity, private var mode: String,private var taskTextView: TextView, private val taskArrayList: ArrayList<TaskModal>) : RecyclerView.Adapter<RecyclerAdapter.ItemHolder>(){

    private var selectedItem: Int ?= null
    private var alertDialogBuilder = AlertDialog.Builder(activity)
    private var databaseHandler = DatabaseHandler(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_items, parent, false)
        return ItemHolder(view)
    }

    override fun getItemCount(): Int {
        return taskArrayList.size
    }

    override fun onBindViewHolder(holder: ItemHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.taskTextView.text = taskArrayList[position].userTask
        holder.studyNumberText.text = "${taskArrayList[position].doneGoal}/${taskArrayList[position].studyNumber}"
        if (!taskArrayList[position].check) holder.checkImageView.visibility = View.GONE
        holder.itemView.setOnLongClickListener {
            if (taskArrayList[position].doneGoal != taskArrayList[position].studyNumber) {
                if (mode == "Pomodoro") {
                    when (selectedItem) {
                        null -> {
                            selectedItem = position
                            taskArrayList[position].check = true
                            holder.checkImageView.visibility = View.VISIBLE
                            taskTextView.visibility = View.VISIBLE
                            taskTextView.text = taskArrayList[position].userTask
                        }
                        position -> {
                            taskArrayList[selectedItem!!].check = false
                            holder.checkImageView.visibility = View.GONE
                            taskTextView.visibility = View.GONE
                            selectedItem = null
                            taskTextView.text = ""
                        }
                        else -> {
                            taskArrayList[selectedItem!!].check = false
                            notifyItemChanged(selectedItem!!)
                            selectedItem = position
                            taskArrayList[selectedItem!!].check = true
                            taskTextView.text = taskArrayList[position].userTask
                            holder.checkImageView.visibility = View.VISIBLE
                        }
                    }
                } else {
                    Toast.makeText(activity, "You can't choose it the break.", Toast.LENGTH_SHORT).show()
                }
            } else {
                //holder.checkImageView.visibility = View.GONE
                //taskArrayList[position].check = false
                //taskTextView.text = ""
                Toast.makeText(activity, "You have reached your goal. You can't choose this.", Toast.LENGTH_SHORT).show()
            }

            true
        }

        holder.deleteMenu.setOnClickListener {
            with(alertDialogBuilder) {
                setTitle("Delete Task")
                setMessage("Are you sure you want to delete ${taskArrayList[position].userTask} task?")
                setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                    databaseHandler.deleteData(taskArrayList[position])
                    taskArrayList.remove(taskArrayList[position])
                    notifyItemRemoved(position)
                    taskTextView.text = ""
                })
                setNegativeButton("No, Thanks", DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
                create()
                show()
            }

        }
    }

    fun changeMode(changedMode: String) {
        mode = changedMode
    }

    class ItemHolder(itemView: View) : ViewHolder(itemView) {
        val checkImageView: ImageView = itemView.findViewById(R.id.checkImage)
        val taskTextView: TextView = itemView.findViewById(R.id.taskTextView)
        val studyNumberText: TextView = itemView.findViewById(R.id.studyNumberText)
        val deleteMenu: ImageView = itemView.findViewById(R.id.deleteMenu)



    }

}