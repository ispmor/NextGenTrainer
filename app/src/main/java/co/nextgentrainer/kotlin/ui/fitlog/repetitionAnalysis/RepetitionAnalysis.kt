package co.nextgentrainer.kotlin.ui.fitlog.repetitionAnalysis

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import co.nextgentrainer.R
import co.nextgentrainer.databinding.FragmentRepetitionAnalysisBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RepetitionAnalysis : Fragment(R.layout.fragment_repetition_analysis) {
    private val viewModel: RepetitionAnalysisViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentRepetitionAnalysisBinding.bind(view)

        val repetitionAnalysisListAdapter = RepetitionAnalysisListAdapter(viewModel)

        binding.apply {
            analysisChartsListView.apply {
                adapter = repetitionAnalysisListAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        viewModel.uiState.observe(
            viewLifecycleOwner
        ) {
            if (it.charts.isNotEmpty()) {
                repetitionAnalysisListAdapter.submitList(it.charts)
            }
        }

        viewModel.updateCharts()
    }
}
