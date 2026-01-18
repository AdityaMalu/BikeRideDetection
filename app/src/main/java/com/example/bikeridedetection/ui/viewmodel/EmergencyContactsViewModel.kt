package com.example.bikeridedetection.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bikeridedetection.domain.model.EmergencyContact
import com.example.bikeridedetection.domain.usecase.AddEmergencyContactUseCase
import com.example.bikeridedetection.domain.usecase.GetEmergencyContactsUseCase
import com.example.bikeridedetection.domain.usecase.RemoveEmergencyContactUseCase
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
 * UI state for the emergency contacts screen.
 */
data class EmergencyContactsUiState(
    val contacts: List<EmergencyContact> = emptyList(),
    val isLoading: Boolean = true,
    val showAddDialog: Boolean = false,
    val showDeleteConfirmation: EmergencyContact? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null,
)

/**
 * ViewModel for the emergency contacts screen.
 */
@HiltViewModel
class EmergencyContactsViewModel
    @Inject
    constructor(
        private val getEmergencyContactsUseCase: GetEmergencyContactsUseCase,
        private val addEmergencyContactUseCase: AddEmergencyContactUseCase,
        private val removeEmergencyContactUseCase: RemoveEmergencyContactUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(EmergencyContactsUiState())
        val uiState: StateFlow<EmergencyContactsUiState> = _uiState.asStateFlow()

        init {
            observeContacts()
        }

        private fun observeContacts() {
            getEmergencyContactsUseCase()
                .onEach { contacts ->
                    _uiState.update {
                        it.copy(contacts = contacts, isLoading = false)
                    }
                }.catch { e ->
                    Timber.e(e, "Error observing emergency contacts")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to load emergency contacts",
                        )
                    }
                }.launchIn(viewModelScope)
        }

        /**
         * Shows the add contact dialog.
         */
        fun showAddDialog() {
            _uiState.update { it.copy(showAddDialog = true) }
        }

        /**
         * Hides the add contact dialog.
         */
        fun hideAddDialog() {
            _uiState.update { it.copy(showAddDialog = false) }
        }

        /**
         * Adds a new emergency contact.
         */
        fun addContact(
            phoneNumber: String,
            displayName: String,
            contactUri: String? = null,
        ) {
            viewModelScope.launch {
                try {
                    addEmergencyContactUseCase(phoneNumber, displayName, contactUri)
                    _uiState.update {
                        it.copy(
                            showAddDialog = false,
                            successMessage = "Contact added",
                        )
                    }
                    Timber.d("Emergency contact added: $displayName")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to add emergency contact")
                    _uiState.update {
                        it.copy(errorMessage = "Failed to add contact")
                    }
                }
            }
        }

        /**
         * Shows delete confirmation for a contact.
         */
        fun showDeleteConfirmation(contact: EmergencyContact) {
            _uiState.update { it.copy(showDeleteConfirmation = contact) }
        }

        /**
         * Hides delete confirmation dialog.
         */
        fun hideDeleteConfirmation() {
            _uiState.update { it.copy(showDeleteConfirmation = null) }
        }

        /**
         * Removes an emergency contact.
         */
        fun removeContact(contact: EmergencyContact) {
            viewModelScope.launch {
                try {
                    removeEmergencyContactUseCase(contact)
                    _uiState.update {
                        it.copy(
                            showDeleteConfirmation = null,
                            successMessage = "Contact removed",
                        )
                    }
                    Timber.d("Emergency contact removed: ${contact.displayName}")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to remove emergency contact")
                    _uiState.update {
                        it.copy(errorMessage = "Failed to remove contact")
                    }
                }
            }
        }

        /**
         * Clears the error message.
         */
        fun clearError() {
            _uiState.update { it.copy(errorMessage = null) }
        }

        /**
         * Clears the success message.
         */
        fun clearSuccess() {
            _uiState.update { it.copy(successMessage = null) }
        }
    }
