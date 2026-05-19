package com.sharfu.shaalevikasa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TimelineAdapter(private val events: List<TimelineEvent>) :
    RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder>() {

    class TimelineViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvTimelineDate)
        val tvTitle: TextView = view.findViewById(R.id.tvTimelineTitle)
        val tvDescription: TextView = view.findViewById(R.id.tvTimelineDescription)
        val viewLine: View = view.findViewById(R.id.viewLine)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_timeline, parent, false)
        return TimelineViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        val event = events[position]
        holder.tvDate.text = event.date
        holder.tvTitle.text = event.title
        holder.tvDescription.text = event.description
        
        // Hide line for the last item
        holder.viewLine.visibility = if (position == events.size - 1) View.GONE else View.VISIBLE
    }

    override fun getItemCount() = events.size
}
