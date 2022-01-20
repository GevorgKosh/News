package com.example.news.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.news.R
import com.example.news.application.NewsApplication
import com.example.news.repository.NewsRepository
import com.example.news.viewmodel.NewsViewModel
import com.example.news.viewmodel.NewsViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_article.*
import kotlinx.android.synthetic.main.fragment_breaking_news.*


class ArticleFragment : Fragment(R.layout.fragment_article) {
    private val args: ArticleFragmentArgs by navArgs()
    private lateinit var viewModelProviderFactory: NewsViewModelFactory
    private lateinit var viewModel: NewsViewModel
    private lateinit var newsRepository: NewsRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRepository()
        initViewModel()
        initWebView()
    }

    private fun initRepository(){
        context?.let {
            newsRepository = NewsRepository(requireContext())
        }
    }

    private fun initViewModel(){
        viewModelProviderFactory = NewsViewModelFactory(activity?.application as NewsApplication, newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)
    }

    private fun initWebView() {
        val article = args.article
        webView.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url)
        }

        fab.setOnClickListener {
            viewModel.upsertArticle(article)
            Snackbar.make(it, "Successfully added", Snackbar.LENGTH_SHORT).show()
        }
    }
}
