package com.example.db_elections.model

interface DelInput{
    fun input(): Election?
}

class ConsoleDelInput constructor(
    private val baseInput: BaseInput,
): DelInput {
    override fun input(): Election? = baseInput.noteInput()
}

interface DelLogic {
    fun logic (election: Election?, list: MutableList<Election>): Message
}

class BaseDelLogic : DelLogic {
    override fun logic (election: Election?, list: MutableList<Election>) =
        if (election != null) {
            if (list.contains(election)) {
                list.remove(election)
                Message.ACTION_COMPLETED
            } else Message.RECORD_NOT_EXIST
        } else Message.RECORD_INCORRECT
}

interface DelOutput{
    fun output(mes: Message)
}

class ConsoleDelOutput constructor(
    private val printMessage: PrintMessage
): DelOutput {
    override fun output(mes: Message){
        printMessage.printMessage(mes)
    }
}

interface Del: Action {
    override fun action (list: MutableList<Election>):Int
}

class ConsoleDel constructor(
    private val baseInput: BaseInput,
    private val printMessage: PrintMessage
): Del {
    override fun action(list: MutableList<Election>): Int {
        val input = ConsoleDelInput(baseInput)
        val logic = BaseDelLogic()
        val output = ConsoleDelOutput(printMessage)
        output.output(logic.logic(input.input(),list))
        return 0
    }
}

class AndroidDel : Del {
    override fun action(list: MutableList<Election>): Int {
        return 0
    }
}