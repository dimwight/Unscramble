package com.example.unscramble

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel containing the app data and methods to process the data
 */
class GameModel : ViewModel() {

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    var thenGuess by mutableStateOf("")
        private set
    var nowGuess by mutableStateOf("")
        private set

    fun updateGuess(guess: String) {
        thenGuess = nowGuess
        nowGuess = guess.trim()
    }

    private var usedWords: MutableSet<String> = mutableSetOf()
    private lateinit var currentWord: String

    init {
        resetGame()
    }

    fun resetGame() {
        usedWords.clear()
        _gameState.value = GameState(currentScramble = pickRandomWordAndShuffle())
    }

    fun checkGuess() {
        if (nowGuess.equals(currentWord, ignoreCase = true)) {
            val updatedScore = _gameState.value.score.plus(SCORE_INCREASE)
            updateStateForScore(updatedScore)
            return
        }
        var guessChars = nowGuess.trim().toCharArray()
        val wordChars = currentWord.trim().toCharArray()
        var badChar = false
        val wordSize = wordChars.size
        if (guessChars.size > wordSize) {
            guessChars = guessChars.slice(0..wordSize - 1)
                .toCharArray()
            badChar = true
        } else {
            for (at in 0..guessChars.size - 1) {
                if (guessChars[at] != wordChars[at]) {
                    guessChars = guessChars.slice(0..at - 1).toCharArray()
                    badChar = true
                    break
                }
            }
        }
        _gameState.update { currentState ->
            currentState.copy(badChar = badChar)
        }
        updateGuess(String(guessChars))
    }

    /*
     * Skip to next word
     */
    fun skipWord() {
        updateStateForScore(_gameState.value.score)
        updateGuess("")
    }

    private fun updateStateForScore(score: Int) {
        if (usedWords.size == MAX_NO_OF_WORDS) {
            _gameState.update { current ->
                current.copy(
                    badChar = false,
                    score = score,
                    isGameOver = true
                )
            }
        } else {
            _gameState.update { current ->
                current.copy(
                    badChar = false,
                    currentScramble = pickRandomWordAndShuffle(),
                    currentCount = current.currentCount.inc(),
                    score = score
                )
            }
        }
        updateGuess("")
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
        val debug = false
        currentWord = if (debug) "abc" else
            allWords.random()
        return if (usedWords.contains(currentWord)) {
            pickRandomWordAndShuffle()
        } else {
            if (!debug) usedWords.add(currentWord)
            shuffleCurrentWord(currentWord)
        }
    }
}










