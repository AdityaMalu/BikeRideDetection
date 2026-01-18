package com.example.bikeridedetection.util

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class for contact-related operations.
 */
@Singleton
class ContactsHelper
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        /**
         * Checks if a phone number exists in the user's contacts.
         *
         * @param phoneNumber The phone number to check
         * @return true if the number is in contacts, false otherwise
         */
        suspend fun isPhoneNumberInContacts(phoneNumber: String): Boolean =
            withContext(Dispatchers.IO) {
                if (phoneNumber.isBlank()) {
                    return@withContext false
                }

                try {
                    val uri =
                        Uri.withAppendedPath(
                            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                            Uri.encode(phoneNumber),
                        )

                    val projection = arrayOf(ContactsContract.PhoneLookup._ID)

                    context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                        val isInContacts = cursor.count > 0
                        Timber.d("Phone number $phoneNumber is in contacts: $isInContacts")
                        return@withContext isInContacts
                    }

                    false
                } catch (e: Exception) {
                    Timber.e(e, "Error checking if phone number is in contacts")
                    false
                }
            }
    }
