package com.sharfu.shaalevikasa

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.os.Handler
import android.os.Looper
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    private lateinit var sampleNeeds: MutableList<SchoolNeed>
    private lateinit var galleryAdapter: GalleryAdapter
    private lateinit var needsAdapter: NeedsAdapter
    private lateinit var timelineAdapter: TimelineAdapter
    private lateinit var leaderboardAdapter: LeaderboardAdapter
    private var currentUploadNeed: SchoolNeed? = null
    private var isBeforeUpload: Boolean = true
    private lateinit var userRole: Role

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val index = sampleNeeds.indexOfFirst { it.id == currentUploadNeed?.id }
            if (index != -1) {
                val need = sampleNeeds[index]
                val updatedNeed = if (isBeforeUpload) {
                    need.copy(beforeImageUrl = uri.toString())
                } else {
                    need.copy(afterImageUrl = uri.toString())
                }
                sampleNeeds[index] = updatedNeed
                
                // Important: Update the data in the gallery adapter
                updateGallery()
                
                // Also update needs adapter if needed
                needsAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        userRole = Role.valueOf(intent.getStringExtra("EXTRA_ROLE") ?: Role.ALUMNI.name)
        
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = if (userRole == Role.ADMIN) "Admin Panel" else "Shaale Vikasa"

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        val rvNeeds: RecyclerView = findViewById(R.id.rvNeeds)
        val rvGallery: RecyclerView = findViewById(R.id.rvGallery)
        val rvTimeline: RecyclerView = findViewById(R.id.rvTimeline)
        val rvLeaderboard: RecyclerView = findViewById(R.id.rvLeaderboard)
        val fabAddNeed: FloatingActionButton = findViewById(R.id.fabAddNeed)
        val fabAiChat: FloatingActionButton = findViewById(R.id.fabAiChat)
        val bottomNav: BottomNavigationView = findViewById(R.id.bottomNavigation)
        
        val searchView: SearchView = findViewById(R.id.searchView)
        val chipGroupFilters: ChipGroup = findViewById(R.id.chipGroupFilters)

        // Admin can see FAB to add needs
        fabAddNeed.visibility = if (userRole == Role.ADMIN) View.VISIBLE else View.GONE

        sampleNeeds = getSampleData().toMutableList()

        // Set up Adapters
        needsAdapter = NeedsAdapter(sampleNeeds, userRole) {
            updateTimeline()
            updateGallery()
        }
        rvNeeds.layoutManager = LinearLayoutManager(this)
        rvNeeds.adapter = needsAdapter

        // Setup Search and Filters
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filterData(newText ?: "", getSelectedFilter(chipGroupFilters))
                return true
            }
        })

        chipGroupFilters.setOnCheckedStateChangeListener { group, checkedIds ->
            val checkedId = checkedIds.firstOrNull()
            val filterText = if (checkedId != null) findViewById<Chip>(checkedId).text.toString() else "All"
            filterData(searchView.query.toString(), filterText)
        }

        galleryAdapter = GalleryAdapter(getGalleryData()) { need, isBefore ->
            currentUploadNeed = need
            isBeforeUpload = isBefore
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        rvGallery.layoutManager = LinearLayoutManager(this)
        rvGallery.adapter = galleryAdapter

        timelineAdapter = TimelineAdapter(getTimelineData())
        rvTimeline.layoutManager = LinearLayoutManager(this)
        rvTimeline.adapter = timelineAdapter

        leaderboardAdapter = LeaderboardAdapter(getTopAlumniData())
        rvLeaderboard.layoutManager = LinearLayoutManager(this)
        rvLeaderboard.adapter = leaderboardAdapter

        // Navigation Logic
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    showView(rvNeeds, toolbar, "Shaale Vikasa", fabAddNeed)
                    true
                }
                R.id.nav_gallery -> {
                    updateGallery()
                    showView(rvGallery, toolbar, "Impact Gallery", null)
                    true
                }
                R.id.nav_timeline -> {
                    updateTimeline()
                    showView(rvTimeline, toolbar, "Project Timeline", null)
                    true
                }
                R.id.nav_maps -> {
                    startActivity(Intent(this, MapsActivity::class.java))
                    false
                }
                R.id.nav_leaderboard -> {
                    showView(rvLeaderboard, toolbar, "Leaderboard", null)
                    true
                }
                else -> false
            }
        }

        fabAddNeed.setOnClickListener {
            showAddNeedDialog()
        }

        fabAiChat.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }
    }

    private fun getSelectedFilter(group: ChipGroup): String {
        val checkedId = group.checkedChipId
        return if (checkedId != -1) findViewById<Chip>(checkedId).text.toString() else "All"
    }

    private fun filterData(query: String, filter: String) {
        needsAdapter.filter(query, filter)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_stats -> {
                startActivity(Intent(this, StatisticsActivity::class.java))
                true
            }
            R.id.action_stories -> {
                startActivity(Intent(this, SuccessStoriesActivity::class.java))
                true
            }
            R.id.action_logout -> {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAddNeedDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_need, null)
        val etTitle = dialogView.findViewById<TextInputEditText>(R.id.etTitle)
        val etCost = dialogView.findViewById<TextInputEditText>(R.id.etCost)
        val actvPriority = dialogView.findViewById<AutoCompleteTextView>(R.id.actvPriority)
        
        val priorities = Priority.values().map { it.name }.toTypedArray()
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, priorities)
        actvPriority.setAdapter(adapter)

        dialogView.findViewById<Button>(R.id.btnAiAnalyze).setOnClickListener {
            val title = etTitle.text.toString()
            if (title.isNotEmpty()) {
                showAiAnalysisDialog(title) { recommendedPriority ->
                    actvPriority.setText(recommendedPriority.name, false)
                }
            } else {
                Toast.makeText(this, "Enter a title first", Toast.LENGTH_SHORT).show()
            }
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Add New School Need")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val title = etTitle.text.toString()
                val cost = etCost.text.toString().toDoubleOrNull() ?: 0.0
                val priority = Priority.valueOf(actvPriority.text.toString())
                
                val newNeed = SchoolNeed(
                    id = (sampleNeeds.size + 1).toString(),
                    title = title,
                    description = "",
                    estimatedCost = cost,
                    currentAmount = 0.0,
                    priority = priority,
                    status = Status.PENDING,
                    timeline = listOf(TimelineEvent("Today", "Need Listed", "Added by Admin"))
                )
                
                sampleNeeds.add(0, newNeed)
                needsAdapter.notifyItemInserted(0)
                updateTimeline()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAiAnalysisDialog(title: String, onRecommendation: (Priority) -> Unit) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_ai_priority, null)
        val cardResult = dialogView.findViewById<View>(R.id.cardAiResult)
        val pbAnalyzing = dialogView.findViewById<ProgressBar>(R.id.pbAiAnalyzing)
        val tvRecommendation = dialogView.findViewById<TextView>(R.id.tvAiRecommendation)
        val tvReasoning = dialogView.findViewById<TextView>(R.id.tvAiReasoning)

        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .setPositiveButton("Apply Recommendation") { _, _ ->
                val priority = if (tvRecommendation.text.contains("HIGH")) Priority.HIGH else Priority.MEDIUM
                onRecommendation(priority)
            }
            .setNegativeButton("Close", null)
            .show()

        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).isEnabled = false

        // Simulate AI Analysis
        Handler(Looper.getMainLooper()).postDelayed({
            pbAnalyzing.visibility = View.GONE
            cardResult.visibility = View.VISIBLE
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).isEnabled = true

            if (title.lowercase().contains("roof") || title.lowercase().contains("water") || title.lowercase().contains("safety")) {
                tvRecommendation.text = "Recommendation: HIGH PRIORITY"
                tvReasoning.text = "Analysis: This need directly impacts student safety and basic health requirements. Immediate intervention recommended."
            } else {
                tvRecommendation.text = "Recommendation: MEDIUM PRIORITY"
                tvReasoning.text = "Analysis: This need impacts the quality of education and environment, but does not present an immediate safety hazard."
            }
        }, 2000)
    }

    private fun updateTimeline() {
        timelineAdapter = TimelineAdapter(getTimelineData())
        findViewById<RecyclerView>(R.id.rvTimeline).adapter = timelineAdapter
    }

    private fun updateGallery() {
        galleryAdapter = GalleryAdapter(getGalleryData()) { need, isBefore ->
            currentUploadNeed = need
            isBeforeUpload = isBefore
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        findViewById<RecyclerView>(R.id.rvGallery).adapter = galleryAdapter
    }

    private fun getGalleryData() = sampleNeeds.filter { it.status == Status.COMPLETED || it.status == Status.IN_PROGRESS }

    private fun getTimelineData(): List<TimelineEvent> {
        val allEvents = sampleNeeds.flatMap { need -> 
            val events = need.timeline.map { it.copy(title = "${need.title}: ${it.title}") }.toMutableList()
            need.pledges.forEach { pledge ->
                events.add(TimelineEvent(pledge.date, "${need.title}: New Pledge", "${pledge.donorName} pledged to ${pledge.contributionType}"))
            }
            events
        }
        return allEvents.sortedByDescending { it.date }
    }

    private fun showView(viewToShow: View, toolbar: Toolbar, title: String, fab: FloatingActionButton?) {
        findViewById<View>(R.id.rvNeeds).visibility = View.GONE
        findViewById<View>(R.id.rvGallery).visibility = View.GONE
        findViewById<View>(R.id.rvTimeline).visibility = View.GONE
        findViewById<View>(R.id.rvLeaderboard).visibility = View.GONE
        
        // Keep filterLayout visible as it contains all the RecyclerViews
        findViewById<View>(R.id.filterLayout).visibility = View.VISIBLE
        
        // Hide/Show specific filters only for the Dashboard (rvNeeds)
        findViewById<View>(R.id.searchView).visibility = if (viewToShow.id == R.id.rvNeeds) View.VISIBLE else View.GONE
        findViewById<View>(R.id.filterChipsScroll).visibility = if (viewToShow.id == R.id.rvNeeds) View.VISIBLE else View.GONE
        
        viewToShow.visibility = View.VISIBLE
        toolbar.title = title
        
        if (userRole == Role.ADMIN) {
            findViewById<FloatingActionButton>(R.id.fabAddNeed).visibility = if (fab != null) View.VISIBLE else View.GONE
        }
    }

    private fun getTopAlumniData(): List<Contributor> {
        return listOf(
            Contributor(1, "Arjun Mehta", "Diamond Contributor", "15,200 pts", "https://randomuser.me/api/portraits/men/32.jpg"),
            Contributor(2, "Siddharth Rao", "Platinum Partner", "12,800 pts", "https://randomuser.me/api/portraits/men/44.jpg"),
            Contributor(3, "Priyanka Sharma", "Gold Member", "10,500 pts", "https://randomuser.me/api/portraits/women/68.jpg"),
            Contributor(4, "Aditi Iyer", "Silver Contributor", "8,900 pts", "https://randomuser.me/api/portraits/women/43.jpg"),
            Contributor(5, "Rohan Deshmukh", "Silver Contributor", "7,400 pts", "https://randomuser.me/api/portraits/men/52.jpg"),
            Contributor(6, "Kavita Nair", "Active Volunteer", "5,200 pts", "https://randomuser.me/api/portraits/women/24.jpg"),
            Contributor(7, "Sanjay Patil", "Bronze Contributor", "4,800 pts", "https://randomuser.me/api/portraits/men/75.jpg"),
            Contributor(8, "Anjali Gupta", "Active Volunteer", "4,500 pts", "https://randomuser.me/api/portraits/women/22.jpg"),
            Contributor(9, "Vikram Singh", "Community Leader", "3,900 pts", "https://randomuser.me/api/portraits/men/85.jpg"),
            Contributor(10, "Sneha Kulkarni", "Bronze Contributor", "3,200 pts", "https://randomuser.me/api/portraits/women/44.jpg"),
            Contributor(11, "Rahul Verma", "Rising Star", "2,800 pts", "https://randomuser.me/api/portraits/men/22.jpg")
        )
    }

    private fun getSampleData(): List<SchoolNeed> {
        return listOf(
            SchoolNeed(
                "1", "Classroom Roof Repair", 
                "Repairing the leaking roof in Grade 5 classroom to prevent water damage during monsoon.",
                15000.0, 10500.0, 
                Priority.HIGH, Status.IN_PROGRESS,
                imageUrl = "https://images.unsplash.com/photo-1519452575417-564c1401ecc0?q=80&w=800&auto=format&fit=crop",
                timeline = listOf(
                    TimelineEvent("20 Oct 2023", "Need Identified", "Leaking noticed after heavy rains."),
                    TimelineEvent("25 Oct 2023", "Funds Pledged", "Alumni batch 2005 pledged ₹10,000."),
                    TimelineEvent("01 Nov 2023", "Work Started", "Tiles removed and waterproofing started.")
                ),
                pledges = listOf(
                    Pledge("Suresh Kumar", "Sponsor Funds", "25 Oct 2023")
                )
            ),
            SchoolNeed(
                "2", "5 Sets of Wall Paints", 
                "Refreshing the colors of the primary block to create a vibrant learning environment.",
                5000.0, 1500.0, 
                Priority.MEDIUM, Status.PENDING,
                imageUrl = "https://images.unsplash.com/photo-1562664377-709f2c337eb2?q=80&w=800&auto=format&fit=crop"
            ),
            SchoolNeed(
                "3", "New Drinking Water Filter", 
                "Installation of a RO water purifier to provide clean drinking water for 400+ students.",
                12000.0, 12000.0, 
                Priority.HIGH, Status.COMPLETED,
                imageUrl = "https://images.unsplash.com/photo-1544333346-64e4fe18204b?q=80&w=800&auto=format&fit=crop",
                timeline = listOf(
                    TimelineEvent("10 Oct 2023", "Need Listed", "Old filter beyond repair."),
                    TimelineEvent("15 Oct 2023", "Fully Funded", "Local businessman donated full amount."),
                    TimelineEvent("22 Oct 2023", "Installation Complete", "RO unit installed and tested.")
                ),
                pledges = listOf(
                    Pledge("Mahesh Babu", "Sponsor Funds", "15 Oct 2023")
                )
            ),
            SchoolNeed(
                "4", "Library Books (Science)", 
                "Adding latest science encyclopedias and space exploration books to the library.",
                3000.0, 0.0, 
                Priority.LOW, Status.PENDING,
                imageUrl = "https://images.unsplash.com/photo-1503676260728-1c00da094a0b?q=80&w=800&auto=format&fit=crop"
            ),
            SchoolNeed(
                "5", "Broken Desk Repair", 
                "Fixing 10 broken wooden desks in the middle school to improve classroom comfort.",
                4000.0, 2000.0, 
                Priority.MEDIUM, Status.IN_PROGRESS,
                imageUrl = "https://images.unsplash.com/photo-1524178232363-1fb2b075b655?q=80&w=800&auto=format&fit=crop",
                timeline = listOf(
                    TimelineEvent("02 Nov 2023", "Carpentry Started", "Carpenter fixing the wooden frames.")
                ),
                pledges = listOf(
                    Pledge("Anita Rao", "Arrange Desks", "02 Nov 2023")
                )
            )
        )
    }
}
