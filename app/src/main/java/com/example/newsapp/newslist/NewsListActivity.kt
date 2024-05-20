package com.example.newsapp.newslist

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.account.AccountActivity
import com.example.newsapp.network.ApiService
import com.example.newsapp.network.NewsListViewModelFactory
import com.example.newsapp.network.NewsRepository
import com.google.android.material.bottomnavigation.BottomNavigationView


class NewsListActivity : AppCompatActivity() {

    private lateinit var newsRecycler: RecyclerView
    private lateinit var viewModel: NewsListViewModel
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.news_movie_list)
        newsRecycler = findViewById(R.id.news_list)
        bottomNavigation = findViewById(R.id.bottom_navigation)
        viewModel = ViewModelProvider(
            this,
            NewsListViewModelFactory(NewsRepository(ApiService.create()))
        )[NewsListViewModel::class.java]
        val adapter = NewsAdapter(this, viewModel)

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.feed -> {
                    true
                }

                R.id.account -> {
                    val intent = Intent(this, AccountActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }

        newsRecycler.adapter = adapter
        newsRecycler.layoutManager = LinearLayoutManager(this)

        viewModel.networkNews.observe(this) { pagingData ->
            adapter.submitData(lifecycle, pagingData)
        }

    }

    override fun onResume() {
        super.onResume()
        bottomNavigation.selectedItemId = R.id.feed
    }
}