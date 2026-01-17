package com.example.bikeridedetection.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bikeridedetection.domain.model.BikeMode
import com.example.bikeridedetection.domain.usecase.ObserveBikeModeUseCase
import com.example.bikeridedetection.domain.usecase.UpdateAutoReplyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * UI state for the settings screen.
 */
data class SettingsUiState(
    val autoReplyMessage: String = BikeMode.DEFAULT_AUTO_REPLY,
    val autoReplyValidationError: String? = null,
    val isAutoDetectEnabled: Boolean = true,
    val isCallBlockingEnabled: Boolean = true,
    val isSmsAutoReplyEnabled: Boolean = true,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null,
)

/**
 * ViewModel for the settings screen.
 * Manages app settings using StateFlow and coroutines.
 */
@HiltViewModel
class SettingsViewModel
    @Inject
    constructor(
        private val observeBikeModeUseCase: ObserveBikeModeUseCase,
        private val updateAutoReplyUseCase: UpdateAutoReplyUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(SettingsUiState())
        val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

        init {
            observeSettings()
        }

        private fun observeSettings() {
            observeBikeModeUseCase()
                .onEach { bikeMode ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            autoReplyMessage = bikeMode.autoReplyMessage,
                        )
                    }
                }.catch { e ->
                    Timber.e(e, "Error observing settings")
                    _uiState.update { it.copy(errorMessage = "Failed to load settings") }
                }.launchIn(viewModelScope)
        }

        /**
         * Updates the auto-reply message and clears any validation error.
         */
        fun updateAutoReplyMessage(message: String) {
            _uiState.update {
                it.copy(
                    autoReplyMessage = message,
                    autoReplyValidationError = null,
                )
            }
        }

        /**
         * Saves the current settings.
         * If the message is empty or whitespace, shows a validation warning
         * and saves the default message instead.
         */
        fun saveSettings() {
            viewModelScope.launch {
                _uiState.update {
                    it.copy(
                        isSaving = true,
                        saveSuccess = false,
                        autoReplyValidationError = null,
                    )
                }
                try {
                    val currentMessage = _uiState.value.autoReplyMessage
                    val isValid = updateAutoReplyUseCase(currentMessage)

                    if (!isValid) {
                        // Message was empty/whitespace, default was used
                        _uiState.update {
                            it.copy(
                                isSaving = false,
                                saveSuccess = true,
                                autoReplyValidationError = "Empty message replaced with default",
                                autoReplyMessage = BikeMode.DEFAULT_AUTO_REPLY,
                            )
                        }
                        Timber.d("Settings saved with default message (empty input)")
                    } else {
                        _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
                        Timber.d("Settings saved successfully")
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Failed to save settings")
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = "Failed to save settings",
                        )
                    }
                }
            }
        }

        /**
         * Clears the validation error for auto-reply message.
         */
        fun clearAutoReplyValidationError() {
            _uiState.update { it.copy(autoReplyValidationError = null) }
        }

        /**
         * Toggles auto-detect cycling feature.
         */
        fun toggleAutoDetect(enabled: Boolean) {
            _uiState.update { it.copy(isAutoDetectEnabled = enabled) }
        }

        /**
         * Toggles call blocking feature.
         */
        fun toggleCallBlocking(enabled: Boolean) {
            _uiState.update { it.copy(isCallBlockingEnabled = enabled) }
        }

        /**
         * Toggles SMS auto-reply feature.
         */
        fun toggleSmsAutoReply(enabled: Boolean) {
            _uiState.update { it.copy(isSmsAutoReplyEnabled = enabled) }
        }

        /**
         * Clears the error message.
         */
        fun clearError() {
            _uiState.update { it.copy(errorMessage = null) }
        }

        /**
         * Clears the save success flag.
         */
        fun clearSaveSuccess() {
            _uiState.update { it.copy(saveSuccess = false) }
        }
    }
