package pl.koain.sports.maestry.demo.java

import pl.koain.sports.maestry.demo.java.utils.Repetition
import pl.koain.sports.maestry.demo.java.utils.ExerciseSet
import pl.koain.sports.maestry.demo.R
import androidx.annotation.RequiresApi
import android.os.Build.VERSION_CODES
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.charts.PieChart
import com.google.gson.GsonBuilder
import com.google.gson.JsonStreamParser
import com.google.gson.JsonSyntaxException
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.LayoutRes
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.*
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.function.ToDoubleFunction
import java.util.stream.Collectors

@RequiresApi(VERSION_CODES.O)
class FitLogActivity : AppCompatActivity(), View.OnClickListener {
    private var summedReps = 0
    private val repsOfEachExercise: MutableMap<String?, Float> = HashMap()
    override fun onCreate(savedInstanceState: Bundle?) {
//        if (BuildConfig.DEBUG) {
//            StrictMode.setThreadPolicy(
//                    new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
//            StrictMode.setVmPolicy(
//                    new StrictMode.VmPolicy.Builder()
//                            .detectLeakedSqlLiteObjects()
//                            .detectLeakedClosableObjects()
//                            .penaltyLog()
//                            .build());
//        }
        super.onCreate(savedInstanceState)
        val context = applicationContext
        val cacheFilename = context.getString(R.string.cache_filename)
        Log.d(TAG, cacheFilename)
        val separateSessions = readHistoryFromFile(context, cacheFilename)
        setContentView(R.layout.activity_fit_log)
        //        TextView tv = (TextView) findViewById(R.id.reps_number_total);
//        tv.setText(String.format("%d", summedReps));
        fillQualityProgressChart(separateSessions)
        fillPieChartWithData(repsOfEachExercise)
        // Set up ListView and Adapter
        val listView = findViewById<ListView>(R.id.list_sessions)
        val adapter = MyArrayAdapter(this, android.R.layout.simple_list_item_2, separateSessions)
        listView.adapter = adapter
    }

    private fun fillPieChartWithData(repsOfEachExercise: Map<String?, Float>) {
        val entries = repsOfEachExercise.entries.stream().map { (key, value): Map.Entry<String?, Float> -> PieEntry(value, key!!.split("_").toTypedArray()[0].uppercase()) }
                .collect(Collectors.toList())
        val set = PieDataSet(entries, "")
        set.sliceSpace = 2f
        set.setColors(*ColorTemplate.VORDIPLOM_COLORS)
        set.valueTextColor = Color.BLACK
        val data = PieData(set)
        val pieChart = findViewById<PieChart>(R.id.pieChart)
        val legend = pieChart.legend
        legend.isEnabled = false
        val description = Description()
        description.text = ""
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.description = description
        pieChart.data = data
        //pieChart.setDrawEntryLabels(true);
        pieChart.centerText = String.format("Total: %d", summedReps)
        //pieChart.setHoleRadius(75);
        pieChart.setTouchEnabled(true)
        pieChart.animateX(1000)
        //pieChart.invalidate(); // refresh
    }

    private fun readHistoryFromFile(context: Context, cacheFilename: String): MutableList<Map<String?, MutableList<ExerciseSet>>> {
        var whatShouldBeSessionSize = 0
        var setsAppearedSoFarForExercise: MutableMap<String?, MutableList<ExerciseSet>> = HashMap()
        val allSessions: MutableList<Map<String?, MutableList<ExerciseSet>>> = ArrayList()
        try {
            context.openFileInput(cacheFilename).use { inputStreamFromFile ->
                InputStreamReader(inputStreamFromFile, StandardCharsets.UTF_8).use { reader ->
                    whatShouldBeSessionSize++
                    val gson = GsonBuilder().create()
                    val jsonStreamParserToObject = JsonStreamParser(reader)
                    var lastTimestamp: Date? = null
                    while (jsonStreamParserToObject.hasNext()) {
                        val singleJsonElement = jsonStreamParserToObject.next()
                        if (singleJsonElement.isJsonObject) {
                            val loadedRepetition = gson.fromJson(singleJsonElement, Repetition::class.java)
                            val actualTimestamp = loadedRepetition.timestamp
                            lastTimestamp = lastTimestamp ?: actualTimestamp
                            if (!isSameDayUsingInstant(lastTimestamp!!, actualTimestamp!!)) {
                                whatShouldBeSessionSize++
                                allSessions.add(setsAppearedSoFarForExercise)
                                setsAppearedSoFarForExercise = HashMap()
                                lastTimestamp = actualTimestamp
                            }
                            setsAppearedSoFarForExercise = addRepetitionToExerciseSet(setsAppearedSoFarForExercise, loadedRepetition)
                            addRepetitionToCounterAndFavourites(loadedRepetition)
                        }
                    }
                    if (allSessions.isEmpty() || allSessions.size != whatShouldBeSessionSize) {
                        allSessions.add(setsAppearedSoFarForExercise)
                    }
                }
            }
        } catch (e: JsonSyntaxException) {
            Log.d(TAG, e.message!!)
            e.printStackTrace()
            return ArrayList()
        } catch (e: IOException) {
            Log.d(TAG, e.message!!)
            e.printStackTrace()
            return ArrayList()
        }
        return allSessions
    }

