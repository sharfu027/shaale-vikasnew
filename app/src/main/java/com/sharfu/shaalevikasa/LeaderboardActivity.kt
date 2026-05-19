package com.sharfu.shaalevikasa

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.ChipGroup

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var adapter: LeaderboardAdapter
    private lateinit var rvLeaderboard: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        rvLeaderboard = findViewById(R.id.rvLeaderboard)
        rvLeaderboard.layoutManager = LinearLayoutManager(this)
        
        adapter = LeaderboardAdapter(getTopAlumni())
        rvLeaderboard.adapter = adapter

        val chipGroup: ChipGroup = findViewById(R.id.chipGroupLeaderboard)
        chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            when (checkedIds.firstOrNull()) {
                R.id.chipTopAlumni -> adapter.updateData(getTopAlumni())
                R.id.chipVolunteers -> adapter.updateData(getActiveVolunteers())
                R.id.chipMonthly -> adapter.updateData(getMonthlyContributors())
            }
        }
    }

    private fun getTopAlumni(): List<Contributor> {
        return listOf(
            Contributor(1, "Suresh Kumar", "Platinum Donor", "15,000 pts"),
            Contributor(2, "Anita Rao", "Gold Contributor", "12,200 pts"),
            Contributor(3, "Rajesh Khanna", "Silver Partner", "9,500 pts"),
            Contributor(4, "Meera Deshmukh", "Silver Partner", "8,100 pts"),
            Contributor(5, "Vikram Singh", "Active Member", "5,000 pts")
        )
    }

    private fun getActiveVolunteers(): List<Contributor> {
        return listOf(
            Contributor(1, "Mahesh Babu", "Master Volunteer", "45 Tasks"),
            Contributor(2, "Sunita Williams", "Expert Helper", "32 Tasks"),
            Contributor(3, "Rahul Dravid", "Dedicated Volunteer", "28 Tasks"),
            Contributor(4, "Priya Mani", "Active Volunteer", "15 Tasks")
        )
    }

    private fun getMonthlyContributors(): List<Contributor> {
        return listOf(
            Contributor(1, "Alumni Batch 2005", "Monthly Champion", "₹25,000"),
            Contributor(2, "Kiran Mazumdar", "Regular Donor", "₹10,000"),
            Contributor(3, "Nandan Nilekani", "Consistent Helper", "₹8,500")
        )
    }
}