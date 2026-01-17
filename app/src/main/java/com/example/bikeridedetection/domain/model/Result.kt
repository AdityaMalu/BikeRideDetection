package com.example.bikeridedetection.domain.model

/**
 * A sealed class representing the result of an operation.
 * Used for proper error handling throughout the app.
 *
 * @param T The type of data on success
 */
sealed class Result<out T> {
    /**
     * Represents a successful operation with data.
     *
     * @param data The result data
     */
    data class Success<out T>(val data: T) : Result<T>()

    /**
     * Represents a failed operation with an error.
     *
     * @param error The error that occurred
     * @param message A user-friendly error message
     */
    data class Error(
        val error: Throwable? = null,
        val message: String = error?.message ?: "An unknown error occurred"
    ) : Result<Nothing>()

    /**
     * Represents a loading state.
     */
    data object Loading : Result<Nothing>()

    /**
     * Returns true if this result is a success.
     */
    val isSuccess: Boolean get() = this is Success

    /**
     * Returns true if this result is an error.
     */
    val isError: Boolean get() = this is Error

    /**
     * Returns true if this result is loading.
     */
    val isLoading: Boolean get() = this is Loading

    /**
     * Returns the data if this is a success, null otherwise.
     */
    fun getOrNull(): T? = (this as? Success)?.data

    /**
     * Returns the data if this is a success, or the default value otherwise.
     */
    fun getOrDefault(default: @UnsafeVariance T): T = getOrNull() ?: default

    /**
     * Maps the success data to a new type.
     */
    inline fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
        is Loading -> this
    }

    /**
     * Executes the given block if this is a success.
     */
    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }

    /**
     * Executes the given block if this is an error.
     */
    inline fun onError(action: (Error) -> Unit): Result<T> {
        if (this is Error) action(this)
        return this
    }
}

/**
 * Wraps a suspending block in a Result, catching any exceptions.
 */
suspend inline fun <T> runCatching(block: suspend () -> T): Result<T> {
    return try {
        Result.Success(block())
    } catch (e: Exception) {
        Result.Error(e)
    }
}