    private fun addRepetitionToCounterAndFavourites(loadedRepetition: Repetition) {
        summedReps += 1
        val posename = loadedRepetition.poseName
        if (repsOfEachExercise.containsKey(posename)) {
            repsOfEachExercise[posename] = repsOfEachExercise[posename]!! + 1f
        } else {
            repsOfEachExercise[posename] = 1f
        }
    }

    private fun addRepetitionToExerciseSet(setsAppearedSoFarForExercise: MutableMap<String?, MutableList<ExerciseSet>>,
                                           loadedRepetition: Repetition): MutableMap<String?, MutableList<ExerciseSet>> {
        val exerciseName = loadedRepetition.poseName
        if (setsAppearedSoFarForExercise.containsKey(exerciseName)) {
            val lastIdx = setsAppearedSoFarForExercise[exerciseName]!!.size
            val lastSet = setsAppearedSoFarForExercise[exerciseName]!![lastIdx - 1]
            val lastRep = lastSet.repetitions[lastSet.repetitions.size - 1]
            if (repetitionShouldBeInTheSameSet(lastRep, loadedRepetition)) {
                setsAppearedSoFarForExercise[exerciseName]!![lastIdx - 1].addRepetition(loadedRepetition)
            } else {
                val newSet = ExerciseSet(lastIdx + 1)
                newSet.addRepetition(loadedRepetition)
                setsAppearedSoFarForExercise[exerciseName]!!.add(newSet)
            }
        } else {
            val newSet = ExerciseSet(1)
            newSet.addRepetition(loadedRepetition)
            val newList: MutableList<ExerciseSet> = ArrayList()
            newList.add(newSet)
            setsAppearedSoFarForExercise[exerciseName] = newList
        }
        return setsAppearedSoFarForExercise
    }

    private fun repetitionShouldBeInTheSameSet(lastRep: Repetition, loadedRepetition: Repetition): Boolean {
        return (((lastRep.repetitionCounter?.numRepeats ?: 999) <
                (loadedRepetition.repetitionCounter?.numRepeats ?: 9999)
                && isNoLaterThan15s(loadedRepetition.timestamp, lastRep.timestamp)))
    }

    override fun onClick(v: View) {
        //TODO
    }

    class MyArrayAdapter(context: Context, @LayoutRes private val resource: Int, private val objects:  MutableList<Map<String?, MutableList<ExerciseSet>>>) : ArrayAdapter<Map<String?, MutableList<ExerciseSet>>>(context, resource, objects) {
        //val objects: List<Map<String?, MutableList<ExerciseSet>>>

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var view = convertView
            if (convertView == null) {
                val inflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
                view = inflater.inflate(android.R.layout.simple_list_item_2, null)
            }
            val firstSessionSet = objects[position].entries.stream().findFirst()
            val formatter = SimpleDateFormat("yyyy-MM-dd")
            if (firstSessionSet.isPresent) {
                val format = formatter.format(firstSessionSet.get().value[0].repetitions[0].timestamp)
                (view!!.findViewById<View>(android.R.id.text1) as TextView).text = format
                val sessionTotalSets = objects[position].values.stream().mapToLong { obj: List<ExerciseSet> -> obj.size.toLong() }.sum()
                (view.findViewById<View>(android.R.id.text2) as TextView).text = "Sets done: $sessionTotalSets"
            } else {
                (view!!.findViewById<View>(android.R.id.text1) as TextView).text = "No session was found :/"
                (view.findViewById<View>(android.R.id.text2) as TextView).text = "Work out a bit and come see there results!"
            }
            return view
        }
    }

    private fun fillQualityProgressChart(quality: List<Map<String?, MutableList<ExerciseSet>>>) {
        val chart = findViewById<View>(R.id.chart) as LineChart
        val entries: MutableList<Entry> = ArrayList()
        for (i in quality.indices) {
            entries.add(Entry(i.toFloat(), calculateAvgSessionQuality(quality[i])))
        }
        val dataSet = LineDataSet(entries, "") // add entries to dataset
        dataSet.color = Color.RED
        dataSet.fillColor = Color.RED
        dataSet.lineWidth = 1f
        dataSet.valueTextColor = 2 // styling, ...
        val lineData = LineData(dataSet)
        val legend = chart.legend
        legend.isEnabled = false
        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 10f
        val description = Description()
        description.text = "Average quality for each session"
        chart.description = description
        chart.data = lineData
        chart.animateY(1500)
        // chart.invalidate(); // refresh
    }

    private fun calculateAvgSessionQuality(session: Map<String?, MutableList<ExerciseSet>>): Float {
        return session.values.stream().flatMap { obj: List<ExerciseSet> -> obj.stream() }.mapToDouble { exerciseSet: ExerciseSet -> exerciseSet.repetitions.stream().mapToDouble(ToDoubleFunction { rep: Repetition -> rep.quality!!.quality.toDouble() }).average().orElse(Double.NaN) }.average().orElse(Double.NaN).toFloat()
    }

    companion object {
        private const val TAG = "FitLog"
        fun isSameDayUsingInstant(date1: Date, date2: Date): Boolean {
            val instant1 = date1.toInstant()
                    .truncatedTo(ChronoUnit.DAYS)
            val instant2 = date2.toInstant()
                    .truncatedTo(ChronoUnit.DAYS)
            return instant1 == instant2
        }

        fun isNoLaterThan15s(later: Date, earlier: Date): Boolean {
            return later.time - earlier.time < 15 * 1000
        }
    }
}