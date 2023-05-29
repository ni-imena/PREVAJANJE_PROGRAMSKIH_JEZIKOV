package task

import java.io.File
import java.io.InputStream

const val SKIP_SYMBOL = 0
const val INT_SYMBOL = 1
const val STRING_SYBOL = 2
const val VAR_SYMBOL = 3
const val PLUS_SYMBOL = 4
const val MINUS_SYMBOL = 5
const val TIMES_SYMBOL = 6
const val DIVIDE_SYMBOL = 7
const val COMMA_SYMBOL = 8
const val ASSIGN_SYMBOL = 9
const val SEMI_SYMBOL = 10
const val CURLY_OPEN_SYMBOL = 11
const val CURLY_CLOSE_SYMBOL = 12
const val LPAREN_SYMBOL = 13
const val RPAREN_SYMBOL = 14
const val RUN_SYMBOL = 15
const val PATH_SYMBOL = 16
const val START_SYMBOL = 17
const val END_SYMBOL = 18
const val TIME_SYMBOL = 19
const val FOOD_STATION_SYMBOL = 20
const val WATER_STATION_SYMBOL = 21
const val CIRC_SYMBOL = 22
const val BOX_SYMBOL = 23
const val BWAND_SYMBOL = 24
const val BWOR_SYMBOL = 25
const val DECIMAL_SYMBOL = 26

const val ERROR_STATE = 0
const val EOF_SYMBOL = -1
const val EOF = -1
const val NEWLINE = '\n'.code

interface DFA {
    val states: Set<Int>
    val alphabet: IntRange
    fun next(state: Int, code: Int): Int
    fun symbol(state: Int): Int
    val startState: Int
    val finalStates: Set<Int>
}

object ForForeachFFFAutomaton: DFA {
    override val states = (1 .. 100).toSet()
    override val alphabet = 0 .. 255
    override val startState = 1
    override val finalStates = setOf(2, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 22, 26, 31, 34, 38, 42, 47, 51, 54, 55, 56, 58)

    private val numberOfStates = states.max() + 1 // plus the ERROR_STATE
    private val numberOfCodes = alphabet.max() + 1 // plus the EOF
    private val transitions = Array(numberOfStates) {IntArray(numberOfCodes)}
    private val values = Array(numberOfStates) {SKIP_SYMBOL}

    private fun setTransition(from: Int, chr: Char, to: Int) {
        transitions[from][chr.code + 1] = to // + 1 because EOF is -1 and the array starts at 0
    }

    private fun setTransition(from: Int, code: Int, to: Int) {
        transitions[from][code + 1] = to
    }

    private fun setTransition(from: Int, range: CharRange, to: Int) {
        for (c in range) {
            transitions[from][c.code + 1] = to
        }
    }

    private fun setNegativeTransition(from: Int, ignore: Char, to: Int) {
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz "
        for (c in allowedChars) {
            if (c != ignore) {
                transitions[from][c.code + 1] = to
            }
        }
    }

    private fun setSymbol(state: Int, symbol: Int) {
        values[state] = symbol
    }

    override fun next(state: Int, code: Int): Int {
        assert(states.contains(state))
        assert(alphabet.contains(code))
        return transitions[state][code + 1]
    }

