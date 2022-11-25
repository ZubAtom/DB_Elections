package com.example.db_elections.view_model

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.db_elections.R
import com.example.db_elections.model.*
import com.example.db_elections.view.InputFragment
import com.example.db_elections.view.SortListFragment

class BaseViewModel: ViewModel() {

    val activityState = ActivityState.instance

    class ElectionsAdapter(
        private val list: MutableList<Election>,
        val updateAdapter: () -> Unit
    ) : RecyclerView.Adapter<ElectionsAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val locationView: TextView
            val candidateView: TextView
            val votesView: TextView
            val editButton: Button
            val deleteButton: Button

            init {
                locationView = view.findViewById(R.id.recyclerTextViewLocation)
                candidateView = view.findViewById(R.id.recyclerTextViewCandidate)
                votesView = view.findViewById(R.id.recyclerTextViewVotes)
                editButton = view.findViewById(R.id.recyclerEditButton)
                deleteButton = view.findViewById(R.id.recyclerDeleteButton)

            }
        }

        override fun getItemCount(): Int = list.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_election, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val election = list[position]
            holder.locationView.text = list[position].location
            holder.candidateView.text = list[position].candidate
            holder.votesView.text = list[position].quantityOfVotes.toString()

            holder.editButton.setOnClickListener {
                val electionList = StorageState.instance
                electionList.election = election
                electionList.position = position
                val button = StorageState.instance
                button.action = Actions.EDIT
                FragmentTransit.instance.goToInputFragment()
            }

            holder.deleteButton.setOnClickListener {
                BaseDelLogic().logic(election, StorageState.instance.list)
                updateAdapter()
            }
        }
    }

    class SortsAdapter : RecyclerView.Adapter<SortsAdapter.ViewHolder>() {
        private val list = AndroidSortList().getSortingList()

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val chosenButton: Button

            init {
                chosenButton = view.findViewById(R.id.chosenButton)
            }
        }

        override fun getItemCount(): Int = list.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_buttons, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.chosenButton.text = ActivityState.instance.createString(list[position].resources)

            holder.chosenButton.setOnClickListener {
                BaseWorkSortLogic().work(StorageState.instance.list, list[position].func)
                RecyclerManager.instance.updateRecycler()
            }
        }
    }

    class MenuAdapter : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {
        private val createFullMenuNote: CreateExtendedMenuNote = ExtendedCreateFullMenuNote()
        private val list = SecondMenuList(createFullMenuNote).createList()

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val chosenButton: Button

            init {
                chosenButton = view.findViewById(R.id.chosenButton)
            }
        }

        override fun getItemCount(): Int = list.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_buttons, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.chosenButton.text =
                ActivityState.instance.createString(StringResources().getString(list[position].res))

            holder.chosenButton.setOnClickListener {
                list[position].action.action(StorageState.instance.list)
            }
        }
    }

    class RecyclerManager(
        var recyclerView: RecyclerView? = null,
        var list: MutableList<Election> = mutableListOf()
    ) {
        fun updateRecycler() {
            recyclerView?.adapter =
                ElectionsAdapter(
                    StorageState.instance.list.intersect(list.toSet()).toMutableList(),
                    ::updateRecycler
                )
        }

        companion object {
            val instance by lazy { RecyclerManager() }
        }
    }

    class FragmentTransit {

        var fragmentManager: FragmentManager? = null

        fun goToInputFragment() {
            fragmentManager?.commit {
                replace<InputFragment>(R.id.fragmentContainerView)
                setReorderingAllowed(true)
                addToBackStack(null)
            }
        }

        fun goToSortFragment() {
            fragmentManager?.commit {
                replace<SortListFragment>(R.id.fragmentContainerView)
                setReorderingAllowed(true)
                addToBackStack(null)
            }
        }

        companion object {
            val instance by lazy { FragmentTransit() }
        }
    }

    class InputFragmentViewModel{
        private val recyclerManager = RecyclerManager.instance
        private val fragmentTransit = FragmentTransit.instance
        private val storage = StorageState.instance
        private val addLogic = BaseAddLogic()
        private val editLogic = BaseEditLogic()
        private val findLogic = BaseFindLogic()
        private val checkLoc = SimpleCheckersLocation()
        private val checksCan = SimpleCheckersCandidate()
        private val checkVotes = SimpleCheckersVotes()
        private val election = SimpleElectionFactory()
        private val processing = BaseInputProcessing(election, checkLoc, checksCan, checkVotes)

        fun primaryCalculation() {
            when (storage.findState){
                0,2 -> recyclerManager.list =mutableListOf()
                1 -> {
                    storage.action= Actions.FIND
                    recyclerManager.list =storage.list
                }
            }
            if (storage.findState>0) storage.findState++
            if (storage.findState==3) storage.findState=1
        }

        fun checkElection () =
            if (storage.election!=null&&storage.action== Actions.EDIT) storage.election
            else null

        fun fragment (fragmentManager: FragmentManager){
            fragmentTransit.fragmentManager=fragmentManager
            recyclerManager.updateRecycler()
        }

        fun actionCalculation(loc:String?, can: String?, votes:String?):Message?{
            var mes:Message? = null
            when (storage.action) {
                Actions.ADD -> mes = addLogic.logic(processing.processing(loc, can, votes, true), storage.list)
                Actions.FIND -> {
                    storage.foundList = findLogic.logic(processing.processing(loc, can, votes, false),storage.list).toMutableList()
                    recyclerManager.list = storage.foundList
                    recyclerManager.updateRecycler()
                }
                Actions.EDIT -> {
                    val newElection = processing.processing(loc, can, votes, true)
                    mes = editLogic.logic(storage.election, newElection, storage.list)
                    if (mes == Message.ACTION_COMPLETED)storage.election = newElection
                }
                Actions.EXIT, Actions.SORT, Actions.DELETE, null -> {}
            }
            return mes
        }
    }

    class MenuFragmentViewModel{
        private val recyclerManager = RecyclerManager.instance
        private val fragmentTransit = FragmentTransit.instance
        private val storage = StorageState.instance

        fun fragment (fragmentManager: FragmentManager){
            storage.findState=0
            fragmentTransit.fragmentManager=fragmentManager
            recyclerManager.list = storage.list
            recyclerManager.updateRecycler()
        }
    }

    class SortListFragmentViewModel{
        private val recyclerManager = RecyclerManager.instance
        private val fragmentTransit = FragmentTransit.instance
        private val storage = StorageState.instance


        fun fragment(fragmentManager: FragmentManager){
            fragmentTransit.fragmentManager = fragmentManager
            recyclerManager.list = storage.list
            recyclerManager.updateRecycler()

        }
    }

    class ActivityState {

        private var activity: Activity? = null

        fun createString(res: Int): String? = activity?.getString(res)

        fun finishActivity() {
            activity?.finish()
        }

        fun createRecycler(activity: Activity, recyclerView: RecyclerView){
            this.activity = activity
            val recyclerManager = RecyclerManager.instance
            recyclerManager.list= StorageState.instance.list
            recyclerManager.recyclerView = recyclerView
            recyclerManager.updateRecycler()
        }

        companion object {
            val instance by lazy { ActivityState() }
        }
    }



    class StringResources {

        fun getString(res: Actions? = null): Int {
            return when (res ?: StorageState.instance.action) {
                Actions.ADD -> R.string.add
                Actions.DELETE -> R.string.delete
                Actions.EDIT -> R.string.edit
                Actions.FIND -> R.string.find
                Actions.SORT -> R.string.sort
                Actions.EXIT -> R.string.exit
                null -> 0
            }
        }

        fun getMessage(mes: Message) =
        when (mes) {
            Message.RECORD_INCORRECT -> R.string.record_incorrect
            Message.RECORD_EXISTS -> R.string.record_exists
            Message.RECORD_NOT_EXIST ->R.string.record_not_exists
            Message.ACTION_COMPLETED -> R.string.action_completed
            Message.RECORD_CORRECT -> R.string.record_correct
        }
    }
}