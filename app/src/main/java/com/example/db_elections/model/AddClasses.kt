package com.example.db_elections.model

import com.example.db_elections.view_model.BaseViewModel


interface AddInput{
    fun input(): Election?
}

class ConsoleAddInput constructor(
    private val baseInput: BaseInput,
) : AddInput {
    override fun input(): Election? = baseInput.noteInput()
}

interface AddLogic {
    fun logic (election: Election?, list: MutableList<Election>): Message
}

class BaseAddLogic : AddLogic {
    override fun logic (election: Election?, list: MutableList<Election>) =
        if (election != null) {
            if (list.find { it.location == election.location && it.candidate==election.candidate }==null) {
                list.add(election)
                Message.ACTION_COMPLETED
            }
            else Message.RECORD_EXISTS
        } else Message.RECORD_INCORRECT

}

interface AddOutput{
    fun output(mes: Message)
}

class ConsoleAddOutput (
    private val  printMessage: PrintMessage
) : AddOutput {
    override fun output(mes: Message){
        printMessage.printMessage(mes)
    }
}

interface Add: Action {
    override fun action (list: MutableList<Election>):Int
}

class ConsoleAdd constructor(
    private val baseInput: BaseInput,
    private val printMessage: PrintMessage,
): Add {
    override fun action(list: MutableList<Election>): Int {
        val input = ConsoleAddInput(baseInput)
        val logic = BaseAddLogic()
        val output = ConsoleAddOutput(printMessage)
        output.output(logic.logic(input.input(),list))
        return 0
    }
}

class AndroidAdd : Add {
    override fun action(list: MutableList<Election>): Int {
        StorageState.instance.action = Actions.ADD
        StorageState.instance.election = null
        BaseViewModel.FragmentTransit.instance.goToInputFragment()
        return 0
    }
}