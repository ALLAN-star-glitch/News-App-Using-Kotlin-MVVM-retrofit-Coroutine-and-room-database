package com.example.thenewsapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.thenewsapp.models.Article
import com.example.thenewsapp.models.NewsResponse
import com.example.thenewsapp.repository.NewsRepository
import com.example.thenewsapp.util.Resource
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response

class NewsViewModel(app: Application, private var newsRepository: NewsRepository): AndroidViewModel(app) {

    private val headlines: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    private var headlinesPage =  1 // To track the headlines page number for headline pagination
    private var headlinesResponse: NewsResponse? = null // To store the last received news response for the headlines

    //To display search results
    private val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    private var searchNewsPage = 1 //To track the current page number for search results pagination
    private var searchNewsResponse: NewsResponse? = null // To store last received news response for search results
    private var newsSearchQuery: String? = null //search query
    private var oldSearchQuery: String? = null //To store previous search query

    init {
        getHeadlines("us")
    }

    private fun getHeadlines(countryCode: String) = viewModelScope.launch {
        headlinesInternet(countryCode)
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        searchNewsInternet(searchQuery)
    }
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

    fun addToFavourites(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getFavouriteNews() = viewModelScope.launch{
        newsRepository.getFavoriteNews()
    }

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticles(article)
    }

    private fun internetConnection(context: Context): Boolean{
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
            return getNetworkCapabilities(activeNetwork)?.run{
                when{
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } ?: false
        }
    }

    private suspend fun headlinesInternet(countryCode: String){
        headlines.postValue(Resource.Loading())
        try{
            if(internetConnection(this.getApplication())){
                val response = newsRepository.getHeadlines(countryCode, headlinesPage)
                headlines.postValue(response?.let { handleHeadlinesResponse(it) })
            }else{
                headlines.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable){
            when(t){
                is IOException -> headlines.postValue(Resource.Error("Unable to connect"))
                else -> headlines.postValue(Resource.Error("No Signal"))
            }
        }
    }

    private suspend fun searchNewsInternet(searchQuery: String){
        newsSearchQuery = searchQuery
        searchNews.postValue(Resource.Loading())
        try{
            if(internetConnection(this.getApplication())){
                val response = newsRepository.searchNews(searchQuery, searchNewsPage)
                searchNews.postValue(Resource.Error("No Internet Connection!"))
            }
        }catch(e: Throwable){
            when(e){
               is IOException -> searchNews.postValue(Resource.Error("Unable to connect"))
                else -> searchNews.postValue(Resource.Error("No Signal"))
            }

        }
    }

}