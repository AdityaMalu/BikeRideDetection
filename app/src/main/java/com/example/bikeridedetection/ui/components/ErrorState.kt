@file:Suppress("MatchingDeclarationName")

package com.example.bikeridedetection.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.bikeridedetection.R

/**
 * Sealed class representing different types of errors.
 */
sealed class ErrorType {
    data object Network : ErrorType()

    data object Permission : ErrorType()

    data class Generic(
        val message: String? = null,
    ) : ErrorType()
}

/**
 * A composable that displays an error state with an icon, message, and retry button.
 *
 * @param errorType The type of error to display
 * @param onRetry Callback when retry button is clicked
 * @param onOpenSettings Optional callback for opening settings (for permission errors)
 * @param modifier Modifier for the component
 */
@Composable
fun ErrorState(
    errorType: ErrorType,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenSettings: (() -> Unit)? = null,
) {
    val (icon, title, message) =
        when (errorType) {
            is ErrorType.Network ->
                Triple(
                    Icons.Filled.WifiOff,
                    stringResource(R.string.error_title),
                    stringResource(R.string.error_network),
                )
            is ErrorType.Permission ->
                Triple(
                    Icons.Filled.Settings,
                    stringResource(R.string.error_title),
                    stringResource(R.string.error_permission_denied),
                )
            is ErrorType.Generic ->
                Triple(
                    Icons.Filled.Error,
                    stringResource(R.string.error_title),
                    errorType.message ?: stringResource(R.string.error_network),
                )
        }

    ErrorStateContent(
        icon = icon,
        title = title,
        message = message,
        onRetry = onRetry,
        onOpenSettings = if (errorType is ErrorType.Permission) onOpenSettings else null,
        modifier = modifier,
    )
}

@Composable
private fun ErrorStateContent(
    icon: ImageVector,
    title: String,
    message: String,
    onRetry: () -> Unit,
    onOpenSettings: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(24.dp).semantics { contentDescription = "$title. $message" },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRetry) {
            Icon(imageVector = Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.size(8.dp))
            Text(stringResource(R.string.error_retry))
        }
        if (onOpenSettings != null) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(onClick = onOpenSettings) {
                Icon(imageVector = Icons.Filled.Settings, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.size(8.dp))
                Text(stringResource(R.string.error_open_settings))
            }
        }
    }
}

/**
 * A compact inline error message for use in forms or lists.
 */
@Composable
fun InlineError(
    message: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = message,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.error,
        modifier = modifier.padding(vertical = 4.dp),
    )
}
