package com.example.newsapp.network

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NewsPagingSource<T : Any>(
    private val apiService: ApiService,
) : PagingSource<Int, T>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return withContext(Dispatchers.IO) {
            try {
                val nextPageNumber = params.key ?: 1
                val response = apiService.getNews(nextPageNumber)
                Log.i("response", response.body().toString())


                return@withContext if (response.isSuccessful) {
                    val items = response.body()?.let { body ->
                        when (body) {
                            is NewsResponse -> body.articles
                            else -> emptyList()
                        }
                    } ?: emptyList()

                    LoadResult.Page(
                        data = items as List<T>,
                        prevKey = if (nextPageNumber == 1) null else nextPageNumber - 1,
                        nextKey = if (items.isEmpty()) null else nextPageNumber + 1
                    )
                } else {
                    LoadResult.Error(Exception("Error fetching data"))
                }
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition
    }
}
