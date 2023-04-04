package com.erhansen.pomodoro.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.erhansen.pomodoro.R
import com.erhansen.pomodoro.model.TaskModal

class RecyclerAdapter(private val context: Context, private var taskTextView: TextView, private val taskArrayList: ArrayList<TaskModal>) : RecyclerView.Adapter<RecyclerAdapter.ItemHolder>(){


    private var selectedItem: Int ?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_items, parent, false)
        return ItemHolder(view)
    }

    override fun getItemCount(): Int {
        return taskArrayList.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.taskTextView.text = taskArrayList[position].userTask
        holder.studyNumberText.text = "0/${taskArrayList[position].studyNumber}"
        if (!taskArrayList[position].check) holder.checkImageView.visibility = View.GONE
        holder.itemView.setOnLongClickListener {
            if (selectedItem == null) {
                selectedItem = position
                taskArrayList[position].check = true
                holder.checkImageView.visibility = View.VISIBLE
                taskTextView.visibility = View.VISIBLE
                taskTextView.text = taskArrayList[position].userTask
            } else if (selectedItem == position) {
                taskArrayList[selectedItem!!].check = false
                holder.checkImageView.visibility = View.GONE
                taskTextView.visibility = View.GONE
                selectedItem = null
            } else {
                taskArrayList[selectedItem!!].check = false
                notifyItemChanged(selectedItem!!)
                selectedItem = position
                taskArrayList[selectedItem!!].check = true
                taskTextView.text = taskArrayList[position].userTask
                holder.checkImageView.visibility = View.VISIBLE
            }
            true
        }
    }

    class ItemHolder(itemView: View) : ViewHolder(itemView) {
        val checkImageView: ImageView = itemView.findViewById(R.id.checkImage)
        val taskTextView: TextView = itemView.findViewById(R.id.taskTextView)
        val studyNumberText: TextView = itemView.findViewById(R.id.studyNumberText)
    }

}