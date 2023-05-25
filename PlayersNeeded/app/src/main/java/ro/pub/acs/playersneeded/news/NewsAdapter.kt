package ro.pub.acs.playersneeded.news

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import ro.pub.acs.playersneeded.R
import java.net.URL


class NewsAdapter(
    private val context: Context,
    private val newsList: Array<News>) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {
    class NewsViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById<ImageView>(R.id.defaultNewsImage)
        val title: TextView = view.findViewById<TextView>(R.id.defaultNewsTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val newsView = LayoutInflater.from(parent.context).inflate(R.layout.news_view, parent,
            false)
        return NewsViewHolder(newsView)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentItem = newsList[position]

        holder.title.text = currentItem.title


        if (currentItem.urlToImage != "image") {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            val url = URL(currentItem.urlToImage)
            val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            holder.image.setImageBitmap(bmp)
        }

        holder.itemView.setOnClickListener {
            val openURL = Intent(android.content.Intent.ACTION_VIEW)
            openURL.data = Uri.parse(currentItem.url)
            startActivity(context, openURL, Bundle())
        }
    }

    override fun getItemCount(): Int {
        return newsList.size
    }
}