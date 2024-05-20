package com.example.newsapp.newslist

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.newsapp.database.AppDatabase
import com.example.newsapp.database.DatabaseNews
import com.example.newsapp.network.NetworkNews
import com.example.newsapp.network.NewsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class NewsListViewModel(
    private val repository: NewsRepository,
) : ViewModel() {

    private val _networkNews = MutableLiveData<PagingData<NetworkNews>>()
    val networkNews: LiveData<PagingData<NetworkNews>> get() = _networkNews

    private val _error = MutableLiveData<String>()

    init {
        fetchNews()
    }

    private fun fetchNews() {
        val pagingSource = repository.getMoviesPagingSource()
        val pager = Pager(PagingConfig(pageSize = 10)) {
            pagingSource
        }
        viewModelScope.launch {
            pager.flow.cachedIn(viewModelScope).collectLatest {
                _networkNews.postValue(it)
            }
        }
    }

    fun addFav(context: Context, networkNews: NetworkNews) {
        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.getDatabase(context).newsDao().insertNew(networkNews.toDatabaseNews())
        }
    }

    fun removeFav(context: Context, id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.getDatabase(context).newsDao().deleteNew(id)
        }
    }

    private fun NetworkNews.toDatabaseNews(): DatabaseNews {
        return DatabaseNews(
            name = this.name,
            title = this.title,
            description = this.description,
            publishedAt = this.publishedAt,
        )
    }

}

