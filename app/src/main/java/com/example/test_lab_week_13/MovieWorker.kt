package com.example.test_lab_week_13

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MovieWorker(
    private val context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    // doWork() is called in a background thread
    override fun doWork(): Result {
        // get a reference to the repository
        // Note: We cast applicationContext to ensure we get the App instance
        val movieRepository = (context.applicationContext as MovieApplication).movieRepository

        // launch a coroutine in the IO thread
        CoroutineScope(Dispatchers.IO).launch {
            movieRepository.fetchMoviesFromNetwork()
        }
        return Result.success()
    }
}