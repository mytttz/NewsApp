package com.example.newsapp.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "news",
    indices = [Index(value = ["title"], unique = true)]
)
data class DatabaseNews(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String?,
    val title: String,
    val description: String?,
    val publishedAt: String?,
)