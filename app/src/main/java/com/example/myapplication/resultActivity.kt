package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class resultActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_result)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.resultLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val matchType = intent.getIntExtra("match_type", 1)
        val winner = intent.getStringExtra("winner")
        val rabbitWins = intent.getIntExtra("rabbit_wins", 0)
        val turtleWins = intent.getIntExtra("turtle_wins", 0)
        val matchCount=findViewById<TextView>(R.id.matchCount)
        val showwinner=findViewById<TextView>(R.id.winner)
        val winnerImage=findViewById<ImageView>(R.id.winnerImage)
        val BackButton= findViewById<Button>(R.id.backButton)
        val winResult =findViewById<TextView>(R.id.winResult)
        matchCount.text="${matchType.toString()}戰制比賽結果"
        showwinner.text =winner
        winResult.text="兔子${rabbitWins}勝 烏龜${turtleWins}勝"

        val winnerImages = if (winner == "兔子") R.drawable.rabbit else R.drawable.turtle
        winnerImage.setImageResource(winnerImages)
        BackButton.setOnClickListener {
            val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

    }
}