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
         * Updates the auto-reply message.
         */
        fun updateAutoReplyMessage(message: String) {
            _uiState.update { it.copy(autoReplyMessage = message) }
        }

        /**
         * Saves the current settings.
         */
        fun saveSettings() {
            viewModelScope.launch {
                _uiState.update { it.copy(isSaving = true, saveSuccess = false) }
                try {
                    updateAutoReplyUseCase(_uiState.value.autoReplyMessage)
                    _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
                    Timber.d("Settings saved successfully")
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
