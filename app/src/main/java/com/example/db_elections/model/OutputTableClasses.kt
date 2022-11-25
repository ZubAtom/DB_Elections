package com.example.db_elections.model

interface OutputTable: Action {
    override fun action (list: MutableList<Election>):Int
}

class ConsoleOutputTable constructor(
    private val print: PrintElections
): OutputTable {
    override fun action(list: MutableList<Election>): Int {
        print.printElections(list)
        return 0
    }
}