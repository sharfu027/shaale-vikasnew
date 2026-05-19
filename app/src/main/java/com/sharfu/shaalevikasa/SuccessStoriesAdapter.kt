package com.sharfu.shaalevikasa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load

class SuccessStoriesAdapter(private val stories: List<SchoolNeed>) :
    RecyclerView.Adapter<SuccessStoriesAdapter.SuccessViewHolder>() {

    class SuccessViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivImage: ImageView = view.findViewById(R.id.ivSuccessImage)
        val tvTitle: TextView = view.findViewById(R.id.tvProjectTitle)
        val tvTestimonial: TextView = view.findViewById(R.id.tvTestimonial)
        val tvAppreciation: TextView = view.findViewById(R.id.tvAppreciationNote)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuccessViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_success_story, parent, false)
        return SuccessViewHolder(view)
    }

    override fun onBindViewHolder(holder: SuccessViewHolder, position: Int) {
        val story = stories[position]
        holder.tvTitle.text = story.title
        holder.tvTestimonial.text = story.testimonial ?: "No testimonial available."
        holder.tvAppreciation.text = story.appreciationNote ?: "Special thanks to all donors."
        
        holder.ivImage.load(story.imageUrl ?: story.afterImageUrl) {
            crossfade(true)
            placeholder(android.R.drawable.ic_menu_gallery)
        }
    }

    override fun getItemCount(): Int = stories.size
}
