package com.nextgentrainer.kotlin.ui.fitlog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nextgentrainer.R
import com.nextgentrainer.databinding.FragmentWorkoutsBinding
import com.nextgentrainer.kotlin.FitLogActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorkoutsFragment : Fragment(R.layout.fragment_workouts) {
    private val viewModel: WorkoutsFragmentViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentWorkoutsBinding.bind(view)
        val workoutsListAdapter = WorkoutsListAdapter(viewModel)

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
            if (it.workoutsItems.isNotEmpty()) {
                workoutsListAdapter.submitList(it.workoutsItems)
            }
            if (it.userSelectedWorkout) {
                val action = WorkoutsFragmentDirections.actionWorkoutToSets()
                view.findNavController().navigate(action)
            }
        }
        viewModel.fetchWorkouts()
    }


}
