package ru.teplicate.queensandkings.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import ru.teplicate.queensandkings.calc.InWidthCalc

class CalcViewModel : ViewModel() {
    private val className = this::class.java.name

    private val _calcState: MutableLiveData<CalcState> = MutableLiveData(CalcState.NONE)
    val calcState: LiveData<CalcState>
        get() = _calcState

    var job: Job? = null
    var queensCount: Int? = null
    var queensAndKingsCount: Int? = null

    fun startCalc() {
        _calcState.value = CalcState.IN_PROGRESS
    }

    fun cancelCalc() {
        _calcState.value = CalcState.CANCEL
    }

    private fun calcDone() {
        Log.i(className, "DONE")
        _calcState.value = CalcState.DONE
    }

    fun cancelCalcJob() {
        job!!.cancel(CancellationException("Stop"))
        InWidthCalc.stop()
        _calcState.value = CalcState.CANCELLED
    }

    fun calc(boardSize: Int, queens: Int, kings: Int) {
        Log.i(className, "Launching calc")

        job = viewModelScope.launch {
            val pair = withContext(Dispatchers.Default) {
                try {
                    InWidthCalc.calcCombinations(
                        boardSize = boardSize,
                        queens = queens,
                        kings = kings
                    )
                } catch (e: StackOverflowError) {
                    null
                }
            }

            pair?.let {
                queensAndKingsCount = it.second
                queensCount = it.first
                calcDone()
            } ?: stackOverflow()
        }
    }

    private fun stackOverflow() {
        _calcState.value = CalcState.STACK_OVERFLOW
    }
}

enum class CalcState {
    IN_PROGRESS,
    DONE,
    CANCEL,
    NONE,
    CANCELLED,
    STACK_OVERFLOW
}