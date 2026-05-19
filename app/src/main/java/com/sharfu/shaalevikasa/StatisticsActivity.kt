package com.sharfu.shaalevikasa

import android.os.Bundle
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate

class StatisticsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setupPieChart()
        setupBarChart()
    }

    private fun setupPieChart() {
        val pieChart: PieChart = findViewById(R.id.pieChart)
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(45f, "Infrastructure"))
        entries.add(PieEntry(25f, "Learning Materials"))
        entries.add(PieEntry(20f, "Drinking Water"))
        entries.add(PieEntry(10f, "Sports"))

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 12f

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.description.isEnabled = false
        pieChart.centerText = "Funds Use"
        pieChart.animateY(1400)
        pieChart.invalidate()
    }

    private fun setupBarChart() {
        val barChart: BarChart = findViewById(R.id.barChart)
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(1f, 10f))
        entries.add(BarEntry(2f, 25f))
        entries.add(BarEntry(3f, 45f))
        entries.add(BarEntry(4f, 60f))
        entries.add(BarEntry(5f, 85f))

        val dataSet = BarDataSet(entries, "Monthly Projects")
        dataSet.color = ColorTemplate.MATERIAL_COLORS[0]
        
        val data = BarData(dataSet)
        barChart.data = data
        barChart.description.isEnabled = false
        barChart.animateY(1000)
        barChart.invalidate()
    }
}