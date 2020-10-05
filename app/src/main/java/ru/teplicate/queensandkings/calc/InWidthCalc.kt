package ru.teplicate.queensandkings.calc

import android.util.Log
import kotlin.math.abs

object InWidthCalc {
    private val className = this::class.java.name
    private var boardSize: Int = 0
    private var queenSize: Int = 0
    private var kingSize: Int = 0
    private lateinit var queens: Array<MPair>
    private lateinit var kings: Array<MPair>
    private var queenCounter = 0
    private var queensAndKignsCounter = 0
    var stop = false

    @Throws(StackOverflowError::class)
    fun calcCombinations(boardSize: Int, queens: Int, kings: Int): Pair<Int?, Int?> {
        Log.i(className, "In calc")
        setParams(boardSize, queens, kings)
        return try {
            if (queens != 0)
                solve()
            else solveKings()
            queenCounter to queensAndKignsCounter
        } catch (ise: IllegalStateException) {
            Log.i(className, "Stopped")
            null to null
        }
    }

    fun stop() {
        stop = true
    }

    private fun setParams(boardSize: Int, queens: Int, kings: Int) {
        this.boardSize = boardSize
        this.queenSize = queens
        this.kingSize = kings
        stop = false
        this.queens = Array(queenSize) { MPair(0, 0) }
        this.kings = Array(kingSize) { MPair(0, 0) }
        queenCounter = 0
        queensAndKignsCounter = 0
    }

    @Throws(IllegalStateException::class, StackOverflowError::class)
    fun solve(queen: Int = 0, horPoz: Int = 1): Boolean {
        when {
            queen == queenSize -> {
                queenCounter++
                return true
            }
            horPoz > boardSize -> {
                return false
            }
            stop -> throw IllegalStateException("Stopped")
            else -> {
                for (v in 1..boardSize) {
                    var crosses = false
                    for (q in 0 until queen) {
                        if (isQueenCrossing(q, v, horPoz)) {
                            crosses = true
                            break
                        }
                    }

                    if (!crosses) {
                        queens[queen].x = horPoz
                        queens[queen].y = v
                        val solved = solve(queen + 1, horPoz + 1)

                        if (solved) {
                            solveKings()
                        }

                        queens[queen].reset()
                    }
                }


                return solve(queen, horPoz + 1)
            }
        }
    }

    @Throws(IllegalStateException::class, StackOverflowError::class)
    private fun solveKings(king: Int = 0, horPos: Int = 1, verPos: Int = 1): Boolean {
        when {
            king == kingSize -> {
                queensAndKignsCounter++
                return true
            }
            horPos > boardSize -> {
                return false
            }
            stop -> throw IllegalStateException("Stopped")
            else -> {
                var crosses = false

                for (q in 0 until queenSize) {
                    if (isQueenCrossing(q, verPos, horPos)) {
                        crosses = true
                        break
                    }
                }

                if (!crosses) {
                    for (k in 0 until king) {
                        if (isKingCrossing(k, verPos, horPos)) {
                            crosses = true
                            break
                        }
                    }
                }

                if (!crosses) {
                    kings[king].x = horPos
                    kings[king].y = verPos
                    if (verPos == boardSize)
                        solveKings(king + 1, horPos + 1, 1)
                    else solveKings(king + 1, horPos, verPos + 1)
                }

                kings[king].reset()

                return if (verPos == boardSize) {
                    solveKings(king, horPos + 1, 1)
                } else solveKings(king, horPos, verPos + 1)
            }
        }

    }

    private fun isQueenCrossing(q: Int, verPoz: Int, horPoz: Int): Boolean {
        if (!queens[q].isSet())
            return false

        return queens[q].x == horPoz || queens[q].y == verPoz || (abs(queens[q].y - verPoz) == abs(
            queens[q].x - horPoz
        ))
    }

    private fun isKingCrossing(k: Int, verPos: Int, horPos: Int): Boolean {
        if (!kings[k].isSet())
            return false

        return (abs(kings[k].x - horPos) <= 1 && abs(kings[k].y - verPos) <= 1)
    }

    class MPair(var x: Int, var y: Int) {
        fun isSet() = (this.x != 0 && this.y != 0)

        fun reset() {
            this.x = 0
            this.y = 0
        }
    }
}