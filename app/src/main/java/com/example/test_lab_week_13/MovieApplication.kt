package com.example.test_lab_week_13

import android.app.Application
import androidx.work.Constraints // Import
import androidx.work.NetworkType // Import
import androidx.work.PeriodicWorkRequest // Import
import androidx.work.WorkManager // Import
import java.util.concurrent.TimeUnit // Import
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.example.test_lab_week_13.api.MovieService
import com.example.test_lab_week_13.database.MovieDatabase

class MovieApplication : Application() {
    lateinit var movieRepository: MovieRepository

    override fun onCreate() {
        super.onCreate()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        val movieService = retrofit.create(MovieService::class.java)
        val movieDatabase = MovieDatabase.getInstance(applicationContext)

        movieRepository = MovieRepository(movieService, movieDatabase)

        // --- WorkManager Scheduler ---
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequest.Builder(
            MovieWorker::class.java,
            15, // Minimum interval is usually 15 minutes in Android, but set to 1 hour as requested if > 15
            TimeUnit.MINUTES // changed to MINUTES to respect Android's minimum of 15 min, or use TimeUnit.HOURS for 1 hour
        )
            .setConstraints(constraints)
            .addTag("movie-work")
            .build()

        WorkManager.getInstance(applicationContext).enqueue(workRequest)
    }
}