    override fun symbol(state: Int): Int {
        assert(states.contains(state))
        return values[state]
    }
    init {
        setTransition(1, '0'..'9', 2)
        setTransition(2, '0'..'9', 2)

        setTransition(1, '"', 3)
        setTransition(3, 'a'..'z', 4)
        setTransition(3, 'A'..'Z', 4)
        setTransition(3, '0'..'9', 4)
        setTransition(4, 'a'..'z', 4)
        setTransition(4, 'A'..'Z', 4)
        setTransition(4, '0'..'9', 4)
        setTransition(4, ' ', 4)
        setTransition(4, '.', 4)
        setTransition(4, ',', 4)
        setTransition(4, '"', 5)

        setTransition(1, '+', 6)
        setTransition(1, '-', 7)
        setTransition(1, '*', 8)
        setTransition(1, '/', 9)
        setTransition(1, EOF, 10)
        setTransition(1, ' ', 11)
        setTransition(1, '\n', 11)
        setTransition(1, '\r', 11)
        setTransition(1, '\t', 11)
        setTransition(1, '=', 12)
        setTransition(1, ',', 13)

        setTransition(1, 'a', 14)
        setTransition(1, 'a'..'b', 14)
        setTransition(1, 'd'..'e', 14)
        setTransition(1, 'g'..'o', 14)
        setTransition(1, 'q', 14)
        setTransition(1, 't'..'v', 14)
        setTransition(1, 'x'..'z', 14)
        setTransition(1, 'A'..'Z', 14)
        setTransition(14, 'a'..'z', 14)
        setTransition(14, 'A'..'Z', 14)
        setTransition(14, '0'..'9', 15)
        setTransition(15, '0'..'9', 15)

        setTransition(1, '(', 16)
        setTransition(1, ')', 17)

        setTransition(1, '{', 18)
        setTransition(1, '}', 19)

        setTransition(1, 'r', 20)
        setTransition(20, 'u', 21)
        setTransition(21, 'n', 22)

        setNegativeTransition(20, 'u', 14)
        setNegativeTransition(21, 'n', 14)
        setTransition(20, '0'..'9', 15)
        setTransition(21, '0'..'9', 15)

        setTransition(1, 'p', 23)
        setTransition(23, 'a', 24)
        setTransition(24, 't', 25)
        setTransition(25, 'h', 26)

        setNegativeTransition(23, 'a', 14)
        setNegativeTransition(24, 't', 14)
        setNegativeTransition(25, 'h', 14)
        setTransition(23, '0'..'9', 15)
        setTransition(24, '0'..'9', 15)
        setTransition(25, '0'..'9', 15)

        setTransition(1, 's', 27)
        setTransition(27, 't', 28)
        setTransition(28, 'a', 29)
        setTransition(29, 'r', 30)
        setTransition(30, 't', 31)

        setNegativeTransition(27, 't', 14)
        setNegativeTransition(28, 'a', 14)
        setNegativeTransition(29, 'r', 14)
        setNegativeTransition(30, 't', 14)
        setTransition(27, '0'..'9', 15)
        setTransition(28, '0'..'9', 15)
        setTransition(29, '0'..'9', 15)
        setTransition(30, '0'..'9', 15)

        setTransition(1, 'e', 32)
        setTransition(32, 'n', 33)
        setTransition(33, 'd', 34)

        setNegativeTransition(32, 'n', 14)
        setNegativeTransition(33, 'd', 14)
        setTransition(32, '0'..'9', 15)
        setTransition(33, '0'..'9', 15)

        setTransition(1, 't', 35)
        setTransition(35, 'i', 36)
        setTransition(36, 'm', 37)
        setTransition(37, 'e', 38)

        setNegativeTransition(35, 'i', 14)
        setNegativeTransition(36, 'm', 14)
        setNegativeTransition(37, 'e', 14)
        setTransition(35, '0'..'9', 15)
        setTransition(36, '0'..'9', 15)
        setTransition(37, '0'..'9', 15)

        setTransition(1, 'f', 39)
        setTransition(39, 'o', 40)
        setTransition(40, 'o', 41)
        setTransition(41, 'd', 42)

        setNegativeTransition(39, 'o', 14)
        setNegativeTransition(40, 'o', 14)
        setNegativeTransition(41, 'd', 14)
        setTransition(39, '0'..'9', 15)
        setTransition(40, '0'..'9', 15)
        setTransition(41, '0'..'9', 15)

        setTransition(1, 'w', 43)
        setTransition(43, 'a', 44)
        setTransition(44, 't', 45)
        setTransition(45, 'e', 46)
        setTransition(46, 'r', 47)

        setNegativeTransition(43, 'a', 14)
        setNegativeTransition(44, 't', 14)
        setNegativeTransition(45, 'e', 14)
        setNegativeTransition(46, 'r', 14)
        setTransition(43, '0'..'9', 15)
        setTransition(44, '0'..'9', 15)
        setTransition(45, '0'..'9', 15)
        setTransition(46, '0'..'9', 15)

        setTransition(1, 'c', 48)
        setTransition(48, 'i', 49)
        setTransition(49, 'r', 50)
        setTransition(50, 'c', 51)

        setNegativeTransition(48, 'i', 14)
        setNegativeTransition(49, 'r', 14)
        setNegativeTransition(50, 'c', 14)
        setTransition(48, '0'..'9', 15)
        setTransition(49, '0'..'9', 15)
        setTransition(50, '0'..'9', 15)

        setTransition(1, 'b', 52)
        setTransition(52, 'o', 53)
        setTransition(53, 'x', 54)

        setNegativeTransition(52, 'o', 14)
        setNegativeTransition(53, 'x', 14)
        setTransition(52, '0'..'9', 15)
        setTransition(53, '0'..'9', 15)

        setTransition(1, '&', 55)
        setTransition(1, '|', 56)

        setTransition(2, '.', 57)
        setTransition(57, '0'..'9', 58)
        setTransition(58, '0'..'9', 58)


        setSymbol(2, INT_SYMBOL)
        setSymbol(5, STRING_SYBOL)
        setSymbol(6, PLUS_SYMBOL)
        setSymbol(7, MINUS_SYMBOL)
        setSymbol(8, TIMES_SYMBOL)
        setSymbol(9, DIVIDE_SYMBOL)
        setSymbol(10, EOF_SYMBOL)
        setSymbol(11, SKIP_SYMBOL)
        setSymbol(12, ASSIGN_SYMBOL)
        setSymbol(13, COMMA_SYMBOL)
        setSymbol(14, VAR_SYMBOL)
        setSymbol(15, VAR_SYMBOL)
        setSymbol(16, LPAREN_SYMBOL)
        setSymbol(17, RPAREN_SYMBOL)
        setSymbol(18, CURLY_OPEN_SYMBOL)
        setSymbol(19, CURLY_CLOSE_SYMBOL)
        setSymbol(22, RUN_SYMBOL)
        setSymbol(26, PATH_SYMBOL)
        setSymbol(31, START_SYMBOL)
        setSymbol(34, END_SYMBOL)
        setSymbol(38, TIME_SYMBOL)
        setSymbol(42, FOOD_STATION_SYMBOL)
        setSymbol(47, WATER_STATION_SYMBOL)
        setSymbol(51, CIRC_SYMBOL)
        setSymbol(54, BOX_SYMBOL)
        setSymbol(55, BWAND_SYMBOL)
        setSymbol(56, BWOR_SYMBOL)
        setSymbol(58, DECIMAL_SYMBOL)

    }
}

