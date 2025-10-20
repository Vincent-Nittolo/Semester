package com.example.semester

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlin.random.Random

class MainViewModel : ViewModel() {

    var currentPage = mutableIntStateOf(1)
    var timeLeft = mutableIntStateOf(10)
    var navigate = mutableIntStateOf(1)
    var gold = mutableIntStateOf(0)
    var collected = mutableStateListOf<Int>().apply {
        repeat(100) { add(0) }
    }

    val caught = mutableListOf<Int>()
    private var timerJob: Job? = null

    val colorList = listOf(
        Color(0xFFE57373), // Red 300
        Color(0xFFF06292), // Pink 300
        Color(0xFFBA68C8), // Purple 300
        Color(0xFF9575CD), // Deep Purple 300
        Color(0xFF7986CB), // Indigo 300
        Color(0xFF64B5F6), // Blue 300
        Color(0xFF4DD0E1), // Cyan 300
        Color(0xFF4DB6AC), // Teal 300
        Color(0xFF81C784), // Green 300
        Color(0xFFDCE775)  // Lime 300
    )

    fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (timeLeft.intValue > 0) {
                delay(1000L)
                timeLeft.intValue -= 1
            }
        }
    }

    fun resetTimer(seconds: Int = 10) {
        timeLeft.intValue = seconds
        startTimer()
    }

    fun handleCatch(randomNumber: Int) {
        caught.add(randomNumber)
        collected[randomNumber] += 1
        navigate.intValue = 1
        resetTimer()
    }

    fun generateRandomEncounter(): Int {
        return Random.nextInt(0, 100)
    }

    fun increaseGold(amount: Int) {
        gold.intValue += amount
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
