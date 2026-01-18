package com.example.bikeridedetection.domain.model

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ResultTest {
    @Test
    fun `Success_isSuccess_returnsTrue`() {
        val result: Result<String> = Result.Success("data")

        assertTrue(result.isSuccess)
        assertFalse(result.isError)
        assertFalse(result.isLoading)
    }

    @Test
    fun `Error_isError_returnsTrue`() {
        val result: Result<String> = Result.Error(RuntimeException("error"))

        assertTrue(result.isError)
        assertFalse(result.isSuccess)
        assertFalse(result.isLoading)
    }

    @Test
    fun `Loading_isLoading_returnsTrue`() {
        val result: Result<String> = Result.Loading

        assertTrue(result.isLoading)
        assertFalse(result.isSuccess)
        assertFalse(result.isError)
    }

    @Test
    fun `Success_getOrNull_returnsData`() {
        val data = "test data"
        val result: Result<String> = Result.Success(data)

        assertEquals(data, result.getOrNull())
    }

    @Test
    fun `Error_getOrNull_returnsNull`() {
        val result: Result<String> = Result.Error(RuntimeException("error"))

        assertNull(result.getOrNull())
    }

    @Test
    fun `Loading_getOrNull_returnsNull`() {
        val result: Result<String> = Result.Loading

        assertNull(result.getOrNull())
    }

    @Test
    fun `Success_getOrDefault_returnsData`() {
        val data = "test data"
        val result: Result<String> = Result.Success(data)

        assertEquals(data, result.getOrDefault("default"))
    }

    @Test
    fun `Error_getOrDefault_returnsDefault`() {
        val result: Result<String> = Result.Error(RuntimeException("error"))

        assertEquals("default", result.getOrDefault("default"))
    }

    @Test
    fun `Loading_getOrDefault_returnsDefault`() {
        val result: Result<String> = Result.Loading

        assertEquals("default", result.getOrDefault("default"))
    }

    @Test
    fun `Success_map_transformsData`() {
        val result: Result<Int> = Result.Success(5)

        val mapped = result.map { it * 2 }

        assertEquals(10, (mapped as Result.Success).data)
    }

    @Test
    fun `Error_map_returnsError`() {
        val error = RuntimeException("error")
        val result: Result<Int> = Result.Error(error)

        val mapped = result.map { it * 2 }

        assertTrue(mapped is Result.Error)
        assertEquals(error, (mapped as Result.Error).error)
    }

    @Test
    fun `Loading_map_returnsLoading`() {
        val result: Result<Int> = Result.Loading

        val mapped = result.map { it * 2 }

        assertTrue(mapped is Result.Loading)
    }

    @Test
    fun `Success_onSuccess_executesAction`() {
        var executed = false
        val result: Result<String> = Result.Success("data")

        result.onSuccess { executed = true }

        assertTrue(executed)
    }

    @Test
    fun `Error_onSuccess_doesNotExecuteAction`() {
        var executed = false
        val result: Result<String> = Result.Error(RuntimeException("error"))

        result.onSuccess { executed = true }

        assertFalse(executed)
    }

    @Test
    fun `Success_onError_doesNotExecuteAction`() {
        var executed = false
        val result: Result<String> = Result.Success("data")

        result.onError { executed = true }

        assertFalse(executed)
    }

    @Test
    fun `Error_onError_executesAction`() {
        var executed = false
        val result: Result<String> = Result.Error(RuntimeException("error"))

        result.onError { executed = true }

        assertTrue(executed)
    }

    @Test
    fun `Error_withoutThrowable_hasDefaultMessage`() {
        val result = Result.Error()

        assertEquals("An unknown error occurred", result.message)
    }

    @Test
    fun `Error_withThrowable_usesThrowableMessage`() {
        val result = Result.Error(RuntimeException("Custom error message"))

        assertEquals("Custom error message", result.message)
    }

    @Test
    fun `Error_withCustomMessage_usesCustomMessage`() {
        val result = Result.Error(message = "Custom message")

        assertEquals("Custom message", result.message)
    }

    @Test
    fun `runCatching_success_returnsSuccess`() =
        runTest {
            val result = com.example.bikeridedetection.domain.model.runCatching { "success" }

            assertTrue(result is Result.Success<String>)
            assertEquals("success", (result as Result.Success<String>).data)
        }

    @Test
    fun `runCatching_exception_returnsError`() =
        runTest {
            val exception = RuntimeException("test error")
            val result: Result<String> = com.example.bikeridedetection.domain.model.runCatching { throw exception }

            assertTrue(result is Result.Error)
            assertEquals(exception, (result as Result.Error).error)
        }
}

