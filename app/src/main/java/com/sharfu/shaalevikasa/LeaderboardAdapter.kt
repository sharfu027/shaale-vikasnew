package com.sharfu.shaalevikasa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation

class LeaderboardAdapter(private var contributors: List<Contributor>) :
    RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder>() {

    class LeaderboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRank: TextView = view.findViewById(R.id.tvRank)
        val ivAvatar: ImageView = view.findViewById(R.id.ivAvatar)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvBadge: TextView = view.findViewById(R.id.tvBadge)
        val tvPoints: TextView = view.findViewById(R.id.tvPoints)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaderboard, parent, false)
        return LeaderboardViewHolder(view)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        val contributor = contributors[position]
        holder.tvRank.text = contributor.rank.toString()
        holder.tvName.text = contributor.name
        holder.tvBadge.text = contributor.badge
        holder.tvPoints.text = contributor.points
        
        holder.ivAvatar.load(contributor.avatarUrl ?: "https://i.pravatar.cc/150?u=${contributor.name}") {
            crossfade(true)
            transformations(CircleCropTransformation())
        }
    }

    override fun getItemCount(): Int = contributors.size

    fun updateData(newList: List<Contributor>) {
        contributors = newList
        notifyDataSetChanged()
    }
}