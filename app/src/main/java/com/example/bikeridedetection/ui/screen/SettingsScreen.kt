package com.example.bikeridedetection.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.PhoneDisabled
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bikeridedetection.R
import com.example.bikeridedetection.ui.components.SettingsToggleItem
import com.example.bikeridedetection.ui.viewmodel.SettingsUiState
import com.example.bikeridedetection.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            snackbarHostState.showSnackbar("Settings saved")
            viewModel.clearSaveSuccess()
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
                title = { Text(stringResource(R.string.settings_title)) },
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
        SettingsContent(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState,
            onAutoReplyChanged = viewModel::updateAutoReplyMessage,
            onAutoDetectToggled = viewModel::toggleAutoDetect,
            onCallBlockingToggled = viewModel::toggleCallBlocking,
            onSmsAutoReplyToggled = viewModel::toggleSmsAutoReply,
            onSaveSettings = viewModel::saveSettings,
        )
    }
}

@Composable
private fun SettingsContent(
    modifier: Modifier = Modifier,
    uiState: SettingsUiState,
    onAutoReplyChanged: (String) -> Unit,
    onAutoDetectToggled: (Boolean) -> Unit,
    onCallBlockingToggled: (Boolean) -> Unit,
    onSmsAutoReplyToggled: (Boolean) -> Unit,
    onSaveSettings: () -> Unit,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
    ) {
        // Auto Reply Section
        SettingsSectionHeader(title = stringResource(R.string.settings_section_auto_reply))

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.autoReplyMessage,
            onValueChange = { newValue ->
                onAutoReplyChanged(newValue)
                onSaveSettings()
            },
            label = { Text(stringResource(R.string.settings_auto_reply_label)) },
            placeholder = { Text(stringResource(R.string.settings_auto_reply_placeholder)) },
            supportingText =
                uiState.autoReplyValidationError?.let { error ->
                    { Text(text = error, color = MaterialTheme.colorScheme.error) }
                },
            isError = uiState.autoReplyValidationError != null,
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4,
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Features Section
        SettingsSectionHeader(title = stringResource(R.string.settings_section_features))

        Spacer(modifier = Modifier.height(8.dp))

        SettingsToggleItem(
            title = stringResource(R.string.settings_auto_detect_title),
            subtitle = stringResource(R.string.settings_auto_detect_subtitle),
            icon = Icons.AutoMirrored.Filled.DirectionsBike,
            isChecked = uiState.isAutoDetectEnabled,
            onCheckedChange = onAutoDetectToggled,
        )

        SettingsToggleItem(
            title = stringResource(R.string.settings_call_blocking_title),
            subtitle = stringResource(R.string.settings_call_blocking_subtitle),
            icon = Icons.Default.PhoneDisabled,
            isChecked = uiState.isCallBlockingEnabled,
            onCheckedChange = onCallBlockingToggled,
        )

        SettingsToggleItem(
            title = stringResource(R.string.settings_sms_reply_title),
            subtitle = stringResource(R.string.settings_sms_reply_subtitle),
            icon = Icons.AutoMirrored.Filled.Message,
            isChecked = uiState.isSmsAutoReplyEnabled,
            onCheckedChange = onSmsAutoReplyToggled,
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.semantics { heading() },
    )
}
