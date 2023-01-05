package com.nextgentrainer.kotlin.ui.fitlog.repetition

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nextgentrainer.R
import com.nextgentrainer.databinding.LayoutRepetitionsListItemBinding
import com.nextgentrainer.kotlin.data.model.Repetition
import kotlin.math.roundToInt

class RepetitionsListAdapter(val viewModel: RepetitionsViewModel) :
    ListAdapter<Repetition, RepetitionsListAdapter.RepetitionsViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepetitionsViewHolder {
        val binding = LayoutRepetitionsListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return RepetitionsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RepetitionsViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)

        holder.itemView.setOnClickListener {
            viewModel.selectRepetition(currentItem)
        }
    }

    class RepetitionsViewHolder(private val binding: LayoutRepetitionsListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(repetition: Repetition) {
            val quality = repetition.quality?.quality!!.roundToInt()

            binding.apply {
                bestRepTextView.visibility = if (repetition.isBest) {
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }

                exerciseNameTextView.text = repetition.repetitionCounter?.numRepeats.toString()
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

                val baseUrl = "https://storage.googleapis.com/nextgentrainer-c380e.appspot.com/"

                Glide.with(mainLinearLayoutWorkouts)
                    .load(
                        baseUrl + repetition.quality.movementId + ".webp"
                    )
                    .placeholder(R.drawable.image_not_supported)
                    .into(repetitionGifView)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Repetition>() {
        override fun areItemsTheSame(oldItem: Repetition, newItem: Repetition) =
            oldItem.repetitionId == newItem.repetitionId

        override fun areContentsTheSame(oldItem: Repetition, newItem: Repetition) =
            oldItem == newItem
    }
}
