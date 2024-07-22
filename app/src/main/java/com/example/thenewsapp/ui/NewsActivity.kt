package com.example.thenewsapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.thenewsapp.R
import com.example.thenewsapp.databinding.ActivityNewsBinding
import com.example.thenewsapp.db.ArticleDatabase
import com.example.thenewsapp.repository.NewsRepository

class NewsActivity : AppCompatActivity() {

    lateinit var newsViewModel: NewsViewModel
    private lateinit var binding: ActivityNewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Creating an instance of custom newsRepository
        //NewsRepository requires an ArticleDatabase as dependency
        val newsRepository = NewsRepository(ArticleDatabase(this))

        //creating an instance of custom viewModelProviderFactory
        val viewModelProviderFactory = NewsViewModelProviderFactory(application, newsRepository)

        //We are using ViewModelProvider to create an instance of newsViewModel
        newsViewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)

        //Setting up a nav controller for bottom navigation view that we created
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }
}

/*NOTE:
    1. This setup ensures that the newsViewModel is created using the appropriate dependencies such as
    news repository and the article database
    2. The use of NewsViewModelProviderFactory allows you to provide the custom logic for creating a
     viewModel instance which can be beneficial at the time of testing */