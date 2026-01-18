package com.example.bikeridedetection.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bikeridedetection.domain.model.CallHistoryEntry
import com.example.bikeridedetection.domain.usecase.GetCallHistoryUseCase
import com.example.bikeridedetection.domain.usecase.MarkCallsAsViewedUseCase
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
 * UI state for the call history screen.
 */
data class CallHistoryUiState(
    val entries: List<CallHistoryEntry> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

/**
 * ViewModel for the call history screen.
 * Manages call history state and marks entries as viewed.
 */
@HiltViewModel
class CallHistoryViewModel
    @Inject
    constructor(
        private val getCallHistoryUseCase: GetCallHistoryUseCase,
        private val markCallsAsViewedUseCase: MarkCallsAsViewedUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(CallHistoryUiState())
        val uiState: StateFlow<CallHistoryUiState> = _uiState.asStateFlow()

        init {
            observeCallHistory()
        }

        private fun observeCallHistory() {
            Timber.d("Starting to observe call history")
            getCallHistoryUseCase()
                .onEach { entries ->
                    Timber.d("Received ${entries.size} call history entries")
                    entries.forEach { entry ->
                        Timber.d("Entry: id=${entry.id}, phone=${entry.phoneNumber}, timestamp=${entry.timestamp}")
                    }
                    _uiState.update {
                        it.copy(
                            entries = entries,
                            isLoading = false,
                        )
                    }
                }.catch { error ->
                    Timber.e(error, "Error loading call history")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to load call history",
                        )
                    }
                }.launchIn(viewModelScope)
        }

        /**
         * Marks all unviewed entries as viewed.
         * Should be called when the user opens the call history screen.
         */
        fun markAllAsViewed() {
            viewModelScope.launch {
                try {
                    markCallsAsViewedUseCase()
                    Timber.d("All entries marked as viewed")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to mark entries as viewed")
                }
            }
        }

        /**
         * Clears the error message.
         */
        fun clearError() {
            _uiState.update { it.copy(errorMessage = null) }
        }
    }
