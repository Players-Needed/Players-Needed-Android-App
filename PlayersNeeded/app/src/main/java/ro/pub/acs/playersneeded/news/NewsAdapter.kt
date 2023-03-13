package ro.pub.acs.playersneeded.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ro.pub.acs.playersneeded.R

class NewsAdapter(private val newsList: Array<News>) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {
    class NewsViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.defaultNewsImage)
        val textView = view.findViewById<TextView>(R.id.defaultNewsTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val newsView = LayoutInflater.from(parent.context).inflate(R.layout.news_view, parent,
            false)
        return NewsViewHolder(newsView)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentItem = newsList[position]
    }

    override fun getItemCount(): Int {
        return newsList.size
    }
}