package com.example.counttimerapp

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.myapplication.AppDatabase
import com.example.myapplication.R
import com.example.myapplication.User
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size


class MainActivity : AppCompatActivity() {

    var START_MILLI_SECONDS = 60000L

    lateinit var countdown_timer: CountDownTimer
    var isRunning: Boolean = false;
    var time_in_milli_seconds = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "adb3"
        ).allowMainThreadQueries().build()
        val adb = db.userDao()

        GlobalScope.launch {
//            db.userDao().insertAll(User(1,"b", "b"), User(2,"a", "a"))
            val data = db.userDao().getAll()

            data?.forEach {
                println(it)
            }
        }

        button.setOnClickListener {
            if (isRunning) {
                pauseTimer()
                val user1 = adb.getUser(time_edit_text.text.toString().toInt())
                textView2.text = "${user1.firstName} ${user1.lastName}"
            } else {
                val time  = time_edit_text.text.toString()
                time_in_milli_seconds = time.toLong() *60000L
                startTimer(time_in_milli_seconds)

                adb.insertAll(User(time.toInt(), "Admin"+time.toInt(), name.text.toString()))
                textView2.text = "inserted"


            }
        }

        query.setOnClickListener {
            val user1 = adb.getUser(time_edit_text.text.toString().toInt())
            textView2.text = "${user1.firstName} ${user1.lastName}"
        }

        reset.setOnClickListener {
            resetTimer()

        }


    }

    private fun pauseTimer() {

        button.text = "Start&Insert"
        countdown_timer.cancel()
        isRunning = false
        reset.visibility = View.VISIBLE
    }

    private fun startTimer(time_in_seconds: Long) {
        countdown_timer = object : CountDownTimer(time_in_seconds, 1000) {
            override fun onFinish() {
                loadConfeti()
            }

            override fun onTick(p0: Long) {
                time_in_milli_seconds = p0
                updateTextUI()
            }
        }
        countdown_timer.start()

        isRunning = true
        button.text = "Pause&Query"
        reset.visibility = View.INVISIBLE

    }

    private fun resetTimer() {
        time_in_milli_seconds = START_MILLI_SECONDS
        updateTextUI()
        reset.visibility = View.INVISIBLE
    }

    private fun updateTextUI() {
        val minute = (time_in_milli_seconds / 1000) / 60
        val seconds = (time_in_milli_seconds / 1000) % 60

        timer.text = "$minute:$seconds"
    }


    private fun loadConfeti() {
        viewKonfetti.build()
                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.RECT, Shape.CIRCLE)
                .addSizes(Size(12))
                .setPosition(-50f, viewKonfetti.width + 50f, -50f, -50f)
                .streamFor(300, 5000L)
    }
}