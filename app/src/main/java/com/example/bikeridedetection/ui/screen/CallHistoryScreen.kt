package com.example.bikeridedetection.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.PhoneMissed
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bikeridedetection.R
import com.example.bikeridedetection.domain.model.CallHistoryEntry
import com.example.bikeridedetection.ui.viewmodel.CallHistoryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallHistoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: CallHistoryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Mark entries as viewed when the user leaves the screen (not immediately)
    // This allows users to see the visual distinction between viewed/unviewed entries
    DisposableEffect(Unit) {
        onDispose {
            viewModel.markAllAsViewed()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.call_history_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_navigate_back),
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        CallHistoryContent(
            modifier = Modifier.padding(paddingValues),
            entries = uiState.entries,
            isLoading = uiState.isLoading,
        )
    }
}

@Composable
private fun CallHistoryContent(
    modifier: Modifier = Modifier,
    entries: List<CallHistoryEntry>,
    isLoading: Boolean,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
            }
            entries.isEmpty() -> {
                EmptyCallHistoryState(
                    modifier = Modifier.align(Alignment.Center),
                )
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(entries, key = { it.id }) { entry ->
                        CallHistoryItem(entry = entry)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyCallHistoryState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.PhoneMissed,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.call_history_empty),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun CallHistoryItem(entry: CallHistoryEntry) {
    // Determine colors based on viewed state for proper accessibility
    val containerColor =
        if (entry.isViewed) {
            MaterialTheme.colorScheme.surfaceVariant
        } else {
            MaterialTheme.colorScheme.primaryContainer
        }
    val contentColor =
        if (entry.isViewed) {
            MaterialTheme.colorScheme.onSurfaceVariant
        } else {
            MaterialTheme.colorScheme.onPrimaryContainer
        }
    val secondaryTextColor =
        if (entry.isViewed) {
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        } else {
            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
        }
    val iconTint =
        if (entry.isViewed) {
            MaterialTheme.colorScheme.onSurfaceVariant
        } else {
            MaterialTheme.colorScheme.primary
        }
    // Accent color for unviewed indicator
    val accentColor = MaterialTheme.colorScheme.primary
    // Border for unviewed entries
    val borderModifier =
        if (entry.isViewed) {
            Modifier
        } else {
            Modifier.border(
                width = 2.dp,
                color = accentColor,
                shape = MaterialTheme.shapes.medium,
            )
        }

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .then(borderModifier),
        colors =
            CardDefaults.cardColors(
                containerColor = containerColor,
                contentColor = contentColor,
            ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Icon with unviewed indicator dot
            Box {
                Icon(
                    imageVector =
                        if (entry.isFromContact) {
                            Icons.Default.Person
                        } else {
                            Icons.Default.PersonOff
                        },
                    contentDescription =
                        if (entry.isFromContact) {
                            stringResource(R.string.call_history_from_contact)
                        } else {
                            stringResource(R.string.call_history_unknown_caller)
                        },
                    modifier = Modifier.size(40.dp),
                    tint = iconTint,
                )
                // Show indicator dot for unviewed entries
                if (!entry.isViewed) {
                    Box(
                        modifier =
                            Modifier
                                .align(Alignment.TopEnd)
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(accentColor),
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = entry.phoneNumber,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (entry.isViewed) FontWeight.Normal else FontWeight.Bold,
                        color = contentColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    // "NEW" badge for unviewed entries
                    if (!entry.isViewed) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.call_history_new_badge),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier =
                                Modifier
                                    .background(
                                        color = accentColor,
                                        shape = MaterialTheme.shapes.small,
                                    ).padding(horizontal = 6.dp, vertical = 2.dp),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatTimestamp(entry.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = secondaryTextColor,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.call_history_auto_reply_sent),
                    style = MaterialTheme.typography.labelSmall,
                    color = secondaryTextColor,
                )
                Text(
                    text = "\"${entry.autoReplyMessage}\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
    return dateFormat.format(Date(timestamp))
}
