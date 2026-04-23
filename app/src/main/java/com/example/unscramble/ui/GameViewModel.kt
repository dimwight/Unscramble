/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.unscramble.ui

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
class GameViewModel : ViewModel() {

    private val _gameState = MutableStateFlow(GameState())
    val uiState: StateFlow<GameState> = _gameState.asStateFlow()

    var userGuess by mutableStateOf("")
        private set

    fun updateGuess(guess: String){
        userGuess = guess
    }
    private var usedWords: MutableSet<String> = mutableSetOf()
    private lateinit var currentWord: String
    init {
        resetGame()
    }
    fun resetGame() {
        usedWords.clear()
        _gameState.value = GameState(currentScrambledWord = pickRandomWordAndShuffle())
    }
    fun checkGuess() {
        if (userGuess.equals(currentWord, ignoreCase = true)) {
            // User's guess is correct, increase the score
            // and call updateStateForScore() to prepare the game for next round
            val updatedScore = _gameState.value.score.plus(SCORE_INCREASE)
            updateStateForScore(updatedScore)
        } else {
            // User's guess is wrong, show an error
            _gameState.update { currentState ->
                currentState.copy(isGuessWrong = true)
            }
        }
        updateGuess("")
    }

    /*
     * Skip to next word
     */
    fun skipWord() {
        updateStateForScore(_gameState.value.score)
        // Reset user guess
        updateGuess("")
    }

    private fun updateStateForScore(score: Int) {
        if (usedWords.size == MAX_NO_OF_WORDS){
            _gameState.update { current ->
                current.copy(
                    isGuessWrong = false,
                    score = score,
                    isGameOver = true
                )
            }
        } else{
            // Normal round in the game
            _gameState.update { current ->
                current.copy(
                    isGuessWrong = false,
                    currentScrambledWord = pickRandomWordAndShuffle(),
                    currentWordCount = current.currentWordCount.inc(),
                    score = score
                )
            }
        }
    }

    private fun shuffleCurrentWord(word: String): String {
        val tempWord = word.toCharArray()
        // Scramble the word
        tempWord.shuffle()
        while (String(tempWord) == word) {
            tempWord.shuffle()
        }
        return String(tempWord)
    }

    private fun pickRandomWordAndShuffle(): String {
        // Continue picking up a new random word until you get one that hasn't been used before
        currentWord = allWords.random()
        return if (usedWords.contains(currentWord)) {
            pickRandomWordAndShuffle()
        } else {
            usedWords.add(currentWord)
            shuffleCurrentWord(currentWord)
        }
    }
}
