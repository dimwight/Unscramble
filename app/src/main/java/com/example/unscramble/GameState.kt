package com.example.unscramble

data class GameState(
    val badChar: Boolean = false,
    val hasGuessed: Boolean = false,
    val isSkip: Boolean = false
)