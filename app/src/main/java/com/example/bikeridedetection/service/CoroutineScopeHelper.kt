package com.example.bikeridedetection.service

import com.example.bikeridedetection.domain.model.BikeMode
import com.example.bikeridedetection.domain.model.SmsResult
import com.example.bikeridedetection.domain.repository.BikeModeRepository
import com.example.bikeridedetection.domain.usecase.GetBikeModeUseCase
import com.example.bikeridedetection.domain.usecase.SendAutoReplyUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.function.Consumer

/**
 * Helper object to bridge Kotlin coroutines with Java code.
 * Provides utility methods for creating scopes and launching coroutines from Java.
 */
object CoroutineScopeHelper {
    /**
     * Creates a CoroutineScope with the given job and dispatcher.
     *
     * @param job The parent job for the scope
     * @param dispatcher The dispatcher to use
     * @return A new CoroutineScope
     */
    @JvmStatic
    fun createScope(job: Job, dispatcher: CoroutineDispatcher): CoroutineScope {
        return CoroutineScope(job + dispatcher)
    }

    /**
     * Gets the bike mode and calls the callback with the result.
     *
     * @param scope The coroutine scope to use
     * @param useCase The GetBikeModeUseCase instance
     * @param callback The callback to receive the BikeMode
     * @return The launched Job
     */
    @JvmStatic
    fun getBikeModeAsync(
        scope: CoroutineScope,
        useCase: GetBikeModeUseCase,
        callback: Consumer<BikeMode>,
    ): Job {
        return scope.launch {
            val bikeMode = useCase()
            callback.accept(bikeMode)
        }
    }

    /**
     * Sends an auto-reply and calls the callback with the result.
     *
     * @param scope The coroutine scope to use
     * @param useCase The SendAutoReplyUseCase instance
     * @param phoneNumber The phone number to send the reply to
     * @param callback The callback to receive the SmsResult
     * @return The launched Job
     */
    @JvmStatic
    fun sendAutoReplyAsync(
        scope: CoroutineScope,
        useCase: SendAutoReplyUseCase,
        phoneNumber: String,
        callback: Consumer<SmsResult>,
    ): Job {
        return scope.launch {
            val result = useCase(phoneNumber)
            callback.accept(result)
        }
    }

    /**
     * Sets bike mode enabled state and calls the callback when done.
     *
     * @param scope The coroutine scope to use
     * @param repository The BikeModeRepository instance
     * @param enabled Whether bike mode should be enabled
     * @param callback The callback to call when done (optional)
     * @return The launched Job
     */
    @JvmStatic
    @JvmOverloads
    fun setBikeModeEnabledAsync(
        scope: CoroutineScope,
        repository: BikeModeRepository,
        enabled: Boolean,
        callback: Runnable? = null,
    ): Job {
        return scope.launch {
            repository.setBikeModeEnabled(enabled)
            callback?.run()
        }
    }

    /**
     * Screens a call - gets bike mode, and if enabled, rejects the call and sends auto-reply.
     *
     * @param scope The coroutine scope to use
     * @param getBikeModeUseCase The GetBikeModeUseCase instance
     * @param sendAutoReplyUseCase The SendAutoReplyUseCase instance
     * @param phoneNumber The phone number of the caller (nullable)
     * @param onBikeModeResult Callback with bike mode and optional SMS result
     * @return The launched Job
     */
    @JvmStatic
    fun screenCallAsync(
        scope: CoroutineScope,
        getBikeModeUseCase: GetBikeModeUseCase,
        sendAutoReplyUseCase: SendAutoReplyUseCase,
        phoneNumber: String?,
        onBikeModeResult: CallScreeningCallback,
    ): Job {
        return scope.launch {
            val bikeMode = getBikeModeUseCase()
            if (bikeMode.isEnabled && phoneNumber != null) {
                val smsResult = sendAutoReplyUseCase(phoneNumber)
                onBikeModeResult.onResult(bikeMode, smsResult)
            } else {
                onBikeModeResult.onResult(bikeMode, null)
            }
        }
    }

    /**
     * Handles activity transition - sets bike mode and broadcasts the change.
     *
     * @param scope The coroutine scope to use
     * @param repository The BikeModeRepository instance
     * @param enabled Whether bike mode should be enabled
     * @param onComplete Callback when the operation is complete
     * @return The launched Job
     */
    @JvmStatic
    fun handleTransitionAsync(
        scope: CoroutineScope,
        repository: BikeModeRepository,
        enabled: Boolean,
        onComplete: Runnable,
    ): Job {
        return scope.launch {
            repository.setBikeModeEnabled(enabled)
            onComplete.run()
        }
    }
}

/**
 * Callback interface for call screening results.
 */
fun interface CallScreeningCallback {
    fun onResult(bikeMode: BikeMode, smsResult: SmsResult?)
}

