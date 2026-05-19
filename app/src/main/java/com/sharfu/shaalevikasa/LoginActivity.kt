package com.sharfu.shaalevikasa

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        val tvRegisterLink = findViewById<TextView>(R.id.tvRegisterLink)
        val pbLoading = findViewById<ProgressBar>(R.id.pbLoading)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                btnLogin.isEnabled = false
                pbLoading.visibility = View.VISIBLE
                
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid
                            if (userId != null) {
                                db.collection("users").document(userId).get()
                                    .addOnSuccessListener { document ->
                                        pbLoading.visibility = View.GONE
                                        val roleStr = document.getString("role") ?: Role.ALUMNI.name
                                        val role = try { Role.valueOf(roleStr) } catch(e: Exception) { Role.ALUMNI }
                                        
                                        val intent = Intent(this, MainActivity::class.java).apply {
                                            putExtra("EXTRA_ROLE", role.name)
                                            putExtra("EXTRA_EMAIL", email)
                                        }
                                        startActivity(intent)
                                        finish()
                                    }
                                    .addOnFailureListener {
                                        pbLoading.visibility = View.GONE
                                        // Fallback if role retrieval fails
                                        val intent = Intent(this, MainActivity::class.java).apply {
                                            putExtra("EXTRA_ROLE", Role.ALUMNI.name)
                                            putExtra("EXTRA_EMAIL", email)
                                        }
                                        startActivity(intent)
                                        finish()
                                    }
                            }
                        } else {
                            btnLogin.isEnabled = true
                            pbLoading.visibility = View.GONE
                            Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        tvRegisterLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
