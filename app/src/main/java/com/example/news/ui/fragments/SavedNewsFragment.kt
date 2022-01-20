package com.example.news.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.news.R
import com.example.news.adapter.ArticleAdapter
import com.example.news.application.NewsApplication
import com.example.news.db.ArticleDatabase
import com.example.news.model.Resource
import com.example.news.repository.NewsRepository
import com.example.news.viewmodel.NewsViewModel
import com.example.news.viewmodel.NewsViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.android.synthetic.main.fragment_saved_news.*

class SavedNewsFragment : Fragment(R.layout.fragment_saved_news) {
//    private val viewModel by viewModels<NewsViewModel>{ }
    private lateinit var newsRepository: NewsRepository
    private lateinit var viewModelProviderFactory: NewsViewModelFactory
    private lateinit var viewModel: NewsViewModel
    private lateinit var articleAdapter: ArticleAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        iniRepository()
        initAdapter()
        initViewModel()

        initTouchCallback()
    }

    private fun initTouchCallback(){
        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
        ){
            override fun onMove(recyclerView: RecyclerView,
                                viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = articleAdapter.differ.currentList[position]
                viewModel.deleteArticle(article)
                Snackbar.make( viewHolder.itemView, "Successfully added", Snackbar.LENGTH_SHORT).apply {
                    setAction("Undo"){
                        viewModel.upsertArticle(article)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelper).apply {
            attachToRecyclerView(rvSavedNews)
        }
    }

    private fun iniRepository(){
        context?.let {
            newsRepository = NewsRepository(requireContext())
        }
    }

    private fun initAdapter(){
        articleAdapter = ArticleAdapter()
        rvSavedNews.apply {
            adapter = articleAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun initViewModel(){
        viewModelProviderFactory = NewsViewModelFactory(activity?.application as NewsApplication, newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)
        viewModel.getSavedNews().observe(viewLifecycleOwner){ response ->
            response?.let {
                articleAdapter.differ.submitList(response)
            }
        }
    }
}
