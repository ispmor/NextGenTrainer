package com.nextgentrainer.kotlin.ui.fitlog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.nextgentrainer.R
import com.nextgentrainer.databinding.FragmentWorkoutsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FitLogFragment : Fragment(R.layout.fragment_workouts) {
    private val viewModel: FitLogViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentWorkoutsBinding.bind(view)

        val fitLogListAdapter = FitLogListAdapter()

        binding.apply {
            fitLogCustomListView.apply {
                adapter = fitLogListAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        viewModel.uiState.observe(
            viewLifecycleOwner
        ) {
            fitLogListAdapter.submitList(it.workoutsItems)
        }

        viewModel.fetchWorkouts()
    }
}
