package com.example.bikeridedetection.domain.usecase

import com.example.bikeridedetection.domain.model.BikeMode
import com.example.bikeridedetection.domain.repository.BikeModeRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UpdateAutoReplyUseCaseTest {
    private lateinit var repository: BikeModeRepository
    private lateinit var useCase: UpdateAutoReplyUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = UpdateAutoReplyUseCase(repository)
    }

    @Test
    fun `invoke_validMessage_savesMessageAndReturnsTrue`() =
        runTest {
            val message = "Custom auto-reply message"
            coEvery { repository.setAutoReplyMessage(message) } returns Unit

            val result = useCase(message)

            assertTrue(result)
            coVerify { repository.setAutoReplyMessage(message) }
        }

    @Test
    fun `invoke_emptyMessage_savesDefaultAndReturnsFalse`() =
        runTest {
            coEvery { repository.setAutoReplyMessage(BikeMode.DEFAULT_AUTO_REPLY) } returns Unit

            val result = useCase("")

            assertFalse(result)
            coVerify { repository.setAutoReplyMessage(BikeMode.DEFAULT_AUTO_REPLY) }
        }

    @Test
    fun `invoke_whitespaceOnlyMessage_savesDefaultAndReturnsFalse`() =
        runTest {
            coEvery { repository.setAutoReplyMessage(BikeMode.DEFAULT_AUTO_REPLY) } returns Unit

            val result = useCase("   ")

            assertFalse(result)
            coVerify { repository.setAutoReplyMessage(BikeMode.DEFAULT_AUTO_REPLY) }
        }

    @Test
    fun `invoke_messageWithLeadingTrailingSpaces_trimsAndSaves`() =
        runTest {
            val message = "  Custom message  "
            val trimmedMessage = "Custom message"
            coEvery { repository.setAutoReplyMessage(trimmedMessage) } returns Unit

            val result = useCase(message)

            assertTrue(result)
            coVerify { repository.setAutoReplyMessage(trimmedMessage) }
        }

    @Test
    fun `invoke_tabsAndNewlines_treatedAsWhitespace`() =
        runTest {
            coEvery { repository.setAutoReplyMessage(BikeMode.DEFAULT_AUTO_REPLY) } returns Unit

            val result = useCase("\t\n  \t")

            assertFalse(result)
            coVerify { repository.setAutoReplyMessage(BikeMode.DEFAULT_AUTO_REPLY) }
        }

    @Test
    fun `invoke_singleCharacterMessage_savesAndReturnsTrue`() =
        runTest {
            val message = "X"
            coEvery { repository.setAutoReplyMessage(message) } returns Unit

            val result = useCase(message)

            assertTrue(result)
            coVerify { repository.setAutoReplyMessage(message) }
        }

    @Test
    fun `invoke_longMessage_savesAndReturnsTrue`() =
        runTest {
            val message =
                "This is a very long auto-reply message that explains in detail " +
                    "why I cannot answer the phone right now because I am currently riding my bicycle."
            coEvery { repository.setAutoReplyMessage(message) } returns Unit

            val result = useCase(message)

            assertTrue(result)
            coVerify { repository.setAutoReplyMessage(message) }
        }
}
