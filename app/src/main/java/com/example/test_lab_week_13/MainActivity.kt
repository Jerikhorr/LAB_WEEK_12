package com.example.test_lab_week_13

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.test_lab_week_13.databinding.ActivityMainBinding
import com.example.test_lab_week_13.model.Movie

class MainActivity : AppCompatActivity(), MovieAdapter.MovieClickListener {

    private lateinit var movieAdapter: MovieAdapter
    private lateinit var movieViewModel: MovieViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Initialize Binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // Setup adapter
        movieAdapter = MovieAdapter(this)
        // Access RecyclerView via binding to set the adapter
        binding.movieList.adapter = movieAdapter

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

        // 2. Bind ViewModel and Lifecycle Owner
        binding.viewModel = movieViewModel
        binding.lifecycleOwner = this

        // 3. Removed lifecycleScope.launch block as per instructions
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