package com.nextgentrainer.kotlin.ui.fitlog.sets

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nextgentrainer.R
import com.nextgentrainer.databinding.FragmentSetsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetsFragment : Fragment(R.layout.fragment_sets) {
    private val viewModel: SetsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentSetsBinding.bind(view)

        val setsListAdapter = SetsListAdapter(viewModel)

        binding.apply {
            workoutSetsListView.apply {
                adapter = setsListAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        viewModel.uiState.observe(
            viewLifecycleOwner
        ) {
            if (it.sets.isNotEmpty()) {
                setsListAdapter.submitList(it.sets)
            }

            if (it.userSelectedSet) {
                val action =
                    SetsFragmentDirections.actionSetsFragmentToRepetitionsFragment()
                view.findNavController().navigate(action)
            }
        }

        viewModel.updateSetsListFromRepo()
    }
}
