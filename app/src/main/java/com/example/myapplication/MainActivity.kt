package com.example.myapplication

import android.content.Intent
import android.graphics.Color
import android.os.*
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

data class RaceResult(
    var winner: String,
    var rabbit: Int,
    var turtle: Int,
    var timestamps: String
)

class MainActivity : AppCompatActivity() {

    private lateinit var spnMatches: Spinner
    private var best0f = 1
    private var rabbitwin = 0
    private var turtlewin = 0

    private lateinit var adapter: RaceResultAdapter
    private val raceResults = mutableListOf<RaceResult>()
    private lateinit var rvHistory: RecyclerView

    private var progressRabbit = 0
    private var progressTurtle = 0

    private lateinit var sbrabbitspeed: SeekBar
    private lateinit var sbturtlespeed: SeekBar
    private lateinit var tvrabbitspeed: TextView
    private lateinit var tvturulespeed: TextView
    private var turtlespeed = 1
    private var rabbitspeed = 3

    private lateinit var btnStart: Button
    private lateinit var sbRabbit: SeekBar
    private lateinit var sbTurtle: SeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        spnMatches = findViewById(R.id.spnMatches)
        setupspn()

        sbrabbitspeed = findViewById(R.id.sbRabbitSpeed)
        sbturtlespeed = findViewById(R.id.sbTurtleSpeed)
        tvrabbitspeed = findViewById(R.id.tvRabbitSpeedValue)
        tvturulespeed = findViewById(R.id.tvTurtleSpeedValue)
        setupSpeedControls()

        rvHistory = findViewById(R.id.rvHistory)
        rvHistory.layoutManager = LinearLayoutManager(this)
        adapter = RaceResultAdapter(raceResults)
        rvHistory.adapter = adapter

        btnStart = findViewById(R.id.btnStart)
        sbRabbit = findViewById(R.id.sbRabbit)
        sbTurtle = findViewById(R.id.sbTurtle)

        btnStart.setOnClickListener {
            btnStart.isEnabled = false
            progressRabbit = 0
            progressTurtle = 0
            sbRabbit.progress = 0
            sbTurtle.progress = 0
            runRabbit()
            runTurtle()
        }
    }

    private fun setupspn() {
        val matchOption = arrayOf("1戰1勝", "3戰2勝", "5戰3勝", "7戰4勝")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, matchOption)
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        spnMatches.adapter = adapter
        spnMatches.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                best0f = when (position) {
                    0 -> 1
                    1 -> 3
                    2 -> 5
                    3 -> 7
                    else -> 1
                }
                resetWinCount()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                best0f = 1
            }
        }
    }

    private fun resetWinCount() {
        rabbitwin = 0
        turtlewin = 0
        raceResults.clear()
        adapter.notifyDataSetChanged()
        showToast("已重置為 ${best0f} 戰")
    }

    private fun setupSpeedControls() {
        sbrabbitspeed.progressDrawable.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN)
        sbrabbitspeed.thumb.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN)
        sbturtlespeed.progressDrawable.setColorFilter(Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN)
        sbturtlespeed.thumb.setColorFilter(Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN)

        sbrabbitspeed.max = 9
        sbrabbitspeed.progress = 2
        sbturtlespeed.max = 9
        sbturtlespeed.progress = 0

        rabbitspeed = 3
        turtlespeed = 1
        tvrabbitspeed.text = "$rabbitspeed"
        tvturulespeed.text = "$turtlespeed"

        sbrabbitspeed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                rabbitspeed = progress + 1
                tvrabbitspeed.text = "$rabbitspeed"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        sbturtlespeed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                turtlespeed = progress + 1
                tvturulespeed.text = "$turtlespeed"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun showToast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    private val handler = Handler(Looper.getMainLooper()) { msg ->
        if (msg.what == 1) {
            sbRabbit.progress = progressRabbit
            if (progressRabbit >= 100 && progressTurtle < 100) {
                rabbitwin++
                recordResult("兔子")
                showToast("兔子獲勝")
                checkWinCondition()
                btnStart.isEnabled = true
            }
        } else if (msg.what == 2) {
            sbTurtle.progress = progressTurtle
            if (progressTurtle >= 100 && progressRabbit < 100) {
                turtlewin++
                recordResult("烏龜")
                showToast("烏龜獲勝")
                checkWinCondition()
                btnStart.isEnabled = true
            }
        }
        true
    }

    private fun runRabbit() {
        Thread {
            val sleepProbability = arrayOf(true, true, false)
            while (progressRabbit < 100 && progressTurtle < 100) {
                try {
                    Thread.sleep(100)
                    if (sleepProbability.random()) Thread.sleep(300)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                progressRabbit += rabbitspeed
                handler.sendMessage(Message().apply { what = 1 })
            }
        }.start()
    }

    private fun runTurtle() {
        Thread {
            while (progressTurtle < 100 && progressRabbit < 100) {
                try {
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                progressTurtle += turtlespeed
                handler.sendMessage(Message().apply { what = 2 })
            }
        }.start()
    }

    private fun recordResult(winner: String) {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Taipei")
        val timestamp = dateFormat.format(Date())

        val result = RaceResult(winner, progressRabbit, progressTurtle, timestamp)
        raceResults.add(0, result)
        adapter.notifyItemInserted(0)
        rvHistory.scrollToPosition(0)
    }

    private fun checkWinCondition() {
        val neededWins = (best0f / 2) + 1
        if (rabbitwin == neededWins || turtlewin == neededWins) {
            val winner = if (rabbitwin == neededWins) "兔子" else "烏龜"

            val intent = Intent(this, resultActivity::class.java).apply {
                putExtra("match_type", best0f)
                putExtra("winner", winner)
                putExtra("rabbit_wins", rabbitwin)
                putExtra("turtle_wins", turtlewin)
            }

            startActivity(intent)
            raceResults.clear()
            adapter.notifyDataSetChanged()
            resetWinCount()
        }
    }
}
