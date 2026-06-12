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

class GameModel: ViewModel() {

    var guessing=false
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    var thenGuess by mutableStateOf("")
        private set
    var nowGuess by mutableStateOf("")
        private set

    private var usedWords: MutableSet<String> = mutableSetOf()
    private lateinit var currentWord: String

    init {
        resetGame()
    }

    fun resetGame() {
        usedWords.clear()
        _gameState.value = GameState(currentScramble = pickRandomWordAndShuffle())
    }

    fun updateGuess(update: String) {
        println("R1: update = $update")
        println("R1: updateGuess $guessing")
        if (guessing)return
        thenGuess = nowGuess
        nowGuess = update.trim()
        guessing=true
        println("R1: updateGuess- $guessing")
    }

    fun checkGuess() {
        guessing=false
        println("R1: checkGuess $guessing")
        if (nowGuess.equals(currentWord, ignoreCase = true)) {
            val updatedScore = _gameState.value.score.plus(SCORE_INCREASE)
            updateStateForScore(updatedScore)
            return
        }
        var guessChars = nowGuess.toCharArray()
        val wordChars = currentWord.toCharArray()
        var badChar = false
        for (at in 0..guessChars.size - 1) {
            if (guessChars[at] != wordChars[at]) {
                badChar = true
                nowGuess=thenGuess
                break
            }
        }
       val listOf = listOf(thenGuess, nowGuess)
        println("R1: listOf = $listOf")
        _gameState.update { currentState ->
            currentState.copy(badChar = badChar)
        }
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
        thenGuess=""
        nowGuess=""
        updateGuess("")
        guessing=false
        println("R1: updateStateForScore- $guessing")
    }

    fun skipWord() {
        updateStateForScore(_gameState.value.score)
        nowGuess = ""
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
        currentWord = if (debug) "dance" else
            allWords.random().trim()
        return if (usedWords.contains(currentWord)) {
            pickRandomWordAndShuffle()
        } else {
            if (!debug) usedWords.add(currentWord)
            shuffleCurrentWord(currentWord)
        }
    }
}










