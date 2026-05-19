package com.sharfu.shaalevikasa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.button.MaterialButton

class GalleryAdapter(
    private val needs: List<SchoolNeed>,
    private val onUploadClick: (SchoolNeed, Boolean) -> Unit // Boolean: true for before, false for after
) : RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    class GalleryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvProjectTitle)
        val ivBefore: ImageView = view.findViewById(R.id.ivBefore)
        val ivAfter: ImageView = view.findViewById(R.id.ivAfter)
        val btnUploadBefore: MaterialButton = view.findViewById(R.id.btnUploadBefore)
        val btnUploadAfter: MaterialButton = view.findViewById(R.id.btnUploadAfter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_gallery_comparison, parent, false)
        return GalleryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val need = needs[position]
        holder.tvTitle.text = need.title
        
        // Load images using Coil
        if (need.beforeImageUrl != null) {
            holder.ivBefore.load(need.beforeImageUrl) {
                crossfade(true)
                placeholder(android.R.drawable.ic_menu_gallery)
                error(android.R.drawable.stat_notify_error)
            }
        } else {
            holder.ivBefore.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        if (need.afterImageUrl != null) {
            holder.ivAfter.load(need.afterImageUrl) {
                crossfade(true)
                placeholder(android.R.drawable.ic_menu_gallery)
                error(android.R.drawable.stat_notify_error)
            }
        } else {
            holder.ivAfter.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        // Re-enable upload buttons visibility and set click listeners for both buttons and images
        holder.btnUploadBefore.visibility = View.VISIBLE
        holder.btnUploadAfter.visibility = View.VISIBLE

        val uploadBefore = View.OnClickListener { onUploadClick(need, true) }
        val uploadAfter = View.OnClickListener { onUploadClick(need, false) }

        holder.btnUploadBefore.setOnClickListener(uploadBefore)
        holder.ivBefore.setOnClickListener(uploadBefore)

        holder.btnUploadAfter.setOnClickListener(uploadAfter)
        holder.ivAfter.setOnClickListener(uploadAfter)
    }

    override fun getItemCount() = needs.size
}
