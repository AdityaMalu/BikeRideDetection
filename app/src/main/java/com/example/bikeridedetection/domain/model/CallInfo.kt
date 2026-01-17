package com.example.bikeridedetection.domain.model

/**
 * Represents information about an incoming call.
 *
 * @property phoneNumber The phone number of the caller
 * @property isFromContact Whether the caller is in the user's contacts
 */
data class CallInfo(
    val phoneNumber: String,
    val isFromContact: Boolean = false
)

