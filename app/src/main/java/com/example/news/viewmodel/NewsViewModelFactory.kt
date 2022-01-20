package com.example.news.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.news.application.NewsApplication
import com.example.news.repository.NewsRepository

class NewsViewModelFactory(
    private val application: NewsApplication,
    private val repository: NewsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewsViewModel(application, repository) as T
    }
}
