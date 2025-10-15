package me.jorgecastillo.hiroaki

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.jorgecastillo.hiroaki.NewsAdapter.ViewHolder
import me.jorgecastillo.hiroaki.model.Article
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.collections.ArrayList

class NewsAdapter(var articles: List<Article> = ArrayList()) :
        RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, pos: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_article, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.bind(articles[pos])
    }

    override fun getItemCount() = articles.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val picture: ImageView = view.findViewById(R.id.picture)
        private val title: TextView = view.findViewById(R.id.title)
        private val publishedAt: TextView = view.findViewById(R.id.publishedAt)
        private val description: TextView = view.findViewById(R.id.description)

        fun bind(article: Article) {
            picture.load(article.urlToImage)
            title.text = article.title

            val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(article.publishedAt)
            publishedAt.text = if (date != null) {
                SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(date)
            } else {
                article.publishedAt
            }
            description.text = article.description
        }
    }
}
