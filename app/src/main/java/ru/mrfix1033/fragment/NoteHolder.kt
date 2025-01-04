package ru.mrfix1033.fragment

import android.view.View
import android.widget.CheckBox
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class NoteHolder(itemView: View) : ViewHolder(itemView) {
    val main: RelativeLayout = itemView.findViewById(R.id.main)
    val textViewId: TextView = itemView.findViewById(R.id.textViewId)
    val textViewText: TextView = itemView.findViewById(R.id.textViewText)
    val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
    val textViewTime: TextView = itemView.findViewById(R.id.textViewTime)
    val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
}