package com.example.newsapp.network

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.newsapp.newslist.AccountViewModel
import com.example.newsapp.newslist.NewsListViewModel


class NewsListViewModelFactory(
    private val repository: NewsRepository? = null,
    private val context: Context? = null
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(NewsListViewModel::class.java) -> {
                if (repository != null) {
                    @Suppress("UNCHECKED_CAST")
                    return NewsListViewModel(repository) as T
                } else {
                    throw IllegalArgumentException("NewsRepository is required for NewsListViewModel")
                }
            }

            modelClass.isAssignableFrom(AccountViewModel::class.java) -> {
                if (context != null) {
                    @Suppress("UNCHECKED_CAST")
                    return AccountViewModel(context) as T
                } else {
                    throw IllegalArgumentException("Context is required for AccountViewModel")
                }
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

