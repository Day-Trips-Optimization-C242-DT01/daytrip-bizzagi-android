package com.bizzagi.daytrip.ui.Homepage

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bizzagi.daytrip.R

class DetailArticleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_article)

        val articleTitle = intent.getStringExtra("article_title")
        val articleImage = intent.getIntExtra("article_image", -1)
        val articleContent = intent.getStringExtra("article_content")

        if (articleTitle != null && articleImage != -1  && articleContent != null) {
            val imgArticlePhoto: ImageView = findViewById(R.id.img_article_photo)
            val tvArticleName: TextView = findViewById(R.id.tv_article_name)
            val tvArticleContent: TextView = findViewById(R.id.tv_article_content)

            tvArticleName.text = articleTitle
            imgArticlePhoto.setImageResource(articleImage)
            tvArticleContent.text = Html.fromHtml(articleContent, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Log.e("DetailActivity", "Data artikel tidak valid")
            Toast.makeText(this, "Data artikel tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}