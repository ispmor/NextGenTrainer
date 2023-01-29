package co.nextgentrainer.kotlin.ui.fitlog.sets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import co.nextgentrainer.databinding.LayoutSetsListItemBinding
import co.nextgentrainer.kotlin.data.model.ExerciseSet
import kotlin.math.roundToInt

class SetsListAdapter(val viewModel: SetsViewModel) :
    ListAdapter<ExerciseSet, SetsListAdapter.SetsViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetsViewHolder {
        val binding = LayoutSetsListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return SetsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SetsViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
        holder.itemView.setOnClickListener {
            viewModel.selectSet(currentItem)
        }
    }

    class SetsViewHolder(private val binding: LayoutSetsListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(set: ExerciseSet) {
            val qualityList = set.repetitions.map {
                it.quality!!.quality
            }
            val quality = qualityList.average().roundToInt()
            val repsSum = set.repetitions.size

            binding.apply {
                bestSetTextView.visibility = if (set.isBest) {
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }
                repsSumTextView.text = repsSum.toString()
                exerciseNameTextView.text = set.exerciseName
                    .split("_")[0]
                    .uppercase()
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

    class DiffCallback : DiffUtil.ItemCallback<ExerciseSet>() {
        override fun areItemsTheSame(oldItem: ExerciseSet, newItem: ExerciseSet) = oldItem
            .repetitions[0]
            .repetitionId == newItem.repetitions[0].repetitionId

        override fun areContentsTheSame(oldItem: ExerciseSet, newItem: ExerciseSet) = oldItem == newItem
    }
}
