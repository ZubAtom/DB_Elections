package com.example.db_elections.model

import com.example.db_elections.*

enum class Message(val s: String) {
    RECORD_INCORRECT("Запись некорректна"),
    RECORD_EXISTS("Запись уже существует"),
    RECORD_NOT_EXIST("Запись не существует"),
    ACTION_COMPLETED("Команда выполнена"),
    RECORD_CORRECT("Запись корректна")
}

enum class Actions {
    ADD,
    DELETE,
    EDIT,
    SORT,
    FIND,
    EXIT
}

/*fun main() {
    val checkLoc= SimpleCheckersLocation()
    val checksCan= SimpleCheckersCandidate()
    val checkVotes= SimpleCheckersVotes()

    val election=SimpleElectionFactory()
    val createMenu = SimpleCreateFullMenuNote()
    val printToRead = ConsolePrintToRead()
    val input = ConsoleInput(election,printToRead, checkLoc, checksCan, checkVotes)
    val sort = ConsoleSortList()

    val printMes=ConsolePrintMessage()
    val printSort=ConsolePrintSorting()
    val printElections=ConsolePrintElections()
    val printMenu = ConsolePrintMenu()

    val list = mutableListOf<Election>()

    val menu = FirstMenuList(createMenu, input, printMes, printSort, printElections, sort)
    do {
        printMenu.printMenu(menu)
        var s: String?
        do {
            s = readLine()
        } while (s==null||s.length!=1)
        val action =menu.createList().find { it.key==s[0] }
    } while (action==null||action.f.action(list)!=1)
}*/

/*
interface OnElectionsClickListener{
    fun onElectionClick(election: Election, position: Int)
}

class OnEditClick : OnElectionsClickListener{
    override fun onElectionClick(election: Election, position: Int) {

    }
}
*/





interface Action {
    fun action(list: MutableList<Election>): Int
}

interface Comparators<T> {
    fun compare(o1: T, o2: T): Int
}

class ComparatorInt : Comparators<Int> {
    override fun compare(o1: Int, o2: Int) = o1 - o2

}

class ComparatorString : Comparators<String> {
    override fun compare(o1: String, o2: String): Int {
        val b = o1.length > o2.length
        val len = if (b) o2.length
        else o1.length
        var count = 0
        do {
            if (o1[count] != o2[count]) {
                return if (o1[count].code < o2[count].code) -1
                else 1
            }
            count++
        } while (count < len)
        return if (o1.length == o2.length) 0
        else {
            if (b) 1
            else -1
        }
    }
}

class ComparatorElection constructor(private val o1: Election, private val o2: Election) {
    fun compareLocation(): Int = ComparatorString().compare(o1.location!!, o2.location!!)
    fun compareCandidate(): Int = ComparatorString().compare(o1.candidate!!, o2.candidate!!)
    fun compareVotes(): Int = ComparatorInt().compare(o1.quantityOfVotes!!, o2.quantityOfVotes!!)
}

abstract class MenuNote()

interface SimpleMenuList {
    fun createList(): List<SimpleMenuNote>
}

interface ExtendedMenuList {
    fun createList(): List<ExtendedMenuNote>
}

class FirstMenuList constructor(
    private val createMenuNote: CreateSimpleMenuNote,
    private val baseInput: BaseInput,
    private val printMes: PrintMessage,
    private val printSort: PrintSorting,
    private val printElections: PrintElections,
    private val sort: SortList
) : SimpleMenuList {
    override fun createList(): List<SimpleMenuNote> {
        return listOfNotNull(
            createMenuNote.create('1', "Добавить", ConsoleAdd(baseInput, printMes)),
            createMenuNote.create('2', "Изменить", ConsoleEdit(baseInput, printMes)),
            createMenuNote.create('3', "Удалить", ConsoleDel(baseInput, printMes)),
            createMenuNote.create('4', "Сортировать", ConsoleSort(printSort, sort)),
            createMenuNote.create('5', "Найти", ConsoleFind(baseInput, printElections)),
            createMenuNote.create('6', "Вывести все записи", ConsoleOutputTable(printElections)),
            createMenuNote.create('7', "Выход", SimpleExit())
        )
    }
}

