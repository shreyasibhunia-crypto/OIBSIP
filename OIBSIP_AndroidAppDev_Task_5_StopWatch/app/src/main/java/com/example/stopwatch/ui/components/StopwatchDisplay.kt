package com.example.stopwatch.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stopwatch.ui.theme.NeonCyan
import com.example.stopwatch.ui.theme.AccentTeal
import com.example.stopwatch.ui.theme.DarkSurface
import com.example.stopwatch.ui.theme.WarningAmber
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun StopwatchDisplay(
    elapsedTimeMs: Long,
    isHeld: Boolean = false,
    modifier: Modifier = Modifier,
    size: Dp = 300.dp
) {
    val hours = (elapsedTimeMs / 3600000)
    val minutes = (elapsedTimeMs % 3600000) / 60000
    val seconds = (elapsedTimeMs % 60000) / 1000
    val milliseconds = (elapsedTimeMs % 1000) / 10

    // Sweep angles
    val secondFraction = (elapsedTimeMs % 60000) / 60000f
    val milliFraction = (elapsedTimeMs % 1000) / 1000f

    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .size(size)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.toPx() / 2f, size.toPx() / 2f)
            val radius = (size.toPx() / 2f) - 16f

            // 1. Draw outer background circle
            drawCircle(
                color = DarkSurface,
                radius = radius,
                center = center
            )

            // 2. Draw subtle tick marks (60 ticks representing seconds)
            for (i in 0 until 60) {
                val angleRad = Math.toRadians((i * 6).toDouble() - 90.0)
                val isMajor = i % 5 == 0
                val tickLength = if (isMajor) 20f else 10f
                val tickWidth = if (isMajor) 3.5f else 1.5f
                val tickColor = if (isMajor) NeonCyan.copy(alpha = 0.5f) else Color.Gray.copy(alpha = 0.2f)

                val startX = center.x + (radius - 8f - tickLength) * cos(angleRad).toFloat()
                val startY = center.y + (radius - 8f - tickLength) * sin(angleRad).toFloat()
                val endX = center.x + (radius - 8f) * cos(angleRad).toFloat()
                val endY = center.y + (radius - 8f) * sin(angleRad).toFloat()

                drawLine(
                    color = tickColor,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = tickWidth,
                    cap = StrokeCap.Round
                )
            }

            // 3. Draw Millisecond outer progress ring (glowing trace)
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(AccentTeal.copy(alpha = 0.1f), NeonCyan, AccentTeal)
                ),
                startAngle = -90f,
                sweepAngle = milliFraction * 360f,
                useCenter = false,
                style = Stroke(width = 6f, cap = StrokeCap.Round)
            )

            // 4. Draw Seconds tracker indicator (glow dot)
            val secondAngleRad = Math.toRadians((secondFraction * 360.0) - 90.0)
            val indicatorRadius = radius - 4f
            val indX = center.x + indicatorRadius * cos(secondAngleRad).toFloat()
            val indY = center.y + indicatorRadius * sin(secondAngleRad).toFloat()

            // Outer pulse glow
            drawCircle(
                color = NeonCyan.copy(alpha = 0.3f * pulseScale),
                radius = 16f,
                center = Offset(indX, indY)
            )
            // Inner core indicator
            drawCircle(
                color = Color.White,
                radius = 6f,
                center = Offset(indX, indY)
            )
            drawCircle(
                color = NeonCyan,
                radius = 4f,
                center = Offset(indX, indY)
            )
        }

        // Digital Layout inside the circle
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isHeld) "DISPLAY HELD" else "ELAPSED TIME",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = if (isHeld) WarningAmber else NeonCyan.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            )
            
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                // Hours & Minutes & Seconds
                Text(
                    text = String.format("%02d:%02d:%02d", hours, minutes, seconds),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 36.sp
                    )
                )
                // Milliseconds smaller
                Text(
                    text = String.format(".%02d", milliseconds),
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = AccentTeal
                    ),
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle mapping values
            Text(
                text = "HH   MM   SS   MS",
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    color = Color.Gray.copy(alpha = 0.7f),
                    letterSpacing = 3.5.sp
                )
            )
        }
    }
}
