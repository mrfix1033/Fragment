package ru.mrfix1033.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotesFragment : Fragment() {
    private lateinit var databaseManager: DatabaseManager
    private val notes = mutableListOf<Note>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: RecyclerView.Adapter<NoteHolder>
    private lateinit var editTextNote: EditText

    private val dateFormatter = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
    private val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        editTextNote = view.findViewById(R.id.editTextNote)

        (activity as AppCompatActivity).setSupportActionBar(view.findViewById(R.id.toolbar))

        view.findViewById<Button>(R.id.buttonAdd).setOnClickListener {
            val text = editTextNote.text.trim()
            if (text.isEmpty()) return@setOnClickListener

            val note = Note(-1, text.toString(), -1)
            note.id = databaseManager.insert(note, true)!!
            notes.add(note)
            recyclerViewAdapter.notifyItemInserted(notes.size - 1)

            editTextNote.text.clear()
        }

        view.findViewById<Button>(R.id.buttonLoad).setOnClickListener {
            loadNotes()
        }

        recyclerViewAdapter = object : RecyclerView.Adapter<NoteHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
                return NoteHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.note_item, parent, false)
                )
            }

            override fun getItemCount() = notes.size

            override fun onBindViewHolder(holder: NoteHolder, position: Int) {
                val note = notes[position]
                holder.run {
                    textViewId.setText(note.id.toString())
                    textViewText.setText(note.text)
                    checkBox.isChecked = note.isDone()
                    if (note.isDone()) {
                        textViewDate.setText(dateFormatter.format(note.doneTime))
                        textViewTime.setText(timeFormatter.format(note.doneTime))
                    } else {
                        textViewDate.setText("")
                        textViewTime.setText("")
                    }

                    checkBox.setOnCheckedChangeListener { checkBoxView, isChecked ->
                        if (isChecked) {
                            note.doneTime = Date().time
                            textViewDate.setText(dateFormatter.format(note.doneTime))
                            textViewTime.setText(timeFormatter.format(note.doneTime))
                        } else {
                            textViewDate.setText("")
                            textViewTime.setText("")
                            note.doneTime = -1
                        }
                        databaseManager.update(note)
                    }
                }
            }
        }
        recyclerView.adapter = recyclerViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        databaseManager = DatabaseManager(view.context, null)
        loadNotes()
    }

    private fun loadNotes() {
        notes.clear()
        databaseManager.select {
            while (it.moveToNext()) {
                notes.add(
                    Note(
                        it.getInt(it.getColumnIndex(DatabaseManager.Key.ID)),
                        it.getString(it.getColumnIndex(DatabaseManager.Key.TEXT)),
                        it.getLong(it.getColumnIndex(DatabaseManager.Key.DONE_TIME))
                    )
                )
            }
        }
        recyclerViewAdapter.notifyDataSetChanged()
    }
}