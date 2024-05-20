package com.example.newsapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface NewsDao {
    @Query("SELECT * FROM news")
    fun getAllNews(): List<DatabaseNews>

    @Query("SELECT * FROM news WHERE id = :id")
    fun getNewById(id: Int): DatabaseNews?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNew(databaseNews: DatabaseNews)

    @Query("DELETE FROM news WHERE title = :title")
    fun deleteNew(title: String)

    @Query("DELETE FROM news")
    fun deleteAllNews()

    @Query("SELECT COUNT(*) FROM news")
    fun getNewsCount(): Int

    @Update
    fun updateNews(databaseNews: DatabaseNews)
}