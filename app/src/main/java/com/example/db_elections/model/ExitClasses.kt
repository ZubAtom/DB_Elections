package com.example.db_elections.model

import com.example.db_elections.view_model.BaseViewModel

interface Exit: Action {
    override fun action (list: MutableList<Election>):Int
}

class SimpleExit : Exit {
    override fun action(list: MutableList<Election>) = 1
}

class AndroidExit : Exit {
    override fun action(list: MutableList<Election>) :Int{
        BaseViewModel.ActivityState.instance.finishActivity()
        return 1
    }
}