package com.ditooard.aplikasistoryapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ditooard.aplikasistoryapp.R
import com.ditooard.aplikasistoryapp.databinding.StoryItemRowBinding
import com.ditooard.aplikasistoryapp.ui.register.DetailStory
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StoryListAdapter(private val stories: List<DetailStory>) :
    RecyclerView.Adapter<StoryListAdapter.StoryViewHolder>() {

    private var clickCallback: OnStoryClickCallback? = null

    fun setOnStoryClickCallback(callback: OnStoryClickCallback) {
        this.clickCallback = callback
    }

    interface OnStoryClickCallback {
        fun onStoryClicked(story: DetailStory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = StoryItemRowBinding.inflate(inflater, parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(stories[position])
        holder.itemView.setOnClickListener {
            clickCallback?.onStoryClicked(stories[holder.adapterPosition])
        }
    }

    class StoryViewHolder(private val binding: StoryItemRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: DetailStory) {
            binding.itemStory = story
            binding.executePendingBindings()
        }
    }

    override fun getItemCount(): Int = stories.size

    companion object {

        @JvmStatic
        @BindingAdapter("loadImage")
        fun loadImage(imageView: ImageView, imageUrl: String) {
            Glide.with(imageView.context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .fallback(R.drawable.ic_launcher_foreground)
                .into(imageView)
        }

        @JvmStatic
        fun convertDateToFormattedString(dateStr: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            return try {
                val date: Date? = inputFormat.parse(dateStr)
                date?.let { outputFormat.format(it) } ?: ""
            } catch (e: ParseException) {
                e.printStackTrace()
                ""
            }
        }
    }
}
