package com.nextgentrainer.kotlin.ui.fitlog.repetitionAnalysis

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.nextgentrainer.databinding.LayoutAnalysisListItemBinding
import com.nextgentrainer.kotlin.data.model.ChartData

class RepetitionAnalysisListAdapter(val viewModel: RepetitionAnalysisViewModel) :
    ListAdapter<ChartData, RepetitionAnalysisListAdapter.RepetitionAnalysisViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RepetitionAnalysisViewHolder {
        val binding = LayoutAnalysisListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return RepetitionAnalysisViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RepetitionAnalysisViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    class RepetitionAnalysisViewHolder(private val binding: LayoutAnalysisListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ChartData) {
            binding.apply {
                val dataSet = LineDataSet(data.entries, "") // add entries to dataset
                dataSet.color = Color.RED
                dataSet.fillColor = Color.RED
                dataSet.lineWidth = LINE_WIDTH
                dataSet.valueTextColor = Color.WHITE

                val lineData = LineData(dataSet)
                val legend = chart.legend
                legend.isEnabled = false

                chart.axisLeft.textColor = Color.WHITE
                chart.axisRight.isEnabled = false

                val xAxis = chart.xAxis
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.textSize = TEXT_SIZE_10F
                xAxis.textColor = Color.WHITE
                xAxis.valueFormatter = XAxisFormatter()
                chart.data = lineData
                chart.animateY(MILLIS_1500)

                chart.description.isEnabled = false

                chartNameTextView.text = data.chartName
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ChartData>() {
        override fun areItemsTheSame(oldItem: ChartData, newItem: ChartData) =
            oldItem.chartId == newItem.chartId

        override fun areContentsTheSame(oldItem: ChartData, newItem: ChartData) =
            oldItem.entries == newItem.entries
    }

    class XAxisFormatter : IAxisValueFormatter {
        override fun getFormattedValue(value: Float, axis: AxisBase?): String {
            return "${value}s"
        }
    }

    companion object {
        private const val MILLIS_1500 = 1500
        private const val TEXT_SIZE_10F = 10.0f
        private const val LINE_WIDTH = 1f
    }
}
