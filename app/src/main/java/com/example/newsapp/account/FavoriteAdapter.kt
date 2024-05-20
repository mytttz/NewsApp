package com.example.newsapp.account

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.database.DatabaseNews
import com.example.newsapp.newslist.AccountViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone


class FavoriteAdapter(
    private val context: Context,
    private val viewModel: AccountViewModel
) :
    ListAdapter<DatabaseNews, FavoriteAdapter.NewsViewHolder>(NewsDiffCallback()) {

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val newsTitle: TextView = itemView.findViewById(R.id.news_title)
        val newsDate: TextView = itemView.findViewById(R.id.news_date)
        val newsDescription: TextView = itemView.findViewById(R.id.news_description)
        private val favoriteButton: CheckBox = itemView.findViewById(R.id.favorite_button)

        init {
            favoriteButton.isChecked = true
            favoriteButton.setOnCheckedChangeListener { checkBox, isChecked ->
                if (!isChecked) {
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


    class NewsDiffCallback : DiffUtil.ItemCallback<DatabaseNews>() {
        override fun areItemsTheSame(
            oldItem: DatabaseNews,
            newItem: DatabaseNews
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: DatabaseNews,
            newItem: DatabaseNews
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