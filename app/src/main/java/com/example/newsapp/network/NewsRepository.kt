package com.example.newsapp.network

import androidx.paging.PagingSource

class NewsRepository(private val apiService: ApiService) {
    fun getMoviesPagingSource(
    ): PagingSource<Int, NetworkNews> {
        return NewsPagingSource(apiService)
    }
}