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
const val BWAND_SYMBOL = 8
const val BWOR_SYMBOL = 9
const val POINT_SYMBOL = 10
const val ASSIGN_SYMBOL = 11
const val SEMI_SYMBOL = 12
const val FOR_SYMBOL = 13
const val TO_SYMBOL = 14
const val BEGIN_SYMBOL = 15
const val END_SYMBOL = 16

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
    override val finalStates = setOf(2, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 21, 52, 54, 59, 62)

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

        setTransition(1, 'a', 6)
        setTransition(1, 'c'..'d', 6)
        setTransition(1, 'g'..'s', 6)
        setTransition(1, 'u'..'z', 6)
        setTransition(1, 'A'..'B', 6)
        setTransition(1, 'D'..'Z', 6)
        setTransition(6, 'a'..'z', 6)
        setTransition(6, 'a'..'z', 6)
        setTransition(6, 'A'..'Z', 6)
        setTransition(6, '0'..'9', 7)
        setTransition(7, '0'..'9', 7)

        setTransition(1, '+', 8)
        setTransition(1, '-', 9)
        setTransition(1, '*', 10)
        setTransition(1, '/', 11)
        setTransition(1, '&', 12)
        setTransition(1, '|', 13)
        setTransition(1, EOF, 14)
        setTransition(1, ' ', 15)
        setTransition(1, '\n', 15)
        setTransition(1, '\r', 15)
        setTransition(1, '\t', 15)
        setTransition(1, '=', 16)

        setTransition(1, '(', 17)
        setTransition(17, '0'..'9', 18)
        setTransition(18, '0'..'9', 18)
        setTransition(18, ',', 19)
        setTransition(19, '0'..'9', 20)
        setTransition(20, '0'..'9', 20)
        setTransition(20, ')', 21)

        // for
        setTransition(1, 'f', 50)
        setTransition(50, 'o', 51)
        setTransition(51, 'r', 52)

        setTransition(50, 'a'..'n', 6)
        setTransition(50, 'p'..'z', 6)
        setTransition(50, 'A'..'Z', 6)
        setTransition(50, '0'..'9', 7)

        setTransition(51, 'a'..'q', 6)
        setTransition(51, 's'..'z', 6)
        setTransition(51, 'A'..'Z', 6)
        setTransition(51, '0'..'9', 7)

        setTransition(52, 'a'..'z', 6)
        setTransition(52, 'A'..'Z', 6)
        setTransition(52, '0'..'9', 7)

        // to
        setTransition(1, 't', 23)
        setTransition(53, 'o', 24)

        setTransition(53, 'a'..'n', 6)
        setTransition(53, 'p'..'z', 6)
        setTransition(53, 'A'..'Z', 6)
        setTransition(53, '0'..'9', 7)

        setTransition(52, 'a'..'z', 6)
        setTransition(52, 'A'..'Z', 6)
        setTransition(52, '0'..'9', 7)

        // begin
        setTransition(1, 'b', 25)
        setTransition(55, 'e', 26)
        setTransition(56, 'g', 27)
        setTransition(57, 'i', 28)
        setTransition(58, 'n', 29)

        setTransition(55, 'a'..'d', 6)
        setTransition(55, 'f'..'z', 6)
        setTransition(55, 'A'..'Z', 6)
        setTransition(55, '0'..'9', 7)

        setTransition(56, 'a'..'f', 6)
        setTransition(56, 'h'..'z', 6)
        setTransition(56, 'A'..'Z', 6)
        setTransition(56, '0'..'9', 7)

        setTransition(57, 'a'..'h', 6)
        setTransition(57, 'j'..'z', 6)
        setTransition(57, 'A'..'Z', 6)
        setTransition(57, '0'..'9', 7)

        setTransition(58, 'a'..'m', 6)
        setTransition(58, 'o'..'z', 6)
        setTransition(58, 'A'..'Z', 6)
        setTransition(58, '0'..'9', 7)

        setTransition(59, 'a'..'z', 6)
        setTransition(59, 'A'..'Z', 6)
        setTransition(59, '0'..'9', 7)

        // end
        setTransition(1, 'e', 60)
        setTransition(60, 'n', 61)
        setTransition(61, 'd', 62)

        setTransition(60, 'a'..'m', 6)
        setTransition(60, 'o'..'z', 6)
        setTransition(60, 'A'..'Z', 6)
        setTransition(60, '0'..'9', 7)

        setTransition(61, 'a'..'c', 6)
        setTransition(61, 'e'..'z', 6)
        setTransition(61, 'A'..'Z', 6)
        setTransition(61, '0'..'9', 7)

        setTransition(62, 'a'..'z', 6)
        setTransition(62, 'A'..'Z', 6)
        setTransition(62, '0'..'9', 7)


        setSymbol(2, INT_SYMBOL)
        setSymbol(5, STRING_SYBOL)
        setSymbol(6, VAR_SYMBOL)
        setSymbol(7, VAR_SYMBOL)
        setSymbol(8, PLUS_SYMBOL)
        setSymbol(9, MINUS_SYMBOL)
        setSymbol(10, TIMES_SYMBOL)
        setSymbol(11, DIVIDE_SYMBOL)
        setSymbol(12, BWAND_SYMBOL)
        setSymbol(13, BWOR_SYMBOL)
        setSymbol(14, EOF_SYMBOL)
        setSymbol(15, SKIP_SYMBOL)
        setSymbol(16, ASSIGN_SYMBOL)
        setSymbol(21, POINT_SYMBOL)
        setSymbol(52, FOR_SYMBOL)
        setSymbol(54, TO_SYMBOL)
        setSymbol(59, BEGIN_SYMBOL)
        setSymbol(62, END_SYMBOL)
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
                val lexeme = String(buffer.toCharArray())
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
        BWAND_SYMBOL -> "bwand"
        BWOR_SYMBOL -> "bwor"
        EOF_SYMBOL -> "eof"
        ASSIGN_SYMBOL -> "assign"
        POINT_SYMBOL -> "point"
        SEMI_SYMBOL -> "semi"
        FOR_SYMBOL -> "for"
        TO_SYMBOL -> "to"
        BEGIN_SYMBOL -> "begin"
        END_SYMBOL -> "end"
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
        return Statements()
    }

    private fun Statements(): Boolean {
        while (true) {
            if(name(token.symbol) == "for") {
                if (!ForLoop()) {
                    return false
                }
            }
            else if(name(token.symbol) == "variable") {
                if (!Assign()) {
                    return false
                }
            }
            else if(name(token.symbol) == "console") {
                if (!Console()) {
                    return false
                }
            }
            else {
                return true
            }
        }
    }

    private fun Assign(): Boolean {
        token = scanner.getToken()
        if (name(token.symbol) == "assign") {
            token = scanner.getToken()
            if (Bitwise()) {
                if (name(token.symbol) == "semi") {
                    token = scanner.getToken()
                }
                return true
            }
        }
        return false
    }
    private fun ForLoop(): Boolean {
        token = scanner.getToken()
        if (name(token.symbol) == "lparen") {
            token = scanner.getToken()
            if (name(token.symbol) == "variable") {
                token = scanner.getToken()
                if (name(token.symbol) == "assign") {
                    token = scanner.getToken()
                    if (Bitwise() && To()) {
                        if (name(token.symbol) == "rparen") {
                            token = scanner.getToken()
                            if (Begin()) {
                                return true
                            }
                        }
                    }
                }
            }
        }
        return false
    }
    private fun To(): Boolean {
        if (name(token.symbol) == "to") {
            token = scanner.getToken()
            if (Bitwise()) {
                return true
            }
        }
        return false
    }
    private fun Begin(): Boolean {
        if (name(token.symbol) == "begin") {
            token = scanner.getToken()
            if (Statements()) {
                if (name(token.symbol) == "end") {
                    token = scanner.getToken()
                    if (name(token.symbol) == "semi") {
                        token = scanner.getToken()
                    }
                    return true
                }
            }
        }
        return false
    }
    private fun Console(): Boolean {
        token = scanner.getToken()
        if (Bitwise()) {
            if (name(token.symbol) == "semi") {
                token = scanner.getToken()
            }
            return true
        }
        return false
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
            "int", "hex", "variable" -> {
                token = scanner.getToken()
                return true
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

fun main(args: Array<String>) {
    val file = File(args[0]).readText(Charsets.UTF_8)
    printTokens(Scanner(ForForeachFFFAutomaton, file.byteInputStream()))
//    if(Parser(Scanner(ForForeachFFFAutomaton, file.byteInputStream())).parse()) {
//        println("accept")
//    }
//    else {
//        println("reject")
//    }
}