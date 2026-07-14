package com.example.stopwatch.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stopwatch.model.Lap
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StopwatchViewModel : ViewModel() {

    data class UIState(
        val isRunning: Boolean = false,
        val isDisplayHeld: Boolean = false,
        val heldTimeMs: Long = 0L,
        val elapsedTimeMs: Long = 0L,
        val laps: List<Lap> = emptyList(),
        val currentLapTimeMs: Long = 0L
    )

    private val _uiState = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var startTime: Long = 0L
    private var accumulatedTime: Long = 0L
    private var lastLapTotalTime: Long = 0L

    fun start() {
        if (_uiState.value.isRunning) return

        startTime = System.currentTimeMillis()
        _uiState.update { it.copy(isRunning = true) }

        timerJob = viewModelScope.launch {
            while (true) {
                val currentPeriod = System.currentTimeMillis() - startTime
                val totalElapsed = accumulatedTime + currentPeriod
                val currentLapTime = totalElapsed - lastLapTotalTime

                _uiState.update { state ->
                    state.copy(
                        elapsedTimeMs = totalElapsed,
                        currentLapTimeMs = currentLapTime
                    )
                }
                delay(10)
            }
        }
    }

    fun stop() {
        val currentState = _uiState.value
        if (currentState.isRunning) {
            timerJob?.cancel()
            timerJob = null
            accumulatedTime += (System.currentTimeMillis() - startTime)
            _uiState.update { it.copy(
                isRunning = false, 
                elapsedTimeMs = accumulatedTime,
                isDisplayHeld = false // Reset display hold on stop
            ) }
        } else {
            // Act as reset if already stopped
            reset()
        }
    }

    fun reset() {
        timerJob?.cancel()
        timerJob = null
        startTime = 0L
        accumulatedTime = 0L
        lastLapTotalTime = 0L
        _uiState.value = UIState()
    }

    fun toggleHold() {
        val currentState = _uiState.value
        if (!currentState.isRunning) return

        if (currentState.isDisplayHeld) {
            // Release hold
            _uiState.update { it.copy(isDisplayHeld = false) }
        } else {
            // Capture current running time and hold
            val totalElapsed = accumulatedTime + (System.currentTimeMillis() - startTime)
            lap()
            _uiState.update { it.copy(
                isDisplayHeld = true,
                heldTimeMs = totalElapsed
            ) }
        }
    }

    fun lap() {
        val currentState = _uiState.value
        val totalElapsed = if (currentState.isRunning) {
            accumulatedTime + (System.currentTimeMillis() - startTime)
        } else {
            accumulatedTime
        }

        val lapTime = totalElapsed - lastLapTotalTime
        val newLapNumber = currentState.laps.size + 1
        
        val newLap = Lap(
            id = newLapNumber,
            lapNumber = newLapNumber,
            lapTimeMs = lapTime,
            totalTimeMs = totalElapsed
        )

        val updatedLaps = listOf(newLap) + currentState.laps
        lastLapTotalTime = totalElapsed

        _uiState.update { state ->
            state.copy(
                laps = updatedLaps,
                currentLapTimeMs = 0L
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
