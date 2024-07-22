package com.ditooard.aplikasistoryapp.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.ditooard.aplikasistoryapp.R
import com.ditooard.aplikasistoryapp.adapter.StoryListAdapter
import com.ditooard.aplikasistoryapp.databinding.ActivityDetailBinding
import com.ditooard.aplikasistoryapp.ui.register.DetailStory

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val storyDetails = intent.getParcelableExtra<DetailStory>(STORY) as DetailStory
        displayStoryDetails(storyDetails)

        supportActionBar?.title = getString(R.string.detail_title, storyDetails.name)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun displayStoryDetails(storyDetails: DetailStory) {
        binding.apply {
            UserName.text = storyDetails.name
            Descript.text = storyDetails.description
            Date.text = StoryListAdapter.convertDateToFormattedString(storyDetails.createdAt)
        }
        Glide.with(this)
            .load(storyDetails.photoUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(binding.imageDetailStory)
    }

    companion object {
        const val STORY = "story_extra"
    }
}
