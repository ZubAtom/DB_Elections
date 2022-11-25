package com.example.db_elections.model

import com.example.db_elections.view_model.BaseViewModel

interface SortInput{
    fun input():String
}

class ConsoleSortInput : SortInput {
    override fun input() :String{
        var s:String?
        do {
            s = readLine()
        } while (s.isNullOrEmpty())
        return  s
    }
}

interface SortLogic {
    fun logic (input: ()->String, sortList: List<Sorting>) : (Election, Election) -> Int
}

class ConsoleSortLogic : SortLogic {
    override fun logic (input: ()->String, sortList: List<Sorting>) : (Election, Election) -> Int {
        var s: String
        do {
            s = input()
        } while (s.length!=1||!sortList.map { it.key }.contains(s[0]))
        return sortList.find { it.key==s[0] }?.func!!
    }
}

interface WorkSortLogic {
    fun work (list: MutableList<Election>, sort: (Election, Election) -> Int)
}

class BaseWorkSortLogic : WorkSortLogic {
    override fun work (list: MutableList<Election>, sort: (Election, Election) -> Int) {
        list.sortWith(Comparator (sort) )
    }
}

interface SortOutput{
    fun output(sortList:List<Sorting>)
}

class ConsoleSortOutput constructor(
    private val printSorting: PrintSorting
): SortOutput {
    override fun output(sortList:List<Sorting>){
        printSorting.printSorting(sortList)
    }
}

interface Sort: Action {
    override fun action (list: MutableList<Election>):Int
}

class ConsoleSort constructor(
    private val printSorting: PrintSorting,
    private val sort: SortList
): Sort {
    override fun action(list: MutableList<Election>): Int {
        val output = ConsoleSortOutput(printSorting)
        val input = ConsoleSortInput()
        val logic = ConsoleSortLogic()
        val work = BaseWorkSortLogic()
        val sortList = sort.getSortingList()
        output.output(sortList)
        work.work(list, logic.logic(input::input,sortList))
        return 0
    }
}

class AndroidSort : Sort {
    override fun action(list: MutableList<Election>): Int {
        BaseViewModel.FragmentTransit.instance.goToSortFragment()
        return 0
    }
}