package com.sharfu.shaalevikasa

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val ivLogo = findViewById<ImageView>(R.id.ivLogo)
        val tvAppName = findViewById<TextView>(R.id.tvAppName)

        // Logo Scale and Rotate Animation
        ivLogo.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .rotationBy(360f)
            .setDuration(1500)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()

        // Text Fade-in Animation
        tvAppName.animate()
            .alpha(1f)
            .setDuration(1000)
            .setStartDelay(800)
            .start()

        // Transition to LoginActivity after delay
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 2500)
    }
}