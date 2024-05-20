package com.example.newsapp.newslist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.database.AppDatabase
import com.example.newsapp.network.NetworkNews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone


class NewsAdapter(
    private val context: Context,
    private val viewModel: NewsListViewModel
) :
    PagingDataAdapter<NetworkNews, NewsAdapter.NewsViewHolder>(NewsDiffCallback()) {

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val newsTitle: TextView = itemView.findViewById(R.id.news_title)
        val newsDate: TextView = itemView.findViewById(R.id.news_date)
        val newsDescription: TextView = itemView.findViewById(R.id.news_description)
        private val favoriteButton: CheckBox = itemView.findViewById(R.id.favorite_button)

        init {
            CoroutineScope(Dispatchers.IO).launch {
                val listNews = AppDatabase.getDatabase(context).newsDao().getAllNews()
                for (news in listNews) {
                    if (news.title == getItem(absoluteAdapterPosition)?.title) {
                        favoriteButton.isChecked = true
                    }
                }
            }
            favoriteButton.setOnCheckedChangeListener { checkBox, isChecked ->
                if (isChecked) {
                    getItem(absoluteAdapterPosition)?.let { viewModel.addFav(context, it) }
                } else {
                    getItem(absoluteAdapterPosition)?.let { viewModel.removeFav(context, it.title) }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.news_list_item, parent, false)
        return NewsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.newsTitle.text = getItem(position)?.title
        holder.newsDescription.text = getItem(position)?.description
        holder.newsDate.text = formatTime(getItem(position)?.publishedAt)
    }


    class NewsDiffCallback : DiffUtil.ItemCallback<NetworkNews>() {
        override fun areItemsTheSame(
            oldItem: NetworkNews,
            newItem: NetworkNews
        ): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(
            oldItem: NetworkNews,
            newItem: NetworkNews
        ): Boolean {
            return oldItem == newItem
        }
    }

    private fun formatTime(iso8601Time: String?): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("GMT")
            val date = inputFormat.parse(iso8601Time)

            val outputFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
            outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            "Invalid date"
        }
    }
}