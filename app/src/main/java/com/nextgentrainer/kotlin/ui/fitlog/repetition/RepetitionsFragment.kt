package com.nextgentrainer.kotlin.ui.fitlog.repetition

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nextgentrainer.R
import com.nextgentrainer.databinding.FragmentRepetitionsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RepetitionsFragment : Fragment(R.layout.fragment_repetitions) {
    private val viewModel: RepetitionsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_repetitions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentRepetitionsBinding.bind(view)

        val repetitionsListAdapter = RepetitionsListAdapter(viewModel)

        binding.apply {
            workoutSetsListView.apply {
                adapter = repetitionsListAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        viewModel.uiState.observe(
            viewLifecycleOwner
        ) {
            if (it.repetitions.isNotEmpty()) {
                repetitionsListAdapter.submitList(it.repetitions)
            }

            if (it.userSelectedRepetition) {
                val action =
                    RepetitionsFragmentDirections.actionRepetitionsFragmentToRepetitionAnalysis()
                view.findNavController().navigate(action)
            }
        }

        viewModel.updateRepetitionsListFromRepo()
    }
}
