package com.example.news.repository

import android.content.Context
import com.example.news.Constants.Companion.API_KEY
import com.example.news.api.NewsClient
import com.example.news.api.NewsService
import com.example.news.db.ArticleDatabase
import com.example.news.model.Article

class NewsRepository(private val context: Context) {
    private val service = NewsClient().getRetrofit().create(NewsService::class.java)
    private val database = ArticleDatabase(context)

    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        service.getBreakingNews(
            countryCode = countryCode,
            page = pageNumber,
            apiKey = API_KEY
        )

    suspend fun searchNews(query: String, pageNumber: Int) =
        service.searchForNews(
            searchQuery = query,
            page = pageNumber,
            apiKey = API_KEY
        )

    suspend fun upsertArticle(article: Article) = database.getArticleDao().upsert(article)

    suspend fun deleteArticle(article: Article) = database.getArticleDao().deleteArticle(article)

    fun getSavedNews() = database.getArticleDao().getAllArticles()
}
