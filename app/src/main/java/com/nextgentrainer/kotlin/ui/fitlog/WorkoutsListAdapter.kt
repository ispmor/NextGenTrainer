package com.nextgentrainer.kotlin.ui.fitlog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nextgentrainer.databinding.LayoutFitlogListItemBinding
import com.nextgentrainer.kotlin.data.model.Workout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class WorkoutsListAdapter : ListAdapter<Workout, WorkoutsListAdapter.WorkoutViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val binding = LayoutFitlogListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return WorkoutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    class WorkoutViewHolder(private val binding: LayoutFitlogListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(workout: Workout) {
            val formatterDate = SimpleDateFormat("dd MMM", Locale.getDefault())
            val formatterDayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault())
            val dateString = formatterDate.format(Date(workout.timestampMillis))
            val dayOfWeekString = formatterDayOfWeek.format(Date(workout.timestampMillis))
            val qualityList = workout.sets.flatMap {
                it.repetitions.map {
                    it.quality!!.quality
                }
            }
            val quality = qualityList.average().roundToInt()
            val repsSum = workout.sets.flatMap { it.repetitions }.size
            val setsSum = workout.sets.size

            binding.apply {
                bestWorkoutTextView.visibility = if (workout.isBest) {
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }
                setsSumTextView.text = setsSum.toString()
                repsSumTextView.text = repsSum.toString()
                dayFitLogItem.text = dayOfWeekString
                dateFitLogItem.text = dateString
                val arrayOfQualityMarkersActive = listOf(
                    quality1TextView,
                    quality2TextView,
                    quality3TextView,
                    quality4TextView,
                    quality5TextView
                )

                val arrayOfQualityMarkersPassive = listOf(
                    quality1PassiveTextView,
                    quality2PassiveTextView,
                    quality3PassiveTextView,
                    quality4PassiveTextView,
                    quality5PassiveTextView
                )
                for (i in 0 until quality) {
                    arrayOfQualityMarkersActive[i].visibility = View.VISIBLE
                    arrayOfQualityMarkersPassive[i].visibility = View.INVISIBLE
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Workout>() {
        override fun areItemsTheSame(oldItem: Workout, newItem: Workout) = oldItem.workoutId == newItem.workoutId

        override fun areContentsTheSame(oldItem: Workout, newItem: Workout) = oldItem == newItem
    }
}
