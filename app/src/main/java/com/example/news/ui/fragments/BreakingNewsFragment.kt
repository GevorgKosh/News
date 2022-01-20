package com.example.news.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.news.R
import com.example.news.adapter.ArticleAdapter
import com.example.news.application.NewsApplication
import com.example.news.model.Resource
import com.example.news.repository.NewsRepository
import com.example.news.viewmodel.NewsViewModel
import com.example.news.viewmodel.NewsViewModelFactory
import kotlinx.android.synthetic.main.fragment_breaking_news.*

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {
    //    private val viewModel by viewModels<NewsViewModel>()
    private lateinit var newsRepository: NewsRepository
    private lateinit var viewModelProviderFactory: NewsViewModelFactory
    private lateinit var viewModel: NewsViewModel
    private lateinit var articleAdapter: ArticleAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        iniRepository()
        initAdapter()
        initViewModel()
    }

    private fun iniRepository(){
        context?.let {
            newsRepository = NewsRepository(requireContext())
        }
    }

    private fun initAdapter() {
        articleAdapter = ArticleAdapter()
        rvBreakingNews.apply {
            adapter = articleAdapter
            layoutManager = LinearLayoutManager(context)
        }

        articleAdapter.setOnClickListener { article ->
            val bundle = Bundle().apply {
                putSerializable("article", article)
            }
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle
            )
        }
    }

    private fun initViewModel() {
        viewModelProviderFactory = NewsViewModelFactory(activity?.application as NewsApplication, newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)
        viewModel.breakingNewsLiveData.observe(viewLifecycleOwner) { response ->
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
