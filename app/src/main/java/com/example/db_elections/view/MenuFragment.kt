package com.example.db_elections.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.db_elections.*
import com.example.db_elections.view_model.BaseViewModel


class MenuFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onStart() {
        super.onStart()
        val recyclerView = view?.findViewById<RecyclerView>(R.id.menuRecyclerView)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = BaseViewModel.MenuAdapter()

        BaseViewModel.MenuFragmentViewModel().fragment(parentFragmentManager)


    }
}




