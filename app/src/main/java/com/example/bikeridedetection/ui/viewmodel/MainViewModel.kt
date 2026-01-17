package com.example.bikeridedetection.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bikeridedetection.domain.model.BikeMode
import com.example.bikeridedetection.domain.usecase.ObserveBikeModeUseCase
import com.example.bikeridedetection.domain.usecase.SetBikeModeEnabledUseCase
import com.example.bikeridedetection.domain.usecase.ToggleBikeModeUseCase
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
 * UI state for the main screen.
 */
data class MainUiState(
    val bikeMode: BikeMode = BikeMode(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel for the main screen.
 * Manages bike mode state using StateFlow and coroutines.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val observeBikeModeUseCase: ObserveBikeModeUseCase,
    private val setBikeModeEnabledUseCase: SetBikeModeEnabledUseCase,
    private val toggleBikeModeUseCase: ToggleBikeModeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        observeBikeMode()
    }

    private fun observeBikeMode() {
        observeBikeModeUseCase()
            .onEach { bikeMode ->
                _uiState.update { it.copy(bikeMode = bikeMode, isLoading = false) }
            }
            .catch { error ->
                Timber.e(error, "Error observing bike mode")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load bike mode settings"
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Sets whether bike mode is enabled.
     *
     * @param enabled Whether bike mode should be enabled
     */
    fun setBikeModeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                setBikeModeEnabledUseCase(enabled)
                Timber.d("Bike mode set to: $enabled")
            } catch (e: Exception) {
                Timber.e(e, "Failed to set bike mode")
                _uiState.update {
                    it.copy(errorMessage = "Failed to update bike mode")
                }
            }
        }
    }

    /**
     * Toggles the current bike mode state.
     */
    fun toggleBikeMode() {
        viewModelScope.launch {
            try {
                toggleBikeModeUseCase()
                Timber.d("Bike mode toggled")
            } catch (e: Exception) {
                Timber.e(e, "Failed to toggle bike mode")
                _uiState.update {
                    it.copy(errorMessage = "Failed to toggle bike mode")
                }
            }
        }
    }

    /**
     * Clears the current error message.
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

