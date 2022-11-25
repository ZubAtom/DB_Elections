package com.example.db_elections.model

import com.example.db_elections.view_model.BaseViewModel

interface FindInput{
    fun input(): Election?
}

class ConsoleFindInput constructor(
    private val baseInput: BaseInput,
): FindInput {
    override fun input(): Election? = baseInput.noteInput(false)
}

interface FindLogic {
    fun logic (election: Election?, list: MutableList<Election>):List<Election>
}

class BaseFindLogic : FindLogic {
    override fun logic (election: Election?, list: MutableList<Election>) =
        list
            .filter { if (election?.location != null) it.location == election.location else true }
            .filter { if (election?.candidate != null) it.candidate == election.candidate else true }
            .filter { if (election?.quantityOfVotes != null) it.quantityOfVotes == election.quantityOfVotes else true }
}

interface FindOutput{
    fun output(list:List<Election>)
}

class ConsoleFindOutput constructor(
    private val print: PrintElections
): FindOutput {
    override fun output(list:List<Election>){
        print.printElections(list)
    }
}

interface Find: Action {
    override fun action (list: MutableList<Election>):Int
}

class ConsoleFind constructor(
    private val baseInput: BaseInput,
    private val print: PrintElections
): Find {
    override fun action(list: MutableList<Election>): Int {
        val input = ConsoleFindInput(baseInput)
        val logic = BaseFindLogic()
        val output = ConsoleFindOutput(print)
        output.output(logic.logic(input.input(),list))
        return 0
    }
}

class AndroidFind : Find {
    override fun action(list: MutableList<Election>): Int {
        StorageState.instance.action = Actions.FIND
        StorageState.instance.findState++
        StorageState.instance.election = null
        BaseViewModel.FragmentTransit.instance.goToInputFragment()
        return 0
    }
}