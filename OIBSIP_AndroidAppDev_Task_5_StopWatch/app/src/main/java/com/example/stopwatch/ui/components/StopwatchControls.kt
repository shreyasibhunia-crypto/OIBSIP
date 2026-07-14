package com.example.stopwatch.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stopwatch.ui.theme.NeonCyan
import com.example.stopwatch.ui.theme.AccentTeal
import com.example.stopwatch.ui.theme.WarningAmber
import com.example.stopwatch.ui.theme.AccentRed

@Composable
fun StopwatchControls(
    isRunning: Boolean,
    isDisplayHeld: Boolean,
    elapsedTimeMs: Long,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onHold: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasStarted = elapsedTimeMs > 0

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. HOLD / RELEASE Button
        val holdButtonEnabled = isRunning
        val holdButtonText = if (isDisplayHeld) "RELEASE" else "HOLD"
        val holdButtonColor = when {
            !holdButtonEnabled -> AccentTeal.copy(alpha = 0.2f)
            isDisplayHeld -> WarningAmber
            else -> AccentTeal
        }
        val holdButtonContentColor = if (holdButtonEnabled) Color.Black else Color.Gray.copy(alpha = 0.5f)

        Button(
            onClick = onHold,
            enabled = holdButtonEnabled,
            shape = CircleShape,
            modifier = Modifier.size(85.dp),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = holdButtonColor,
                contentColor = holdButtonContentColor,
                disabledContainerColor = AccentTeal.copy(alpha = 0.15f),
                disabledContentColor = Color.Gray.copy(alpha = 0.4f)
            ),
            border = if (isDisplayHeld) BorderStroke(2.dp, Color.White) else null
        ) {
            Text(
                text = holdButtonText,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 0.5.sp,
                maxLines = 1,
                softWrap = false
            )
        }

        // 2. START / RESUME Button (Hero center button)
        val startButtonEnabled = !isRunning
        val startButtonText = if (hasStarted) "RESUME" else "START"
        val startButtonColor = if (startButtonEnabled) NeonCyan else NeonCyan.copy(alpha = 0.2f)
        val startButtonContentColor = if (startButtonEnabled) Color.Black else Color.Gray.copy(alpha = 0.5f)

        Button(
            onClick = onStart,
            enabled = startButtonEnabled,
            shape = CircleShape,
            modifier = Modifier.size(95.dp),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = startButtonColor,
                contentColor = startButtonContentColor,
                disabledContainerColor = NeonCyan.copy(alpha = 0.15f),
                disabledContentColor = Color.Gray.copy(alpha = 0.4f)
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 6.dp,
                pressedElevation = 2.dp
            )
        ) {
            Text(
                text = startButtonText,
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                letterSpacing = 0.5.sp,
                maxLines = 1,
                softWrap = false
            )
        }

        // 3. STOP / RESET Button
        val stopButtonEnabled = isRunning || hasStarted
        val stopButtonText = if (isRunning) "STOP" else "RESET"
        val stopButtonColor = when {
            !stopButtonEnabled -> AccentRed.copy(alpha = 0.2f)
            isRunning -> AccentRed
            else -> WarningAmber.copy(alpha = 0.8f) // Amber color for RESET action
        }
        val stopButtonContentColor = if (stopButtonEnabled) Color.Black else Color.Gray.copy(alpha = 0.5f)

        Button(
            onClick = onStop,
            enabled = stopButtonEnabled,
            shape = CircleShape,
            modifier = Modifier.size(85.dp),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = stopButtonColor,
                contentColor = stopButtonContentColor,
                disabledContainerColor = AccentRed.copy(alpha = 0.15f),
                disabledContentColor = Color.Gray.copy(alpha = 0.4f)
            )
        ) {
            Text(
                text = stopButtonText,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 0.5.sp,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}
