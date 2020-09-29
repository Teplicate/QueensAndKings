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
        viewModelScope.cancel()
    }

    fun calc(boardSize: Int, queens: Int, kings: Int) {
        Log.i(className, "Launching calc")
        val (queensRes, kingsRes)
                = InWidthCalc.calcCombinations(
            boardSize = boardSize,
            queens = queens,
            kings = kings
        )
        queensAndKingsCount = kingsRes
        queensCount = queensRes
        calcDone()
    }
}

enum class CalcState {
    IN_PROGRESS,
    DONE,
    CANCEL,
    NONE
}