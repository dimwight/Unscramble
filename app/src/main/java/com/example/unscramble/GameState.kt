package com.example.unscramble

data class GameState(
    val currentCount: Int = 1,
    val score: Int = 0,
    val badChar: Boolean = false,
    val hasGuessed: Boolean = false,
    val isGameOver: Boolean = false
)