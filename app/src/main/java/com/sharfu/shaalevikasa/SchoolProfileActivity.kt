package com.sharfu.shaalevikasa

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import coil.load
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

class SchoolProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_school_profile)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        val ivHeader: ImageView = findViewById(R.id.ivSchoolHeader)
        val tvName: TextView = findViewById(R.id.tvSchoolName)
        val tvLocation: TextView = findViewById(R.id.tvVillageLocation)
        val tvStudents: TextView = findViewById(R.id.tvTotalStudents)
        val tvImpacts: TextView = findViewById(R.id.tvCompletedProjects)
        val tvScore: TextView = findViewById(R.id.tvInfraScore)

        // Loading a high-quality real school image
        ivHeader.load("https://images.unsplash.com/photo-1541339907198-e08759dfc3f0?q=80&w=1200&auto=format&fit=crop") {
            crossfade(true)
        }

        tvName.text = "Govt High School - Malleshwaram"
        tvLocation.text = "Malleshwaram, Bangalore North"
        tvStudents.text = "524"
        tvImpacts.text = "18"
        tvScore.text = "7.8/10"

        setupQrCode()
    }

    private fun setupQrCode() {
        val ivQrCode: ImageView = findViewById(R.id.ivQrCode)
        val projectUrl = "https://shaalevikasa.gov.in/school/malleshwaram" // Example URL

        try {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.encodeBitmap(projectUrl, BarcodeFormat.QR_CODE, 400, 400)
            ivQrCode.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}