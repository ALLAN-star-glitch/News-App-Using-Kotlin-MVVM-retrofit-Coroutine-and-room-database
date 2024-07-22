package com.example.thenewsapp.ui.fragments


import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.thenewsapp.R
import com.example.thenewsapp.databinding.FragmentArticleBinding
import com.example.thenewsapp.ui.NewsActivity
import com.example.thenewsapp.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar


class ArticleFragment : Fragment(R.layout.fragment_article) {
    private lateinit var newsViewModel: NewsViewModel

    //ArticleFragmentArgs is an automatically generated class by the safe-arguments plugin.. it is basically used to pass arguments between fragments in a safe way
    private val args: ArticleFragmentArgs by navArgs() //navArgs() is an extension function provided by the navigation component and it is used to retrieve the argument passed to a fragment
    private lateinit var binding: FragmentArticleBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentArticleBinding.bind(view)

        newsViewModel = (activity as NewsActivity).newsViewModel
        val article = args.article

        binding.webView.apply{
            //webViewClient is responsible for handling various events in the web view such as when a new url is about to be loaded
            webViewClient = WebViewClient()
            loadUrl(article.url)
        }

        binding.fab.setOnClickListener {
            newsViewModel.addToFavourites(article)
            Snackbar.make(view, "Added to favourites", Snackbar.LENGTH_SHORT).show()
        }

    }



}

