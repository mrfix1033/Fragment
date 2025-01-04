package ru.mrfix1033.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotesFragment : Fragment() {
    private lateinit var databaseManager: DatabaseManager
    private val notes = mutableListOf<Note>()
    private lateinit var onFragmentDataListener: OnFragmentDataListener

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: RecyclerView.Adapter<NoteHolder>
    private lateinit var editTextNote: EditText

    private val dateFormatter = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
    private val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notes, container, false)

        onFragmentDataListener = requireActivity() as OnFragmentDataListener
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

                    main.setOnClickListener {
                        onFragmentDataListener.onData(position, note.text)
                    }
                }
            }
        }
        recyclerView.adapter = recyclerViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        databaseManager = DatabaseManager(view.context, null)
        loadNotes()

        return view
    }

    override fun onResume() {
        super.onResume()
        if (arguments == null)  // because onResume calls after fragment created
            return
        val arguments = requireArguments()
        val position = arguments.getInt("position")

        val note = notes[position]
        val text = arguments.getString("text")!!
        if (text.isEmpty()) {
            notes.removeAt(position)
            recyclerViewAdapter.notifyItemRemoved(position)
            databaseManager.delete(note.id)
            return
        }
        note.text = text
        recyclerViewAdapter.notifyItemChanged(position)
        databaseManager.update(note)
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
        recyclerViewAdapter.notifyItemRangeChanged(0, notes.size)
    }
}