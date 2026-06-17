package com.example.unscramble

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.unscramble.data.MAX_NO_OF_WORDS
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
        resetGame()
    }

    fun resetGame() {
        usedWords.clear()
        currentScramble = pickRandomWordAndShuffle()
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
//            val updatedScore = _gameState.value.score.plus(SCORE_INCREASE)
//            updateStateForScore()
            updateStateForGuessed()
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
        if (false){
            this.badChar=badChar
        }
        else
            _gameState.update {
            it.copy(
                badChar = badChar,
            )
        }
    }

    private fun updateStateForGuessed() {
        if (false && usedWords.size == MAX_NO_OF_WORDS) {
            _gameState.update {
                it.copy(
//                    score = score,
                    isGameOver = true
                )
            }
        } else {
            if (false){
                badChar = false
            }
            else
                _gameState.update {
                    it.copy(
                        hasGuessed = true,
                        badChar = false,
                    )
                }
        }

    }

    fun continueGame() {
        currentScramble = pickRandomWordAndShuffle()
        _gameState.update {
            it.copy(
                hasGuessed = false,
                currentCount = it.currentCount.inc(),
            )
        }
        guesses=0
        thenGuess = ""
        nowGuess = ""
        updateGuess("")
        inputBlocked = false
        println("R1: updateStateForScore- $inputBlocked")
    }

    fun skipWord() {
        updateStateForGuessed()
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










