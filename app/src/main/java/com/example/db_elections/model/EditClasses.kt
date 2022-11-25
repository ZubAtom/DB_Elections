package com.example.db_elections.model

interface EditInput{
    fun input(): Election?
}

class ConsoleEditInput constructor(
    private val input: BaseInput,
): EditInput {
    override fun input(): Election? = input.noteInput()
}

interface EditCheck{
    fun check(oldElection: Election?, list: MutableList<Election>): Message
}

class BaseEditCheck : EditCheck {
    override fun check(oldElection: Election?, list: MutableList<Election>): Message =
        if (oldElection != null) {
            if (list.contains(oldElection)) Message.RECORD_CORRECT
            else Message.RECORD_NOT_EXIST
        } else Message.RECORD_INCORRECT
}

interface EditLogic {
    fun logic (oldElection: Election?, newElection: Election?, list: MutableList<Election>): Message
}

class BaseEditLogic : EditLogic {
    override fun logic (oldElection: Election?, newElection: Election?, list: MutableList<Election>) =
        if (newElection!=null) {
            val tempList=list.filter { it!=oldElection }
            if (tempList.find { it.location==newElection.location && it.candidate==newElection.candidate } == null) {
                list[list.indexOf(oldElection)] = newElection
                Message.ACTION_COMPLETED
            } else Message.RECORD_EXISTS
        } else Message.RECORD_INCORRECT
}

interface EditOutput{
    fun output(mes: Message)
}

class ConsoleEditOutput constructor(
    private val printMessage: PrintMessage
): EditOutput {
    override fun output(mes: Message){
        printMessage.printMessage(mes)
    }
}

interface Edit: Action {
    override fun action (list: MutableList<Election>):Int
}

class ConsoleEdit constructor(
    private val baseInput: BaseInput,
    private val printMessage: PrintMessage
): Edit {
    override fun action(list: MutableList<Election>): Int {
        val input = ConsoleEditInput(baseInput)
        val check = BaseEditCheck()
        val output = ConsoleEditOutput(printMessage)
        val election = input.input()
        val checkResult = check.check(election,list)
        output.output(checkResult)
        if (checkResult== Message.RECORD_CORRECT){
            val logic = BaseEditLogic()
            output.output(logic.logic(election, input.input(), list))
        }
        return 0
    }
}

class AndroidEdit : Edit {
    override fun action(list: MutableList<Election>): Int {
        return 0
    }
}