package com.example.thenewsapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.thenewsapp.models.NewsResponse
import com.example.thenewsapp.repository.NewsRepository
import com.example.thenewsapp.util.Resource
import retrofit2.Response

class NewsViewModel(app: Application, var newsRepository: NewsRepository): AndroidViewModel(app) {

    val headlines: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    private var headlinesPage =  1 // To track the headlines page number for headline pagination
    private var headlinesResponse: NewsResponse? = null // To store the last received news response for the headlines

    //To display search results
    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1 //To track the current page number for search results pagination
    var searchNewsResponse: NewsResponse? = null // To store last received news response for search results
    var newsSearchQuery: String? = null
    var oldSearchQuery: String? = null //To store previous search query

    private fun handleHeadlinesResponse(response: Response<NewsResponse>): Resource<NewsResponse>{
        if(response.isSuccessful){ //checks if network request was successful
            response.body()
                ?.let{ resultResponse ->
                    headlinesPage++
                    if (headlinesResponse == null) {
                        headlinesResponse = resultResponse
                    } else{
                        val oldArticles = headlinesResponse?.articles
                        val newArticles = resultResponse.articles
                        oldArticles?.addAll(newArticles)
                    }
                    return Resource.Success(headlinesResponse?:resultResponse)

                }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let{resultResponse ->
                if(searchNewsResponse == null || newsSearchQuery != oldSearchQuery){
                    searchNewsPage = 1
                    oldSearchQuery = newsSearchQuery
                    searchNewsResponse = resultResponse
                } else {
                    searchNewsPage++
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse?: resultResponse)
            }
        }
        return Resource.Error(response.message())

    }


}