package com.erhansen.pomodoro.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.erhansen.pomodoro.databinding.RecyclerItemsBinding
import com.erhansen.pomodoro.model.TaskModal

class RecyclerAdapter(private val context: Context, private val taskArrayList: ArrayList<TaskModal>) : RecyclerView.Adapter<RecyclerAdapter.ItemHolder>(){

    private lateinit var binding: RecyclerItemsBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        binding = RecyclerItemsBinding.inflate(LayoutInflater.from(context), parent, false)
        return ItemHolder(binding)
    }

    override fun getItemCount(): Int {
        return taskArrayList.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        binding.taskTextView.text = taskArrayList[position].userTask
        binding.studyNumberText.text = "0/${taskArrayList[position].studyNumber}"
    }

    class ItemHolder(val binding: RecyclerItemsBinding) : ViewHolder(binding.root) {}

}