package com.example.unscramble

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.unscramble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameModel : ViewModel() {

    var badChar=false
    var currentScramble: String=""
    var guesses = 0
    var inputBlocked = false
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    var thenGuess by mutableStateOf("")
        private set
    var nowGuess by mutableStateOf("")
        private set

    private var usedWords: MutableSet<String> = mutableSetOf()
    var currentWord: String=""

    init {
        updateScramble()
    }

    fun updateGuess(update: String) {
        println("R1: update = $update")
        println("R1: updateGuess $inputBlocked")
        if (inputBlocked) return
        thenGuess = nowGuess
        nowGuess = update.trim()
        inputBlocked = true
        println("R1: updateGuess- $inputBlocked")
    }

    fun checkGuess() {
        inputBlocked = false
        println("R1: checkGuess $inputBlocked")
        guesses++
        if (nowGuess.equals(currentWord, ignoreCase = true)) {
            updateStateForGuessed(true)
            return
        }
        val guessChars = nowGuess.toCharArray()
        val wordChars = currentWord.toCharArray()
        var badChar = false
        for (at in 0..<guessChars.size) {
            if (guessChars[at] != wordChars[at]) {
                badChar = true
                nowGuess = thenGuess
                break
            }
        }
        val listOf = listOf(thenGuess, nowGuess)
        println("R1: listOf = $listOf")
        _gameState.update {
            it.copy(
                badChar = badChar,
            )
        }
    }

    private fun updateStateForGuessed(guessed: Boolean) {
        _gameState.update {
            it.copy(
                hasGuessed = guessed,
                badChar = false,
                isSkip = !guessed
            )
        }
    }

    fun updateScramble() {
        currentScramble = pickRandomWordAndShuffle()
        _gameState.update {
            it.copy(
                hasGuessed = false,
                isSkip = false,
                badChar = false,
            )
        }
        guesses=0
        thenGuess = ""
        nowGuess = ""
        updateGuess("")
        inputBlocked = false
        println("R1: - $inputBlocked")
    }

    fun skip() {
        _gameState.update {
            it.copy(
//                hasGuessed = false,
//                badChar = false,
                isSkip = true
            )
        }
//        nowGuess = ""
    }

    fun unskip() {
        _gameState.update {
            it.copy(
                isSkip = false
            )
        }

    }
    private fun shuffleCurrentWord(word: String): String {
        val asChars = word.toCharArray()
        asChars.shuffle()
        while (String(asChars) == word) {
            asChars.shuffle()
        }
        return String(asChars)
    }

    private fun pickRandomWordAndShuffle(): String {
        val debug = true
        currentWord = if (debug) "abc" else
            allWords.random().trim()
        return if (usedWords.contains(currentWord)) {
            pickRandomWordAndShuffle()
        } else {
            if (!debug) usedWords.add(currentWord)
            shuffleCurrentWord(currentWord)
        }
    }
}










