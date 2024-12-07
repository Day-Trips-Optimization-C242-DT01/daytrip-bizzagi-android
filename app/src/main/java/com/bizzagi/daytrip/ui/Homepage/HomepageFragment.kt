package com.bizzagi.daytrip.ui.Homepage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bizzagi.daytrip.R
import com.bizzagi.daytrip.databinding.FragmentHomepageBinding
import com.bizzagi.daytrip.ui.Maps.AddPlan.PickRegionActivity


class HomepageFragment : Fragment() {

    private var _binding: FragmentHomepageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomepageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val articles = listOf(
            Article(R.string.articles_title_1, R.string.article_description_1, R.drawable.image_article_1, R.string.article_content_1),
            Article(R.string.articles_title_2, R.string.article_description_2, R.drawable.image_article_2, R.string.article_content_2),
            Article(R.string.articles_title_3, R.string.article_description_3, R.drawable.image_article_3, R.string.article_content_3)
        )

        Log.d("HomepageFragment", "Articles: $articles")

        val adapter = ArticleAdapter(articles)

        binding.rvTripPlan.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTripPlan.adapter = adapter

        adapter.setOnItemClickListener { article ->
            val intent = Intent(requireContext(), DetailArticleActivity::class.java).apply {
                putExtra("article_title", getString(article.titlearticleResId))
                putExtra("article_description", getString(article.descriptionarticleResId))
                putExtra("article_image", article.imagearticleResId)
                putExtra("article_content", getString(article.contentarticleResId))
            }
            startActivity(intent)
        }

        binding.addTripFab.setOnClickListener {
            val intent = Intent(requireContext(), PickRegionActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}