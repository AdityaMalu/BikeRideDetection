package com.example.bikeridedetection.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.bikeridedetection.domain.usecase.DeleteOldViewedCallsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * WorkManager worker that periodically cleans up old viewed call history entries.
 * Entries are deleted 48 hours after being marked as viewed.
 */
@HiltWorker
class CallHistoryCleanupWorker
    @AssistedInject
    constructor(
        @Assisted context: Context,
        @Assisted workerParams: WorkerParameters,
        private val deleteOldViewedCallsUseCase: DeleteOldViewedCallsUseCase,
    ) : CoroutineWorker(context, workerParams) {
        override suspend fun doWork(): Result =
            try {
                val deletedCount = deleteOldViewedCallsUseCase()
                Timber.d("Call history cleanup completed. Deleted $deletedCount entries.")
                Result.success()
            } catch (e: Exception) {
                Timber.e(e, "Call history cleanup failed")
                Result.retry()
            }

        companion object {
            const val WORK_NAME = "call_history_cleanup"
            private const val REPEAT_INTERVAL_HOURS = 24L

            /**
             * Schedules the periodic cleanup worker.
             * Runs once every 24 hours.
             *
             * @param context The application context
             */
            fun schedule(context: Context) {
                val workRequest =
                    PeriodicWorkRequestBuilder<CallHistoryCleanupWorker>(
                        REPEAT_INTERVAL_HOURS,
                        TimeUnit.HOURS,
                    ).build()

                WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    workRequest,
                )

                Timber.d("Call history cleanup worker scheduled")
            }

            /**
             * Cancels the periodic cleanup worker.
             *
             * @param context The application context
             */
            fun cancel(context: Context) {
                WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
                Timber.d("Call history cleanup worker cancelled")
            }
        }
    }
