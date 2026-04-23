package com.example.unscramble

data class GameState(
    val currentWord: String = "",
    val currentCount: Int = 1,
    val score: Int = 0,
    val isGuessWrong: Boolean = false,
    val isGameOver: Boolean = false
)