class SecondMenuList constructor(
    private val createMenuNote: CreateExtendedMenuNote,
) : ExtendedMenuList {
    override fun createList(): List<ExtendedMenuNote> {
        return listOfNotNull(
            createMenuNote.create(Actions.ADD, '1', "Add", AndroidAdd()),
            createMenuNote.create(Actions.SORT, '2', "Sort", AndroidSort()),
            createMenuNote.create(Actions.FIND, '3', "Find", AndroidFind()),
            createMenuNote.create(Actions.EXIT, '4', "Exit", AndroidExit()),

        )
    }
}

open class SimpleMenuNote constructor(
    open val key: Char,
    open val name: String,
    open val action: Action
) : MenuNote()

class ExtendedMenuNote constructor(
    val res: Actions,
    override val key: Char,
    override val name: String,
    override val action: Action
) : SimpleMenuNote(key, name, action)

interface CreateSimpleMenuNote {
    fun create(key: Char, name: String, f: Action): SimpleMenuNote
}

interface CreateExtendedMenuNote {
    fun create(res: Actions, key: Char, name: String, f: Action): ExtendedMenuNote
}

class SimpleCreateFullMenuNote : CreateSimpleMenuNote {
    override fun create(key: Char, name: String, f: Action) =
        SimpleMenuNote(key, name, f)
}

class ExtendedCreateFullMenuNote : CreateExtendedMenuNote {
    override fun create(res: Actions, key: Char, name: String, f: Action) =
        ExtendedMenuNote(res, key, name, f)
}

class Sorting constructor(
    val key: Char,
    val property: String,
    val resources: Int,
    val func: (Election, Election) -> Int
)

interface SortList {
    fun getSortingList(): List<Sorting>
}

class ConsoleSortList : SortList {
    override fun getSortingList(): List<Sorting> {
        return listOf(
            Sorting('1', "Location", 0) { o1, o2 -> ComparatorElection(o1, o2).compareLocation() },
            Sorting('2', "Candidate", 0) { o1, o2 ->
                ComparatorElection(
                    o1,
                    o2
                ).compareCandidate()
            },
            Sorting('3', "Quantity Votes", 0) { o1, o2 ->
                ComparatorElection(
                    o1,
                    o2
                ).compareVotes()
            },
        )
    }
}

class AndroidSortList : SortList {
    override fun getSortingList(): List<Sorting> {
        return listOf(
            Sorting(' ', "", R.string.location) { o1, o2 ->
                ComparatorElection(
                    o1,
                    o2
                ).compareLocation()
            },
            Sorting(' ', "", R.string.candidate) { o1, o2 ->
                ComparatorElection(
                    o1,
                    o2
                ).compareCandidate()
            },
            Sorting(' ', "", R.string.quantity_votes) { o1, o2 ->
                ComparatorElection(
                    o1,
                    o2
                ).compareVotes()
            },
        )
    }
}

interface PrintToRead {
    fun printToLocation()
    fun printToCandidate()
    fun printToVotes()
}

class ConsolePrintToRead : PrintToRead {
    override fun printToLocation() {
        println("Введите участок выборов")
    }

    override fun printToCandidate() {
        println("Введите кандидата выборов")
    }

    override fun printToVotes() {
        println("Введите количество голосов")
    }
}

interface PrintElections {
    fun printElections(list: List<Election>)
}

class ConsolePrintElections : PrintElections {
    override fun printElections(list: List<Election>) {
        println("Участок  Кандидат  Количество голосов")
        list.forEach {
            println("${it.location}  ${it.candidate}  ${it.quantityOfVotes}")
        }
        println()
    }
}

interface PrintMessage {
    fun printMessage(mes: Message)
}

class ConsolePrintMessage : PrintMessage {
    override fun printMessage(mes: Message) {
        println(mes.s)
    }
}

interface PrintSorting {
    fun printSorting(list: List<Sorting>)
}

class ConsolePrintSorting : PrintSorting {
    override fun printSorting(list: List<Sorting>) {
        println("Отсортировать по:")
        list.forEach {
            println("${it.key} ${it.property}")
        }
    }
}

