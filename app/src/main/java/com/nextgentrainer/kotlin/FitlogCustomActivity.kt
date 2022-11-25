package com.nextgentrainer.kotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.nextgentrainer.R
import com.nextgentrainer.databinding.ActivityFitlogCustomBinding
import com.nextgentrainer.kotlin.data.repository.WorkoutRepository
import com.nextgentrainer.kotlin.data.source.WorkoutSource
import com.nextgentrainer.kotlin.ui.fitlog.FitLogViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FitlogCustomActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fitlog_custom)
    }
}
