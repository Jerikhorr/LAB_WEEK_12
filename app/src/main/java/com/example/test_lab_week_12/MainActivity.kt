package com.example.test_lab_week_13

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.example.test_lab_week_13.model.Movie
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), MovieAdapter.MovieClickListener {

    private lateinit var movieAdapter: MovieAdapter
    private lateinit var movieViewModel: MovieViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup adapter
        movieAdapter = MovieAdapter(this)

        val recyclerView: RecyclerView = findViewById(R.id.movie_list)
        recyclerView.adapter = movieAdapter

        // Ambil repository dari MovieApplication
        val movieRepository = (application as MovieApplication).movieRepository

        // Setup ViewModel
        movieViewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MovieViewModel(movieRepository) as T
                }
            }
        )[MovieViewModel::class.java]

        // fetch movies from the API
        // lifecycleScope is a lifecycle-aware coroutine scope
        lifecycleScope.launch {
            // repeatOnLifecycle is a lifecycle-aware coroutine builder
            // Lifecycle.State.STARTED means that the coroutine will run
            // when the activity is started
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    // collect the list of movies from the StateFlow
                    movieViewModel.popularMovies.collect { movies ->
                        // add the list of movies to the adapter
                        movieAdapter.addMovies(movies)
                    }
                }
                launch {
                    // collect the error message from the StateFlow
                    movieViewModel.error.collect { error ->
                        // if an error occurs, show a Snackbar with the error message
                        if (error.isNotEmpty()) {
                            Snackbar.make(
                                recyclerView, error, Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

    // Ketika item movie di-klik
    override fun onMovieClick(movie: Movie) {
        val intent = Intent(this, DetailsActivity::class.java).apply {
            putExtra(DetailsActivity.EXTRA_TITLE, movie.title)
            putExtra(DetailsActivity.EXTRA_RELEASE, movie.releaseDate)
            putExtra(DetailsActivity.EXTRA_OVERVIEW, movie.overview)
            putExtra(DetailsActivity.EXTRA_POSTER, movie.posterPath)
        }
        startActivity(intent)
    }
}