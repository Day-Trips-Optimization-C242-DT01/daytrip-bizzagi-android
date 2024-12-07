package com.bizzagi.daytrip.ui.Homepage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bizzagi.daytrip.databinding.ItemArticleBinding

class ArticleAdapter(private val articles: List<Article>) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    private var onItemClickListener: ((Article) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]
        holder.bind(article)
    }

    override fun getItemCount(): Int = articles.size

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

    inner class ArticleViewHolder(private val binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(article: Article) {

            binding.tvTitle.text = binding.root.context.getString(article.titlearticleResId)
            binding.tvContent.text = binding.root.context.getString(article.descriptionarticleResId)
            binding.ivArticleImage.setImageResource(article.imagearticleResId)

            itemView.setOnClickListener {
                onItemClickListener?.invoke(article)
            }
        }
    }
}