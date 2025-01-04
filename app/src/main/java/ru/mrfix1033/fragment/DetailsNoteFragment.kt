package ru.mrfix1033.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

class DetailsNoteFragment : Fragment(), OnFragmentDataListener {
    private lateinit var onFragmentDataListener: OnFragmentDataListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        onFragmentDataListener = requireActivity() as OnFragmentDataListener
        val view = inflater.inflate(R.layout.fragment_details_note, container, false)
        val editText: TextView = view.findViewById(R.id.editText)
        editText.setText(requireArguments().getString("text"))
        view.findViewById<Button>(R.id.buttonSave).setOnClickListener {
            onData(requireArguments().getInt("position"), editText.text.toString())
        }
        view.findViewById<Button>(R.id.buttonDeleteNote).setOnClickListener {
            onData(requireArguments().getInt("position"), "")
        }
        return view
    }

    override fun onData(position: Int, text: String) {
        val bundle = Bundle()
        bundle.putString("text", text)
        bundle.getInt("position", position)

        val fragment = NotesFragment()
        fragment.arguments = bundle

        val transition = parentFragmentManager.beginTransaction()
        transition.replace(R.id.fragmentContainerView, fragment)
        transition.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
        transition.addToBackStack(null)

        transition.commit()
    }
}