package co.nextgentrainer.kotlin.data.model

import com.github.mikephil.charting.data.Entry
import java.util.Date

data class ChartData(
    val entries: List<Entry>,
    val xTimestamps: List<Date>,
    val chartId: String,
    val chartName: String
)
