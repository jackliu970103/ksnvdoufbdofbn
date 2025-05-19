package com.example.myapplication

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class RaceRecord(
    val winner: String,
    val rabbitProgress: Int,
    val turtleProgress: Int,
    val timestamp: String
)

class MainActivity : AppCompatActivity() {

    // 進度變數
    private var progressRabbit = 0
    private var progressTurtle = 0

    // UI元件
    private lateinit var btnStart: Button
    private lateinit var sbRabbit: SeekBar
    private lateinit var sbTurtle: SeekBar
    private lateinit var rvHistory: RecyclerView

    // 資料與適配器
    private val raceRecords = mutableListOf<RaceRecord>()
    private lateinit var historyAdapter: RaceHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 初始化UI元件
        initViews()

        // 設定RecyclerView
        setupRecyclerView()

        // 開始按鈕點擊事件
        btnStart.setOnClickListener {
            startRace()
        }
    }

    private fun initViews() {
        btnStart = findViewById(R.id.btnStart)
        sbRabbit = findViewById(R.id.sbRabbit)
        sbTurtle = findViewById(R.id.sbTurtle)
        rvHistory = findViewById(R.id.rvHistory)

        sbRabbit.max = 100
        sbTurtle.max = 100
    }

    private fun setupRecyclerView() {
        historyAdapter = RaceHistoryAdapter(raceRecords)
        rvHistory.layoutManager = LinearLayoutManager(this)
        rvHistory.adapter = historyAdapter
    }

    private fun startRace() {
        btnStart.isEnabled = false
        progressRabbit = 0
        progressTurtle = 0
        sbRabbit.progress = 0
        sbTurtle.progress = 0

        runRabbit()
        runTurtle()
        if (progressRabbit >= 100 && progressTurtle >= 100) {
            handler.post {
                showToast("平手！")
                btnStart.isEnabled = true
                recordRaceResult("平手")
            }
        }
    }

    private fun recordRaceResult(winner: String) {
        val timestamp = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(Date())
        val record = RaceRecord(
            winner = winner,
            rabbitProgress = progressRabbit,
            turtleProgress = progressTurtle,
            timestamp = timestamp
        )

        raceRecords.add(0, record)
        historyAdapter.notifyItemInserted(0)
        rvHistory.smoothScrollToPosition(0)
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private val handler = Handler(Looper.getMainLooper()) { msg ->
        when (msg.what) {
            1 -> { // 兔子進度更新
                sbRabbit.progress = progressRabbit
                if (progressRabbit >= 100 && progressTurtle < 100) {
                    showToast("兔子勝利")
                    btnStart.isEnabled = true
                    recordRaceResult("兔子")
                }
            }
            2 -> { // 烏龜進度更新
                sbTurtle.progress = progressTurtle
                if (progressTurtle >= 100 && progressRabbit < 100) {
                    showToast("烏龜勝利")
                    btnStart.isEnabled = true
                    recordRaceResult("烏龜")
                }
            }
        }
        true
    }

    private fun runRabbit() {
        Thread {
            val sleepProbability = arrayOf(true, true, false) // 2/3機率偷懶
            while (progressRabbit < 100 && progressTurtle < 100) {
                try {
                    Thread.sleep(100)
                    if (sleepProbability.random()) {
                        Thread.sleep(300)
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                progressRabbit += 3

                handler.sendMessage(Message().apply { what = 1 })
            }

            // 檢查平手情況

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

                progressTurtle += 1

                handler.sendMessage(Message().apply { what = 2 })
            }

            // 檢查平手情況

        }.start()
    }
}

class RaceHistoryAdapter(private val raceRecords: List<RaceRecord>) :
    RecyclerView.Adapter<RaceHistoryAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivWinner: ImageView = view.findViewById(R.id.ivWinner)
        val tvWinner: TextView = view.findViewById(R.id.tvWinner)
        val tvProgress: TextView = view.findViewById(R.id.tvProgress)
        val tvTimestamp: TextView = view.findViewById(R.id.tvTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_race_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = raceRecords[position]

        // 設置獲勝者圖示
        when (record.winner) {
            "兔子" -> holder.ivWinner.setImageResource(R.drawable.rabbit)
            "烏龜" -> holder.ivWinner.setImageResource(R.drawable.turtle)
            "平手" -> holder.ivWinner.setImageResource(R.drawable.draw) // 需添加平手圖示
        }

        // 設置文字
        holder.tvWinner.text = "贏家: ${record.winner}"
        if (record.winner.equals("兔子")){
            holder.tvProgress.text = "兔子: ${record.rabbitProgress-2}% - 烏龜: ${record.turtleProgress}%"
        }else if (record.winner.equals("烏龜")){
            holder.tvProgress.text = "兔子: ${record.rabbitProgress}% - 烏龜: ${record.turtleProgress}%"
        }else{
            holder.tvProgress.text = "兔子: ${record.rabbitProgress-2}% - 烏龜: ${record.turtleProgress}%"
        }

        holder.tvTimestamp.text = record.timestamp
    }

    override fun getItemCount() = raceRecords.size
}