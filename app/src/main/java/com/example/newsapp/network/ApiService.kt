package com.example.newsapp.network

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("v2/everything")
    suspend fun getNews(
        @Query("page") page: Int,
        @Query("domains") domains: String = "bbc.com",
        @Query("language") language: String = "ru",
        @Query("apiKey") apiKey: String = APIKEY
    ): Response<NewsResponse>

    companion object {
        private const val APIKEY = "ff5f6c6a08884ef2a131993100ef55b2"
        fun create(): ApiService {
            return Retrofit.Builder()
                .baseUrl("https://newsapi.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}
