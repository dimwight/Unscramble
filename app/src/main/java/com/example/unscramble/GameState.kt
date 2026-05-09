package com.example.unscramble

data class GameState(
    val currentScramble: String = "",
    val currentCount: Int = 1,
    val score: Int = 0,
    val badChar: Boolean = false,
    val isGameOver: Boolean = false
)