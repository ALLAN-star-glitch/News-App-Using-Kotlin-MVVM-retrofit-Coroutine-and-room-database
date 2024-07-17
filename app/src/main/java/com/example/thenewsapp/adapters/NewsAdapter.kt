package com.example.thenewsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.thenewsapp.R
import com.example.thenewsapp.models.Article

class NewsAdapter: RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private lateinit var articleImage: ImageView
    private lateinit var articleSource: TextView
    private lateinit var articleTitle: TextView
    private lateinit var articleDescription: TextView
    private lateinit var articleDateTime: TextView

    //differCallback is used to avoid refreshing the whole list of the recyclerView on an update
    //The ItemCallBack interface is used to determine the difference between two lists
    private val differCallback = object : DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url // checks if an item have been removed or added in the recyclerView
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem //To check if the content of the two items are the same in the article data class
        }
    }
    //Creating an instance of AsyncListDiffer using the differCallback which we created
    //AsyncListDiffer determines differences between two lists on a background thread which helps in smooth UI update.
    private val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    //To set up a callback for item click events which we will implement
    private var onItemClickListener: ((Article)->Unit)? = null //a variable of a nullable function type

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]

        articleImage = holder.itemView.findViewById(R.id.articleImage)
        articleSource = holder.itemView.findViewById(R.id.articleSource)
        articleTitle = holder.itemView.findViewById(R.id.articleTitle)
        articleDescription = holder.itemView.findViewById(R.id.articleDescription)
        articleDateTime = holder.itemView.findViewById(R.id.articleDateTime)

        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).into(articleImage)
            articleSource.text = article.source?.name
            articleTitle.text = article.title
            articleDescription.text = article.description
            articleDateTime.text = article.publishedAt

            //any item that is clicked, it opens up the article
            setOnClickListener{
                onItemClickListener?.let{
                    it(article)
                }
            }
        }
    }

    //This function allows external classes to to set their own click handling Logic for items in the adapter
   fun setOnItemClickListener(listener: (Article)->Unit) {
       onItemClickListener = listener
   }
}