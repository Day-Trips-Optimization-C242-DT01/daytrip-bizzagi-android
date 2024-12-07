package com.bizzagi.daytrip.ui.Homepage

import android.os.Bundle
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

        // Ambil data dari Intent
        val articleTitle = intent.getStringExtra("article_title")
        val articleDescription = intent.getStringExtra("article_description")
        val articleImage = intent.getIntExtra("article_image", -1)  // Menggunakan nilai default jika tidak ditemukan
        val articleContent = intent.getStringExtra("article_content")

        // Periksa apakah data tidak null
        if (articleTitle != null && articleDescription != null && articleImage != -1  && articleContent != null) {
            val imgArticlePhoto: ImageView = findViewById(R.id.img_article_photo)
            val tvArticleName: TextView = findViewById(R.id.tv_article_name)
            val tvArticleDescription: TextView = findViewById(R.id.tv_article_description)
            val tvArticleContent: TextView = findViewById(R.id.tv_article_content)

            tvArticleName.text = articleTitle
            tvArticleDescription.text = articleDescription
            tvArticleContent.text = articleContent
            imgArticlePhoto.setImageResource(articleImage)
        } else {
            Log.e("DetailActivity", "Data artikel tidak valid")
            Toast.makeText(this, "Data artikel tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()  // Menutup activity jika data tidak valid
        }
    }
}