package co.nextgentrainer.kotlin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import co.nextgentrainer.R
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class FitlogCustomActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fitlog_custom)

        findViewById<Button>(R.id.backButton).setOnClickListener {
            startActivity(Intent(this, ChooserActivity::class.java))
        }

        // Mockup for now
        val formatterDay = SimpleDateFormat("dd", Locale.getDefault())
        val formatterDate = SimpleDateFormat("dd MMM", Locale.getDefault())
        val formatterYear = SimpleDateFormat("yyyy", Locale.getDefault())
        val millisecondsInAWeek = 1000 * 3600 * 24 * 7
        val dateStarting = formatterDay.format(Date(System.currentTimeMillis()))
        val dateEnding =
            formatterDate.format(Date(System.currentTimeMillis() - millisecondsInAWeek))
        val year = formatterYear.format(Date(System.currentTimeMillis()))

        findViewById<TextView>(R.id.fitLogDateTextView).text = "$dateEnding-$dateStarting"
        findViewById<TextView>(R.id.fitLogYearTextView).text = year
    }
}
