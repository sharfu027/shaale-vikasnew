package com.sharfu.shaalevikasa

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SuccessStoriesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success_stories)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val rvSuccess: RecyclerView = findViewById(R.id.rvSuccessStories)
        rvSuccess.layoutManager = LinearLayoutManager(this)
        
        val stories = getSampleSuccessStories()
        rvSuccess.adapter = SuccessStoriesAdapter(stories)
    }

    private fun getSampleSuccessStories(): List<SchoolNeed> {
        return listOf(
            SchoolNeed(
                "S1", "Modern Science Lab", 
                "Fully equipped science laboratory for high school students.",
                50000.0, 50000.0, 
                Priority.MEDIUM, Status.COMPLETED,
                imageUrl = "https://images.unsplash.com/photo-1532094349884-543bc11b234d?q=80&w=800&auto=format&fit=crop",
                testimonial = "\"Our students now have the tools to explore scientific concepts practically. It has sparked a new-found interest in STEM.\"",
                appreciationNote = "Heartfelt thanks to the Global Alumni Network for donating ₹50,000 for the equipment."
            ),
            SchoolNeed(
                "S2", "Digital Library", 
                "Implementation of 10 computer stations and a digital catalog.",
                80000.0, 80000.0, 
                Priority.HIGH, Status.COMPLETED,
                imageUrl = "https://images.unsplash.com/photo-1507842217343-583bb7270b66?q=80&w=800&auto=format&fit=crop",
                testimonial = "\"Access to digital resources has bridged the gap between rural and urban education for our children.\"",
                appreciationNote = "Grateful to Mr. Rajesh Khanna (Batch of '85) for sponsoring the hardware and software."
            ),
            SchoolNeed(
                "S3", "Safe Play Area", 
                "Modern playground with safety padding and new equipment.",
                35000.0, 35000.0, 
                Priority.LOW, Status.COMPLETED,
                imageUrl = "https://images.unsplash.com/photo-1588072432836-e10032774350?q=80&w=800&auto=format&fit=crop",
                testimonial = "\"The children's physical well-being and happiness have improved significantly since the new playground opened.\"",
                appreciationNote = "A big shoutout to the local community and volunteers who helped with the installation."
            )
        )
    }
}
