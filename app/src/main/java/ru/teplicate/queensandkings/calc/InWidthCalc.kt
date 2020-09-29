package ru.teplicate.queensandkings.calc

import android.util.Log
import kotlin.math.abs
import kotlin.math.sqrt

object InWidthCalc {
    private val className = this::class.java.name
    val sqrtTwo = sqrt(2.0)
    private var boardSize: Int = 0
    private var queenSize: Int = 0
    private var kingSize: Int = 0
    private lateinit var queens: IntArray
    private lateinit var kings: IntArray
    private var queenCounter = 0
    private var queensAndKignsCounter = 0

    fun calcCombinations(boardSize: Int, queens: Int, kings: Int): Pair<Int, Int> {
        Log.i(className, "In calc")
        setParams(boardSize, queens, kings)
        solve()

        return queenCounter to queensAndKignsCounter
    }

    private fun setParams(boardSize: Int, queens: Int, kings: Int) {
        this.boardSize = boardSize
        this.queenSize = queens
        this.kingSize = kings
        this.queens = IntArray(boardSize) { 0 }
        this.kings = IntArray(boardSize) { 0 }
        queenCounter = 0
        queensAndKignsCounter = 0
    }

    fun solve(queen: Int = 0, horPoz: Int = 0): Boolean {
        if (queen == queenSize) {
            return true
        } else if (horPoz == boardSize) {
            return false
        }

        for (v in 0 until boardSize) { //vert
            var crossing = false

            for (q in 0 until horPoz) {
                if (queens[q] == -1) //разрыв поэтому проверка пересечений не нужна
                    continue

                if (isCrossing(q, v, horPoz)) {
                    crossing = true
                    //плохо, выходим из цикла проверки пересечений, нуно искать дальше
                    break
                }
            }

            if (!crossing) {
                queens[horPoz] = v
                val status = solve(queen + 1, horPoz + 1)

                if (status) {
                    queenCounter++
                    solveKings(queensHorPoz = horPoz)
                }
            }

            for (r in horPoz until boardSize) {
                queens[r] = 0
            }
        }

        queens[horPoz] =
            -1 //места по вертикали закончились, помечаем здесь разрыв и проверяем места в следующем столбце

        return solve(queen, horPoz + 1)
    }

    private fun solveKings(king: Int = 0, horPoz: Int = 0, queensHorPoz: Int): Boolean {
        if (king == kingSize) {
            return true
        } else if (horPoz == boardSize) {
            return false
        }

        for (v in 0 until boardSize) {
            var crossing = false

            for (q in 0..queensHorPoz) {
                if (queens[q] == -1)
                    continue

                if (isCrossing(q, v, horPoz)) {
                    crossing = true
                    break
                }
            }

            if (!crossing) {
                //проверка с другими королями
                for (k in 0 until horPoz) {
                    if (kings[k] == -1)
                        continue

                    if (isKingCrossing(k, v, horPoz)) {
                        crossing = true
                        break
                    }
                }

                if (!crossing) {
                    kings[horPoz] = v
                    val status = solveKings(king + 1, horPoz + 1, queensHorPoz)

                    if (status) {
                        queensAndKignsCounter++
                    }
                }
            }

            for (t in horPoz until boardSize) {
                kings[t] = 0
            }
        }

        kings[horPoz] = -1

        return solveKings(king, horPoz + 1, queensHorPoz)
    }

    private fun isKingCrossing(k: Int, v: Int, king: Int): Boolean {
        //horizontally
        //vertically
        //если на одной вертикали, то проверить длину вектора
        return abs(k - king) <= 1 || abs(kings[k] - v) <= 1 || if (abs(k - king) == abs(kings[k] - v)) {
            val x = k - king
            val y = kings[k] - v
            val length = sqrt(x * x * 1.0 + y * y * 1.0)
            length <= sqrtTwo
        } else false
    }

    private fun isCrossing(q: Int, vertical: Int, horizon: Int): Boolean {
        return (q == horizon) || (queens[q] == vertical) || (abs(q - horizon) == abs(queens[q] - vertical))
    }
}