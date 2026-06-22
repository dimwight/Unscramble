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

package com.example.unscramble

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.unscramble.ui.ClassicAlertDialog
import com.example.unscramble.ui.GameScreen
import com.example.unscramble.ui.theme.UnscrambleTheme

class MainActivity : ComponentActivity() {
    var wide = true

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        if (false)
            setContent {
                UnscrambleTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        GameScreen()
                    }
                }
            }
        else
            setContent {
                UnscrambleTheme() {
                    Scaffold(
                        modifier = Modifier.fillMaxSize()
                    ) { innerPadding ->
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            var showDialog by rememberSaveable {
                                mutableStateOf(true)
                            }
                            if (false)
                            Column() {
                                Button(
                                    onClick = {
                                        wide=true
                                        showDialog = true
                                    }
                                ) {
                                    Text("Show wide")
                                }
                                Button(
                                    onClick = {
                                        wide=false
                                        showDialog = true
                                    }
                                ) {
                                    Text("Show tall")
                                }

                            }
                            if (showDialog) {
                                ClassicAlertDialog(
                                    title = {
                                        Text("Title")
                                    },
                                    text = {
                                        Text("Message")
                                    },
                                    onDismissRequest = {
                                        finish()
//                                        showDialog = false
                                    },
                                    positiveButton = {
                                        TextButton(
                                            onClick = {
                                                showDialog = false
                                            }
                                        ) {
                                            Text("Positive")
                                        }
                                    },
                                    negativeButton = {
                                        TextButton(
                                            onClick = {
                                                showDialog = false
                                            }
                                        ) {
                                            Text("Negative")
                                        }
                                    },
                                    neutralButton = {
                                        TextButton(
                                            onClick = {
                                                showDialog = false
                                            }
                                        ) {
                                            Text("Neutral")
                                        }
                                    },
                                )
                            }
                        }
                    }
                }
            }
    }
}
