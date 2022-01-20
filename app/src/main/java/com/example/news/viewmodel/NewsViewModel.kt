package com.example.news.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_ETHERNET
import android.net.ConnectivityManager.TYPE_MOBILE
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.news.application.NewsApplication
import com.example.news.model.Article
import com.example.news.model.NewsResponse
import com.example.news.model.Resource
import com.example.news.repository.NewsRepository
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
    private val application: NewsApplication,
    private val repository: NewsRepository
) : AndroidViewModel(application) {
    private val breakingNewsLive = MutableLiveData<Resource<NewsResponse>>()
    val breakingNewsLiveData = breakingNewsLive
    private val breakingNewsPage = 1

    private val searchNewsLive = MutableLiveData<Resource<NewsResponse>>()
    val searchNewsLiveData = searchNewsLive
    private val searchNewsPage = 1

    init {
        getBreakingNews("us")
        searchNews("us")
    }

    fun getBreakingNews(countryCode: String) {
        viewModelScope.launch {
            val news = repository.getBreakingNews(countryCode, breakingNewsPage)
            breakingNewsLive.value = handleBreakingNewsResponse(news)
        }
    }

    fun searchNews(query: String) {
        viewModelScope.launch {
            val news = repository.searchNews(query = query, pageNumber = searchNewsPage)
            searchNewsLiveData.value = handleBreakingNewsResponse(news)
        }
    }

    fun upsertArticle(article: Article) =
        viewModelScope.launch {
            repository.upsertArticle(article)
        }

    fun deleteArticle(article: Article) {
        viewModelScope.launch {
            repository.deleteArticle(article)
        }
    }

    fun getSavedNews() = repository.getSavedNews()

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(it, "Success")
            }
        }
        return Resource.Error(response.body(), response.message())
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}
