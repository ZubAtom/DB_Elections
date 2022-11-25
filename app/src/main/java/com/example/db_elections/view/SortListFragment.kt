package com.example.db_elections.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.db_elections.view_model.BaseViewModel
import com.example.db_elections.R


class SortListFragment : Fragment() {

    private val viewModel: BaseViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sort_list, container, false)
    }

    override fun onStart() {
        super.onStart()
        val recyclerView = view?.findViewById<RecyclerView>(R.id.sortRecyclerView)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = BaseViewModel.SortsAdapter()
        BaseViewModel.SortListFragmentViewModel().fragment(parentFragmentManager)
        view?.findViewById<Button>(R.id.backSortListButton)?.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}


