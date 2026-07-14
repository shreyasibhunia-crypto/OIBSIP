package com.example.stopwatch.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stopwatch.model.Lap
import com.example.stopwatch.ui.theme.DarkSurface
import com.example.stopwatch.ui.theme.NeonCyan
import com.example.stopwatch.ui.theme.TextPrimary
import com.example.stopwatch.ui.theme.TextSecondary

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LapHistoryList(
    laps: List<Lap>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (laps.isNotEmpty()) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LAP",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "LAP TIME",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier
                        .weight(2f)
                        .wrapContentWidth(align = Alignment.End)
                )
                Text(
                    text = "TOTAL TIME",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier
                        .weight(2f)
                        .wrapContentWidth(align = Alignment.End)
                )
            }
            HorizontalDivider(
                color = Color.Gray.copy(alpha = 0.2f),
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(
                items = laps,
                key = { it.id }
            ) { lap ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItemPlacement()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Lap number
                    Text(
                        text = String.format("Lap %02d", lap.lapNumber),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = NeonCyan,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    // Lap duration (Interval)
                    Text(
                        text = formatLapDuration(lap.lapTimeMs),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier
                            .weight(2f)
                            .wrapContentWidth(align = Alignment.End)
                    )

                    // Cumulative total time
                    Text(
                        text = formatLapDuration(lap.totalTimeMs),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary
                        ),
                        modifier = Modifier
                            .weight(2f)
                            .wrapContentWidth(align = Alignment.End)
                    )
                }
                
                HorizontalDivider(
                    color = Color.Gray.copy(alpha = 0.1f),
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}


private fun formatLapDuration(timeMs: Long): String {
    val hours = timeMs / 3600000
    val minutes = (timeMs % 3600000) / 60000
    val seconds = (timeMs % 60000) / 1000
    val milliseconds = (timeMs % 1000) / 10

    return if (hours > 0) {
        String.format("%02d:%02d:%02d.%02d", hours, minutes, seconds, milliseconds)
    } else {
        String.format("%02d:%02d.%02d", minutes, seconds, milliseconds)
    }
}
