package com.example.stopwatch.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stopwatch.ui.components.LapHistoryList
import com.example.stopwatch.ui.components.StopwatchControls
import com.example.stopwatch.ui.components.StopwatchDisplay
import com.example.stopwatch.viewmodel.StopwatchViewModel

@Composable
fun StopwatchScreen(
    viewModel: StopwatchViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (isLandscape) {
            // Landscape Layout: Left (Stopwatch + Controls), Right (Lap History)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Column: Stopwatch & Controls
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1.1f)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    StopwatchDisplay(
                        elapsedTimeMs = if (uiState.isDisplayHeld) uiState.heldTimeMs else uiState.elapsedTimeMs,
                        isHeld = uiState.isDisplayHeld,
                        size = 200.dp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    StopwatchControls(
                        isRunning = uiState.isRunning,
                        isDisplayHeld = uiState.isDisplayHeld,
                        elapsedTimeMs = uiState.elapsedTimeMs,
                        onStart = viewModel::start,
                        onStop = viewModel::stop,
                        onHold = viewModel::toggleHold
                    )
                }

                // Subtle vertical divider between timer and history
                VerticalDivider(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                        .padding(vertical = 24.dp)
                )

                // Right Column: Lap history list
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1.3f)
                        .padding(16.dp)
                ) {
                    LapHistoryList(
                        laps = uiState.laps,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        } else {
            // Portrait Layout: Top (Stopwatch + Controls), Bottom (Lap History)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                StopwatchDisplay(
                    elapsedTimeMs = if (uiState.isDisplayHeld) uiState.heldTimeMs else uiState.elapsedTimeMs,
                    isHeld = uiState.isDisplayHeld,
                    size = 300.dp
                )

                Spacer(modifier = Modifier.height(8.dp))

                StopwatchControls(
                    isRunning = uiState.isRunning,
                    isDisplayHeld = uiState.isDisplayHeld,
                    elapsedTimeMs = uiState.elapsedTimeMs,
                    onStart = viewModel::start,
                    onStop = viewModel::stop,
                    onHold = viewModel::toggleHold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Scrollable Lap History List filling remaining space
                LapHistoryList(
                    laps = uiState.laps,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }
        }
    }
}
