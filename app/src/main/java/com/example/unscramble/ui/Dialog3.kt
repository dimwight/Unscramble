package com.example.unscramble.ui

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

/**
 * Classic version of the dialog with positive, negative and neutral buttons.
 * They are placed next to each other when they fit and vertically when they don't fit.
 *
 * You still need to dismiss the dialog manually after clicking on dialog action.
 *
 * Note: If you need more buttons than "is allowed" simply pass more of them into the same slot.
 */
@Composable
fun Dialog3(
    onDismissRequest: () -> Unit,
    positiveButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    negativeButton: @Composable (() -> Unit)? = null,
    neutralButton: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    shape: Shape = AlertDialogDefaults.shape,
    containerColor: Color = AlertDialogDefaults.containerColor,
    iconContentColor: Color = AlertDialogDefaults.iconContentColor,
    titleContentColor: Color = AlertDialogDefaults.titleContentColor,
    textContentColor: Color = AlertDialogDefaults.textContentColor,
    tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
    properties: DialogProperties = DialogProperties()
) {

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            // To show 3 buttons we need to put them into one button slot because the official
            // version stopped supporting 3 buttons for some reason.
            AlertDialogButtonStack {
                positiveButton.invoke()
                negativeButton?.invoke()
                // If we don't have the negative button we want to place an empty view here to
                // trigger the logic of pushing neutral button to the side because there are "3 buttons".
                if (negativeButton == null && neutralButton != null) {
                    Spacer(Modifier.size(0.dp))
                }
                neutralButton?.invoke()
            }
        },
        modifier = modifier,
        // We put all buttons inside the confirm button slot.
        dismissButton = null,
        icon = icon,
        title = title,
        text = text,
        shape = shape,
        containerColor = containerColor,
        iconContentColor = iconContentColor,
        titleContentColor = titleContentColor,
        textContentColor = textContentColor,
        tonalElevation = tonalElevation,
        properties = properties
    )
}

/**
 * Custom layout for stacking buttons as per the material design spec.
 * If all buttons fit on one line. Place them like that.
 * When they don't fit, put them vertically below each other.
 *
 * We need to implement this manually because the compose material implementation does not follow these guidelines and places buttons like [FlowRow].
 */
@Composable
private fun AlertDialogButtonStack(
    modifier: Modifier = Modifier,
    buttons: @Composable () -> Unit,
) {
    val wide =false

    Layout(
        modifier = modifier,
        content = buttons
    ) { measurables, constraints ->
        // Don't constrain child views further, measure them with given constraints.
        val placeables = measurables.map { measurable ->
            // Measure each child
            measurable.measure(constraints)
        }

        // Calculate amount of required space for the buttons.
        val widthOfButtons = placeables.sumOf { placeable ->
            placeable.width
        }

        if (wide&& widthOfButtons < constraints.maxWidth) {
            // Calculate height of the whole button row.
            val maxHeight = placeables.maxOf { placeable ->
                placeable.height
            }

            // When all buttons fit horizontally, place them like that.
            // Place buttons from the end to the start of the layout.
            layout(constraints.maxWidth, maxHeight) {
                // Track the current X coordinate for placing children.
                var xPosition = constraints.maxWidth

                // Place children in the parent layout.
                placeables.forEachIndexed { index, placeable ->
                    if (index == 2 && index == placeables.lastIndex) {
                        // When we place the third button horizontally,
                        // it is a neutral button and it should be pushed to the side.
                        placeable.placeRelative(
                            x = 0,
                            y = 0
                        )
                    } else {
                        // Normal buttons are placed next to each other.
                        placeable.placeRelative(
                            x = xPosition - placeable.width,
                            y = 0
                        )
                    }

                    // Move the X coordinate by the currently placed button.
                    xPosition -= placeable.width
                    println("R1: xPosition = $xPosition")
                }
            }
        } else {
            val heightOfButtons = placeables.sumOf { placeable ->
                placeable.height
            }

            // When all buttons don't fit. Place them vertically.
            layout(constraints.maxWidth, heightOfButtons) {
                // Track the Y coordinate we have placed children up to.
                var yPosition = 0

                // Place children in the parent layout.
                placeables.forEach { placeable ->
                    // Position item on the screen.
                    placeable.placeRelative(
                        x = constraints.maxWidth - placeable.width,
                        y = yPosition
                    )

                    // Move the Y coordinate by the currently placed button.
                    yPosition += placeable.height
                    println("R1: yPosition = $yPosition")
                }
            }
        }
    }
}



