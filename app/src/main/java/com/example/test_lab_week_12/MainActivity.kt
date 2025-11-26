package com.example.test_lab_week_12

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.test_lab_week_12.model.Movie
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar

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

        // Observe movie list
        movieViewModel.popularMovies.observe(this) { movieList ->
            val year = Calendar.getInstance().get(Calendar.YEAR).toString()

            movieAdapter.addMovies(
                movieList
                    //.filter { it.releaseDate?.startsWith(year) == true }
                    .sortedByDescending { it.popularity }
            )
        }

        // Observe error
        movieViewModel.error.observe(this) { errorMsg ->
            if (!errorMsg.isNullOrEmpty()) {
                Snackbar.make(recyclerView, errorMsg, Snackbar.LENGTH_LONG).show()
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
