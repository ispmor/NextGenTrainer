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
class WorkoutsFragment : Fragment(R.layout.fragment_workouts) {
    private val viewModel: WorkoutsFragmentViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentWorkoutsBinding.bind(view)

        val workoutsListAdapter = WorkoutsListAdapter()

        binding.apply {
            fitLogCustomListView.apply {
                adapter = workoutsListAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        viewModel.uiState.observe(
            viewLifecycleOwner
        ) {
            workoutsListAdapter.submitList(it.workoutsItems)
        }

        viewModel.fetchWorkouts()
    }
}
