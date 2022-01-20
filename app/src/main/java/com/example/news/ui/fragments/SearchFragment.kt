package com.example.news.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.news.R
import com.example.news.adapter.ArticleAdapter
import com.example.news.application.NewsApplication
import com.example.news.db.ArticleDatabase
import com.example.news.model.Resource
import com.example.news.repository.NewsRepository
import com.example.news.viewmodel.NewsViewModel
import com.example.news.viewmodel.NewsViewModelFactory
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment(R.layout.fragment_search) {
    //    private val viewModel by viewModels<NewsViewModel>()
    private lateinit var newsRepository: NewsRepository
    private lateinit var viewModelProviderFactory: NewsViewModelFactory
    private lateinit var viewModel: NewsViewModel
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var job: Job

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSearch()
        iniRepository()
        initAdapter()
        initViewModel()
    }

    private fun iniRepository(){
        context?.let {
            newsRepository = NewsRepository(requireContext())
        }
    }

    private fun initSearch() {
        etSearch.addTextChangedListener { editable ->
            job = MainScope().launch {
                delay(500L)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        viewModel.searchNews(query = editable.toString())
                    }
                }
            }
        }
    }

    private fun initAdapter() {
        articleAdapter = ArticleAdapter()
        rvSearchNews.apply {
            adapter = articleAdapter
            layoutManager = LinearLayoutManager(context)
        }

        articleAdapter.setOnClickListener { article ->
            val bundle = Bundle().apply {
                putSerializable("article", article)
            }
            findNavController().navigate(
                R.id.action_searchFragment_to_articleFragment,
                bundle
            )
        }
    }

    private fun initViewModel() {
        viewModelProviderFactory = NewsViewModelFactory(activity?.application as NewsApplication, newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)
        viewModel.searchNewsLiveData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressbar()
                    response.data?.let { newsResponse ->
                        articleAdapter.differ.submitList(newsResponse.articles)
                    }
                }
                is Resource.Error -> {
                    hideProgressbar()
                }
                is Resource.Loading -> showProgressbar()
            }
        }
    }

    private fun hideProgressbar() {
        paginationProgressBar.visibility = View.GONE
    }

    private fun showProgressbar() {
        paginationProgressBar.visibility = View.VISIBLE
    }
}