data class Token(val symbol: Int, val lexeme: String, val startRow: Int, val startColumn: Int)

class Scanner(private val automaton: DFA, private val stream: InputStream) {
    private var last: Int? = null
    private var row = 1
    private var column = 1

    private fun updatePosition(code: Int) {
        if (code == NEWLINE) {
            row += 1
            column = 1
        } else {
            column += 1
        }
    }

    fun getToken(): Token {
        val startRow = row
        val startColumn = column
        val buffer = mutableListOf<Char>()

        var code = last ?: stream.read()
        var state = automaton.startState
        while (true) {
            val nextState = automaton.next(state, code)
            if (nextState == ERROR_STATE) break // Longest match

            state = nextState
            updatePosition(code)
            buffer.add(code.toChar())
            code = stream.read()
        }
        last = code // The code following the current lexeme is the first code of the next lexeme

        if (automaton.finalStates.contains(state)) {
            val symbol = automaton.symbol(state)
            return if (symbol == SKIP_SYMBOL) {
                getToken()
            } else {
                val lexeme = String(buffer.toCharArray()).trim() // If allowing spaces to determine variables
                Token(symbol, lexeme, startRow, startColumn)
            }
        } else {
            throw Error("Invalid pattern at ${row}:${column}")
        }
    }
}

fun name(symbol: Int) =
    when (symbol) {
        INT_SYMBOL -> "int"
        STRING_SYBOL -> "string"
        VAR_SYMBOL -> "variable"
        PLUS_SYMBOL -> "plus"
        MINUS_SYMBOL -> "minus"
        TIMES_SYMBOL -> "times"
        DIVIDE_SYMBOL -> "divide"
        COMMA_SYMBOL -> "comma"
        EOF_SYMBOL -> "eof"
        ASSIGN_SYMBOL -> "assign"
        SEMI_SYMBOL -> "semi"
        CURLY_OPEN_SYMBOL -> "lcurly"
        CURLY_CLOSE_SYMBOL -> "rcurly"
        LPAREN_SYMBOL -> "lparen"
        RPAREN_SYMBOL -> "rparen"
        RUN_SYMBOL -> "run"
        PATH_SYMBOL -> "path"
        START_SYMBOL -> "start"
        END_SYMBOL -> "end"
        TIME_SYMBOL -> "time"
        FOOD_STATION_SYMBOL -> "food"
        WATER_STATION_SYMBOL -> "water"
        CIRC_SYMBOL -> "circle"
        BOX_SYMBOL -> "box"
        BWAND_SYMBOL -> "bwand"
        BWOR_SYMBOL -> "bwor"
        DECIMAL_SYMBOL -> "decimal"

        else -> throw Error("Invalid symbol")
    }

