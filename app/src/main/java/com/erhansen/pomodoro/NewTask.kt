package com.erhansen.pomodoro

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton

class NewTask(private val context: Context) {


    private var currentValue = 0


    @SuppressLint("MissingInflatedId")
    fun showBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(context)
        val layoutInflater = LayoutInflater.from(context)
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        val upButton = bottomSheetView.findViewById<MaterialButton>(R.id.upButton)
        val downButton = bottomSheetView.findViewById<MaterialButton>(R.id.downButton)
        val valueText = bottomSheetView.findViewById<TextView>(R.id.valueText)
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
    }


}