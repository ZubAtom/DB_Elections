package com.example.db_elections.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.db_elections.*
import com.example.db_elections.view_model.BaseViewModel

class InputFragment: Fragment(R.layout.fragment_input) {
    private val viewModel = BaseViewModel.InputFragmentViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_input, container, false)
    }

    override fun onStart() {
        super.onStart()
        val locationTextView = view?.findViewById<TextView>(R.id.editTextLocation)
        val candidateTextView = view?.findViewById<TextView>(R.id.editTextCandidate)
        val votesTextView = view?.findViewById<TextView>(R.id.editTextVotes)

        viewModel.primaryCalculation()

        val election = viewModel.checkElection()
        if (election!=null)
        {
            locationTextView?.text = election.location
            candidateTextView?.text = election.candidate
            votesTextView?.text = election.quantityOfVotes.toString()
        }

        viewModel.fragment(parentFragmentManager)

        val button = view?.findViewById<Button>(R.id.actionInputButton)
        button?.text= getString(BaseViewModel.StringResources().getString())
        button?.setOnClickListener {
            val location = locationTextView?.text.toString()
            val candidate = candidateTextView?.text.toString()
            val votes = votesTextView?.text.toString()
            val mes = viewModel.actionCalculation(location,candidate,votes)
            if (mes!=null)
                view?.findViewById<TextView>(R.id.messageTextView)?.text = getString(BaseViewModel.StringResources().getMessage(mes))
        }
        view?.findViewById<Button>(R.id.backInputButton)?.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

}