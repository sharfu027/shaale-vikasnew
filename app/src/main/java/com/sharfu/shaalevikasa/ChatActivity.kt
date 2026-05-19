package com.sharfu.shaalevikasa

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ChatActivity : AppCompatActivity() {

    private lateinit var adapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val rvChat: RecyclerView = findViewById(R.id.rvChat)
        val etMessage: EditText = findViewById(R.id.etMessage)
        val btnSend: FloatingActionButton = findViewById(R.id.btnSend)

        adapter = ChatAdapter(messages)
        rvChat.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        rvChat.adapter = adapter

        // Initial Greeting
        addBotMessage("Hello! I am your AI assistant for Shaale Vikasa. How can I help you today?")

        btnSend.setOnClickListener {
            val text = etMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                addUserMessage(text)
                etMessage.text.clear()
                generateAiResponse(text)
            }
        }
    }

    private fun addUserMessage(text: String) {
        messages.add(ChatMessage(text, true))
        adapter.notifyItemInserted(messages.size - 1)
        findViewById<RecyclerView>(R.id.rvChat).scrollToPosition(messages.size - 1)
    }

    private fun addBotMessage(text: String) {
        messages.add(ChatMessage(text, false))
        adapter.notifyItemInserted(messages.size - 1)
        findViewById<RecyclerView>(R.id.rvChat).scrollToPosition(messages.size - 1)
    }

    private fun generateAiResponse(userText: String) {
        val response = when {
            userText.contains("how to contribute", ignoreCase = true) -> 
                "You can contribute by clicking on any school need in the dashboard and selecting 'Pledge Support'. You can donate funds or materials."
            userText.contains("project status", ignoreCase = true) -> 
                "Projects can be 'Pending', 'In Progress', or 'Completed'. You can track real-time progress in the Impact Timeline section."
            userText.contains("who are you", ignoreCase = true) -> 
                "I am the Shaale Vikasa AI, here to help alumni and admins coordinate school infrastructure projects."
            userText.contains("hi", ignoreCase = true) || userText.contains("hello", ignoreCase = true) -> 
                "Hello! Need help finding nearby schools or understanding how to pledge?"
            else -> "That's a great question. For specific details on school projects, please check the 'Impact Gallery' or contact the school admin directly."
        }

        Handler(Looper.getMainLooper()).postDelayed({
            addBotMessage(response)
        }, 1000)
    }
}