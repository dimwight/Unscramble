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

    var currentScramble: String=""
        private set
    var guesses = 0
        private set
    var inputBlocked = false
        private set
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()
    var thenGuess =""
        private set
    var guess by mutableStateOf("")
        private set

    private var usedWords: MutableSet<String> = mutableSetOf()
    var currentWord =""
        private set

    init {
        updateScramble()
    }

    fun updateGuess(update: String) {
        if (inputBlocked) return
        thenGuess = guess
        guess = update.trim()
        inputBlocked = true
    }

    fun checkGuess() {
        inputBlocked = false
        guesses++
        if (guess.equals(currentWord, ignoreCase = true)) {
            _gameState.update {
                it.copy(
                    hasGuessed = true,
                    badChar = false,
                )
            }
            return
        }
        val guessChars = guess.toCharArray()
        val wordChars = currentWord.toCharArray()
        var badChar = false
        for ((at, char) in guessChars.withIndex()) {
            if (char != wordChars[at]) {
                badChar = true
                guess = thenGuess
                break
            }
        }
        val listOf = listOf(thenGuess, guess)
        println("R1: listOf = $listOf")
        _gameState.update {
            it.copy(
                badChar = badChar,
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
        guess = ""
        updateGuess("")
        inputBlocked = false
    }

    fun skip() {
        _gameState.update {
            it.copy(
                isSkip = true
            )
        }
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
private const val debug = false