fun printTokens(scanner: Scanner) {
    val token = scanner.getToken()
    if (token.symbol != EOF_SYMBOL) {
        print("${name(token.symbol)}(\"${token.lexeme}\") ")
        printTokens(scanner)
    }
}

class Parser(private val scanner: Scanner) {
    var token: Token = scanner.getToken()

    fun parse(): Boolean {
        return Program() && token.symbol == EOF_SYMBOL
    }
    private fun Program(): Boolean {
        if(name(token.symbol) == "run") {
            token = scanner.getToken()
            if(name(token.symbol) == "string") {
                token = scanner.getToken()
                if(name(token.symbol) == "lcurly") {
                    token = scanner.getToken()
                    return Run()
                }
            }
        }
        return false
    }

    private fun Run(): Boolean {
        if (name(token.symbol) == "path") {
            token = scanner.getToken()
            if (Path()) {
                if (name(token.symbol) == "start") {
                    token = scanner.getToken()
                    if (Start()) {
                        if(name(token.symbol) == "end") {
                            token = scanner.getToken()
                            if (End()) {
                                return Stations()
                            }
                        }
                    }
                }
            }
        }
        else if (name(token.symbol) == "variable") {
            if (Primary()) {
                return Run()
            }
        }

        return false
    }

    private fun Path() : Boolean {
        if(name(token.symbol) == "lcurly") {
            token = scanner.getToken()
            return Points()
        }
        return false
    }

    private fun Start(): Boolean {
        if(name(token.symbol) == "lcurly") {
            token = scanner.getToken()
            if(Point()) {
                if(name(token.symbol) == "rcurly") {
                    token = scanner.getToken()
                    return true
                }
            }
        }

        return false
    }

    private fun End(): Boolean {
        if(name(token.symbol) == "lcurly") {
            token = scanner.getToken()
            if(Point()) {
                if(name(token.symbol) == "rcurly") {
                    token = scanner.getToken()
                    return true
                }
            }
        }
        return false
    }

    private fun Stations(): Boolean {
        if (name(token.symbol) == "rcurly") {
            token = scanner.getToken()
            return true
        }
        when (name(token.symbol)) {
            "time", "food", "water" -> {
                token = scanner.getToken()
                return Station()
            }
        }
        return false
    }

    private fun Station() : Boolean {
        if(name(token.symbol) == "lcurly") {
            token = scanner.getToken()
            if (name(token.symbol) == "box") {
                token = scanner.getToken()
                if (Box()) {
                    if(name(token.symbol) == "rcurly") {
                        token = scanner.getToken()
                        return Stations()
                    }
                }
            }
        }
        return false
    }