interface PrintMenu {
    fun printMenu(menu: SimpleMenuList)
}

class ConsolePrintMenu : PrintMenu {
    override fun printMenu(menu: SimpleMenuList) {
        val list = menu.createList()
        list.forEach {
            println("${it.key} ${it.name}")
        }
    }
}

interface CreateElection {
    fun createElection(loc: String?, can: String?, votes: Int?): Election
}

class SimpleCreateElection(
    private val factory: ElectionFactory
) : CreateElection {
    override fun createElection(loc: String?, can: String?, votes: Int?): Election =
        factory.create(loc, can, votes)
}

interface CheckElection {
    fun checkElection(loc: String?, can: String?, votes: Int?): Election?
}

class SimpleCheckElection constructor(private val create: CreateElection) : CheckElection {
    override fun checkElection(loc: String?, can: String?, votes: Int?): Election? =
        if (loc == null || can == null || votes == null) null
        else create.createElection(loc, can, votes)
}

interface ElectionFactory {
    fun create(loc: String?, can: String?, votes: Int?): Election
}

class SimpleElectionFactory : ElectionFactory {
    override fun create(loc: String?, can: String?, votes: Int?): Election =
        Election(loc, can, votes)
}

data class Election(var location: String?, var candidate: String?, var quantityOfVotes: Int?)

interface Checkers<T> {
    fun check(input: String?): T?
}

interface CheckersString : Checkers<String> {
    override fun check(input: String?): String?
}

interface CheckersInt : Checkers<Int> {
    override fun check(input: String?): Int?
}

interface CheckersLocation : CheckersString {
    override fun check(input: String?): String?
}

interface CheckersCandidate : CheckersString {
    override fun check(input: String?): String?
}

interface CheckersVotes : CheckersInt {
    override fun check(input: String?): Int?
}

class SimpleCheckersLocation : CheckersLocation {
    override fun check(input: String?): String? {
        return if (input.isNullOrEmpty()) null
        else input
    }
}

class SimpleCheckersCandidate : CheckersCandidate {
    override fun check(input: String?): String? {
        return if (input.isNullOrEmpty()) null
        else input
    }
}

class SimpleCheckersVotes : CheckersVotes {
    override fun check(input: String?): Int? {
        val temp = if (!input.isNullOrEmpty()) input.toIntOrNull()
        else null
        return if (temp != null && temp >= 0) temp
        else null
    }

}

interface BaseInput {
    fun noteInput(flag: Boolean = true): Election?
}

class ConsoleInput(
    private val print: PrintToRead,
    private val processing: InputProcessing
) : BaseInput {
    override fun noteInput(flag: Boolean): Election? {
        print.printToLocation()
        val loc = readLine()
        print.printToCandidate()
        val can = readLine()
        print.printToVotes()
        val votes = readLine()
        return processing.processing(loc, can, votes, flag)
    }
}

interface InputProcessing {
    fun processing(loc: String?, can: String?, votes: String?, flag: Boolean): Election?
}

class BaseInputProcessing(
    private val election: ElectionFactory,
    private val checkLoc: CheckersLocation,
    private val checksCan: CheckersCandidate,
    private val checkVotes: CheckersVotes
) : InputProcessing {
    override fun processing(loc: String?, can: String?, votes: String?, flag: Boolean): Election? {
        val createElection = SimpleCreateElection(election)
        val location = checkLoc.check(loc)
        val candidate = checksCan.check(can)
        val numberVotes = checkVotes.check(votes)
        return if (flag) {
            val checkElection = SimpleCheckElection(createElection)
            checkElection.checkElection(location, candidate, numberVotes)
        } else {
            createElection.createElection(location, candidate, numberVotes)
        }
    }
}

class StorageState{

    var action: Actions? = null
    var findState: Int = 0
    var election: Election? = null
    var position: Int? = null
    val list = mutableListOf<Election>(
        //Election("a", "a", 3)
    )
    var foundList = mutableListOf<Election>()

    companion object {
        val instance by lazy { StorageState() }
    }
}
