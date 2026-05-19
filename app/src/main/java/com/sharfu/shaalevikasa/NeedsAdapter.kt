package com.sharfu.shaalevikasa

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NeedsAdapter(
    private var needs: List<SchoolNeed>,
    private val role: Role,
    private val onAction: (SchoolNeed) -> Unit
) : RecyclerView.Adapter<NeedsAdapter.NeedViewHolder>() {

    private var filteredNeeds: List<SchoolNeed> = needs

    fun updateData(newNeeds: List<SchoolNeed>) {
        needs = newNeeds
        filteredNeeds = newNeeds
        notifyDataSetChanged()
    }

    fun filter(query: String, filterType: String) {
        filteredNeeds = needs.filter { need ->
            val matchesQuery = need.title.contains(query, ignoreCase = true) || 
                               need.description.contains(query, ignoreCase = true)
            
            val matchesFilter = when(filterType) {
                "High Priority" -> need.priority == Priority.HIGH
                "Pending" -> need.status == Status.PENDING
                "Completed" -> need.status == Status.COMPLETED
                else -> true
            }
            
            matchesQuery && matchesFilter
        }
        notifyDataSetChanged()
    }

    class NeedViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivImage: ImageView = view.findViewById(R.id.ivNeedImage)
        val tvPriority: TextView = view.findViewById(R.id.tvPriority)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvCost: TextView = view.findViewById(R.id.tvCost)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val pbProgress: ProgressBar = view.findViewById(R.id.pbProgress)
        val tvProgressLabel: TextView = view.findViewById(R.id.tvProgressLabel)
        val tvPledgeCount: TextView = view.findViewById(R.id.tvPledgeCount)
        val tvRecentDonor: TextView = view.findViewById(R.id.tvRecentDonor)
        val btnPledge: Button = view.findViewById(R.id.btnPledge)
        val btnAdminAction: Button = view.findViewById(R.id.btnAdminAction)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NeedViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_school_need, parent, false)
        return NeedViewHolder(view)
    }

    override fun onBindViewHolder(holder: NeedViewHolder, position: Int) {
        val need = filteredNeeds[position]
        holder.tvTitle.text = need.title

        // Load image using Coil
        need.imageUrl?.let {
            holder.ivImage.load(it) {
                crossfade(true)
                placeholder(android.R.drawable.ic_menu_gallery)
            }
        }
        
        val formatter = NumberFormat.getCurrencyInstance(Locale.Builder().setLanguage("en").setRegion("IN").build())
        holder.tvCost.text = "${formatter.format(need.estimatedCost)} Needed"
        
        holder.tvStatus.text = need.status.name.replace("_", " ")
        holder.pbProgress.progress = need.progress
        holder.tvProgressLabel.text = "${need.progress}% Completed"
        
        // Pledges Info
        holder.tvPledgeCount.text = "${need.pledges.size} Pledges"
        if (need.pledges.isNotEmpty()) {
            val lastPledge = need.pledges.last()
            holder.tvRecentDonor.visibility = View.VISIBLE
            holder.tvRecentDonor.text = "Latest: ${lastPledge.donorName} - ${lastPledge.contributionType}"
        } else {
            holder.tvRecentDonor.visibility = View.GONE
        }
        
        // Priority Tag
        holder.tvPriority.text = need.priority.name
        val priorityColor = when (need.priority) {
            Priority.HIGH -> R.color.priority_high
            Priority.MEDIUM -> R.color.priority_medium
            Priority.LOW -> R.color.priority_low
        }
        holder.tvPriority.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, priorityColor))
        
        // Status Color
        val statusColor = when (need.status) {
            Status.PENDING -> R.color.status_pending
            Status.IN_PROGRESS -> R.color.status_in_progress
            Status.COMPLETED -> R.color.status_completed
        }
        holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, statusColor))

        // Role-based visibility
        if (role == Role.ADMIN) {
            holder.btnPledge.visibility = View.GONE
            holder.btnAdminAction.visibility = View.VISIBLE
            holder.btnAdminAction.text = if (need.status == Status.COMPLETED) "Completed" else "Mark Complete"
            holder.btnAdminAction.isEnabled = need.status != Status.COMPLETED
        } else {
            holder.btnPledge.visibility = View.VISIBLE
            holder.btnAdminAction.visibility = View.GONE
        }

        holder.btnPledge.setOnClickListener {
            showPledgeDialog(holder.itemView.context, position)
        }

        holder.btnAdminAction.setOnClickListener {
            val updatedNeed = need.copy(status = Status.COMPLETED, currentAmount = need.estimatedCost)
            // Update the main list as well
            val mainIndex = needs.indexOfFirst { it.id == need.id }
            if (mainIndex != -1) {
                (needs as MutableList)[mainIndex] = updatedNeed
            }
            notifyItemChanged(position)
            onAction(updatedNeed)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, SchoolProfileActivity::class.java)
            holder.itemView.context.startActivity(intent)
        }
    }

    private fun showPledgeDialog(context: android.content.Context, position: Int) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_pledge, null)
        val etName = dialogView.findViewById<TextInputEditText>(R.id.etName)
        val actvContribution = dialogView.findViewById<AutoCompleteTextView>(R.id.actvContribution)
        
        val contributionTypes = arrayOf("Sponsor Funds", "Donate Paint", "Arrange Desks", "Provide Materials", "Volunteer Time")
        val adapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, contributionTypes)
        actvContribution.setAdapter(adapter)

        MaterialAlertDialogBuilder(context)
            .setView(dialogView)
            .setPositiveButton("Pledge") { _, _ ->
                val name = etName.text.toString()
                val contribution = actvContribution.text.toString()
                
                if (name.isNotEmpty() && contribution.isNotEmpty()) {
                    val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
                    val newPledge = Pledge(name, contribution, date)
                    
                    val need = filteredNeeds[position]
                    val updatedPledges = need.pledges.toMutableList().apply { add(newPledge) }
                    val updatedNeed = need.copy(pledges = updatedPledges)
                    
                    val mainIndex = needs.indexOfFirst { it.id == need.id }
                    if (mainIndex != -1) {
                        (needs as MutableList)[mainIndex] = updatedNeed
                    }
                    notifyItemChanged(position)
                    onAction(updatedNeed)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun getItemCount() = filteredNeeds.size
}