    private fun Box(): Boolean {
        if(name(token.symbol) == "lparen") {
            token = scanner.getToken()
            if(Point()) {
                if(name(token.symbol) == "comma") {
                    token = scanner.getToken()
                    if(Point()) {
                        if(name(token.symbol) == "rparen") {
                            token = scanner.getToken()
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    private fun Points(): Boolean {
        if(Point()) {
            if(name(token.symbol) == "comma") {
                token = scanner.getToken()
                return Points()
            }
            if(name(token.symbol) == "rcurly") {
                token = scanner.getToken()
                return true
            }
        }
        return false
    }

    private fun Point(): Boolean {
        if(name(token.symbol) == "lparen") {
            token = scanner.getToken()
            if (Additive()) {
                if(name(token.symbol) == "comma") {
                    token = scanner.getToken()
                    if (Additive()) {
                        if(name(token.symbol) == "rparen") {
                            token = scanner.getToken()
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    private fun Variable(): Boolean {
        if (name(token.symbol) == "assign") {
            token = scanner.getToken()
            if (Bitwise()) {
                return true
            }
            return false
        }
        return true
    }

    private fun Bitwise(): Boolean {
        return Additive() && Bitwise_()
    }
    private fun Bitwise_(): Boolean {
        when (name(token.symbol)) {
            "bwand", "bwor" -> {
                token = scanner.getToken()
                return Additive() && Bitwise_()
            }
        }
        return true // ε
    }
    private fun Additive(): Boolean {
        return Multiplicative() && Additive_()
    }
    private fun Additive_(): Boolean {
        when (name(token.symbol)) {
            "plus", "minus" -> {
                token = scanner.getToken()
                return Multiplicative() && Additive_()
            }
        }
        return true
    }
    private fun Multiplicative(): Boolean {
        return Unary() && Multiplicative_()
    }
    private fun Multiplicative_(): Boolean {
        when (name(token.symbol)) {
            "times", "divide" -> {
                token = scanner.getToken()
                return Unary() && Multiplicative_()
            }
        }
        return true
    }
    private fun Unary(): Boolean {
        when (name(token.symbol)) {
            "plus", "minus" -> {
                token = scanner.getToken()
                return Primary()
            }
        }
        return Primary()
    }
    private fun Primary(): Boolean {
        when (name(token.symbol)) {
            "int", "decimal" -> {
                token = scanner.getToken()
                return true
            }
            "variable" -> {
                token = scanner.getToken()
                return Variable()
            }
            "lparen" -> {
                token = scanner.getToken()
                if (Bitwise()) {
                    if (name(token.symbol) == "rparen") {
                        token = scanner.getToken()
                        return true;
                    }
                }
            }
        }
        return false
    }
}

class Evaluator(private val scanner: Scanner) {
    var token: Token = scanner.getToken()

    fun evaluate(): Int {
        val value = Expr()
        if (token.symbol == EOF_SYMBOL) {
            return value
        } else {
            throw IllegalArgumentException("Invalid expression.")
        }
    }
    private fun Expr(): Int {
        return Bitwise()
    }
    private fun Bitwise(): Int {
        val value = Additive()
        return Bitwise_(value)
    }
    private fun Bitwise_(leftValue: Int): Int {
        when (name(token.symbol)) {
            "bwand" -> {
                token = scanner.getToken()
                val rightValue = Additive()
                return Bitwise_(leftValue and rightValue)
            }
            "bwor" -> {
                token = scanner.getToken()
                val rightValue = Additive()
                return Bitwise_(leftValue or rightValue)
            }
        }
        return leftValue
    }
    private fun Additive(): Int {
        val value = Multiplicative()
        return Additive_(value)
    }
    private fun Additive_(leftValue: Int): Int {
        when (name(token.symbol)) {
            "plus" -> {
                token = scanner.getToken()
                val rightValue = Multiplicative()
                return Additive_(leftValue + rightValue)
            }
            "minus" -> {
                token = scanner.getToken()
                val rightValue = Multiplicative()
                return Additive_(leftValue - rightValue)
            }
        }
        return leftValue
    }
    private fun Multiplicative(): Int {
        val value = Unary()
        return Multiplicative_(value)
    }
    private fun Multiplicative_(leftValue: Int): Int {
        when (name(token.symbol)) {
            "times" -> {
                token = scanner.getToken()
                val rightValue = Unary()
                return Multiplicative_(leftValue * rightValue)
            }
            "divide" -> {
                token = scanner.getToken()
                val rightValue = Unary()
                return Multiplicative_(leftValue / rightValue)
            }
        }
        return leftValue
    }
    private fun Unary(): Int {
        when (name(token.symbol)) {
            "plus", "minus" -> {
                token = scanner.getToken()
                val value = Primary()
                return if (name(token.symbol) == "plus") value else -value
            }
        }
        return Primary()
    }
    private fun Primary(): Int {
        when (name(token.symbol)) {
            "int" -> {
                val value = token.lexeme.toInt()
                token = scanner.getToken()
                return value
            }
            "hex" -> {
                val value = token.lexeme.substring(1).toInt(16)
                token = scanner.getToken()
                return value
            }
            "variable" -> {
                if (token.lexeme == "x") {
                    token = scanner.getToken()
                    return 1
                }
                else {
                    token = scanner.getToken()
                    return 3
                }
            }
            "lparen" -> {
                token = scanner.getToken()
                val value = Bitwise()
                if (name(token.symbol) == "rparen") {
                    token = scanner.getToken()
                    return value;
                }
            }
            else -> throw IllegalArgumentException("Invalid expression.")
        }
        return 0
    }
}

fun main(args: Array<String>) {
    val file = File(args[0]).readText(Charsets.UTF_8)
//    printTokens(Scanner(ForForeachFFFAutomaton, file.byteInputStream()))
//    if(Parser(Scanner(ForForeachFFFAutomaton, file.byteInputStream())).parse()) {
//        println("accept")
//    }
//    else {
//        println("reject")
//    }
    println(Evaluator(Scanner(ForForeachFFFAutomaton, file.byteInputStream())).evaluate())
}