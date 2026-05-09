/*
 * Copyright (c)2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.unscramble.ui.test

import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.getUnscrambledWord
import com.example.unscramble.GameModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GameViewModelTest {
    private val viewModel = GameModel()

    @Test
    fun gameViewModel_Initialization_FirstWordLoaded() {
        /**
         *  Warning: This way to retrieve the uiState works because MutableStateFlow is used. In the
         *  upcoming units you will learn about advanced usages of StateFlow that creates a stream
         *  of data and you need to react to handle the stream. For those scenarios you will write
         *  unit tests using different methods/approaches. This applies to all the usages of
         *  viewModel.uiState.value in this class.
         **/
        val gameUiState = viewModel.gameState.value
        val unScrambledWord = getUnscrambledWord(gameUiState.currentScramble)

        // Assert that current word is scrambled.
        assertNotEquals(unScrambledWord, gameUiState.currentScramble)
        // Assert that current word count is set to 1.
        assertTrue(gameUiState.currentCount == 1)
        // Assert that initially the score is 0.
        assertTrue(gameUiState.score == 0)
        // Assert that wrong word guessed is false.
        assertFalse(gameUiState.badChar)
        // Assert that game is not over.
        assertFalse(gameUiState.isGameOver)
    }

    @Test
    fun gameViewModel_IncorrectGuess_ErrorFlagSet() {
        // Given an incorrect word as input
        val incorrectPlayerWord = "and"

        viewModel.updateGuess(incorrectPlayerWord)
        viewModel.checkGuess()

        val currentGameUiState = viewModel.gameState.value
        // Assert that score is unchanged
        assertEquals(0, currentGameUiState.score)
        // Assert that checkGuess() method updates isGuessedWordWrong correctly
        assertTrue(currentGameUiState.badChar)
    }

    @Test
    fun gameViewModel_CorrectWordGuessed_ScoreUpdatedAndErrorFlagUnset() {
        var currentGameUiState = viewModel.gameState.value
        val correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScramble)

        viewModel.updateGuess(correctPlayerWord)
        viewModel.checkGuess()
        currentGameUiState = viewModel.gameState.value

        // Assert that checkGuess() method updates isGuessedWordWrong is updated correctly.
        assertFalse(currentGameUiState.badChar)
        // Assert that score is updated correctly.
        assertEquals(SCORE_AFTER_FIRST_CORRECT_ANSWER, currentGameUiState.score)
    }

    @Test
    fun gameViewModel_WordSkipped_ScoreUnchangedAndWordCountIncreased() {
        var currentGameUiState = viewModel.gameState.value
        val correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScramble)

        viewModel.updateGuess(correctPlayerWord)
        viewModel.checkGuess()
        currentGameUiState = viewModel.gameState.value
        val lastWordCount = currentGameUiState.currentCount

        viewModel.skipWord()
        currentGameUiState = viewModel.gameState.value
        // Assert that score remains unchanged after word is skipped.
        assertEquals(SCORE_AFTER_FIRST_CORRECT_ANSWER, currentGameUiState.score)
        // Assert that word count is increased by 1 after word is skipped.
        assertEquals(lastWordCount + 1, currentGameUiState.currentCount)
    }

    @Test
    fun gameViewModel_AllWordsGuessed_UiStateUpdatedCorrectly() {
        var expectedScore = 0
        var currentGameUiState = viewModel.gameState.value
        var correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScramble)

        repeat(MAX_NO_OF_WORDS) {
            expectedScore += SCORE_INCREASE
            viewModel.updateGuess(correctPlayerWord)
            viewModel.checkGuess()
            currentGameUiState = viewModel.gameState.value
            correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScramble)
            // Assert that after each correct answer, score is updated correctly.
            assertEquals(expectedScore, currentGameUiState.score)
        }
        // Assert that after all questions are answered, the current word count is up-to-date.
        assertEquals(MAX_NO_OF_WORDS, currentGameUiState.currentCount)
        // Assert that after 10 questions are answered, the game is over.
        assertTrue(currentGameUiState.isGameOver)
    }

    companion object {
        private const val SCORE_AFTER_FIRST_CORRECT_ANSWER = SCORE_INCREASE
    }
}
