package co.nextgentrainer.kotlin

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraInfoUnavailableException
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import co.nextgentrainer.CameraXViewModel
import co.nextgentrainer.GraphicOverlay
import co.nextgentrainer.R
import co.nextgentrainer.kotlin.ui.camera.CameraViewModel
import co.nextgentrainer.kotlin.utils.Constants.RECORD
import co.nextgentrainer.kotlin.utils.Constants.REP_COUNTER
import co.nextgentrainer.kotlin.utils.Constants.SQUATS_TRAINER
import co.nextgentrainer.kotlin.utils.Constants.STATE_SELECTED_MODEL
import co.nextgentrainer.preference.PreferenceUtils
import com.google.android.gms.common.annotation.KeepName
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.common.MlKitException
import dagger.hilt.android.AndroidEntryPoint

@KeepName
@AndroidEntryPoint
class CameraActivity :
    AppCompatActivity(),
    OnItemSelectedListener,
    CompoundButton.OnCheckedChangeListener {
    private var previewView: PreviewView? = null
    private var graphicOverlay: GraphicOverlay? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null
    private var needUpdateGraphicOverlayImageSourceInfo = false
    private var lensFacing = CameraSelector.LENS_FACING_FRONT
    private var cameraSelector: CameraSelector? = null
    private val viewModel: CameraViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")

        if (savedInstanceState != null) {
            viewModel.selectedModel = savedInstanceState.getString(STATE_SELECTED_MODEL, REP_COUNTER)
        }

        viewModel.initWorkouts()

        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        setContentView(R.layout.activity_camera_preview)

        previewView = findViewById(R.id.preview_view)
        if (previewView == null) {
            Log.d(TAG, "previewView is null")
        }
        graphicOverlay = findViewById(R.id.graphic_overlay)
        if (graphicOverlay == null) {
            Log.d(TAG, "graphicOverlay is null")
        }
        val spinner = findViewById<Spinner>(R.id.spinner)
        val options: MutableList<String> = ArrayList()
        options.add(SQUATS_TRAINER)
        options.add(RECORD)

        val dataAdapter = ArrayAdapter(this, R.layout.spinner_style, options)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = dataAdapter
        spinner.onItemSelectedListener = this
        val facingSwitch = findViewById<ToggleButton>(R.id.facing_switch)
        facingSwitch.setOnCheckedChangeListener(this)

        ViewModelProvider(this, AndroidViewModelFactory.getInstance(application))[CameraXViewModel::class.java]
            .processCameraProvider
            .observe(
                this
            ) { provider: ProcessCameraProvider? ->
                cameraProvider = provider
                bindAllCameraUseCases()
            }

        val countdownTextView = findViewById<TextView>(R.id.counterTextView)

        val timer = object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countdownTextView.text = (millisUntilFinished.div(1000).plus(1)).toString()
            }

            override fun onFinish() {
                viewModel.startMovementProcessing()
            }
        }

        val startButton = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        startButton.setOnClickListener {
            viewModel.startExerciseCountDown()
        }

        val saveButton = findViewById<Button>(R.id.save_button)
        saveButton.setOnClickListener {
            viewModel.resetToDefaultState()
            val builder = AlertDialog.Builder(this)
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT

            builder.setTitle("Exercise Name")
            builder.setView(input)

            builder.setPositiveButton("OK") { dialog, _ ->
                run {
                    viewModel.saveExerciseSet()
                    viewModel.saveRecording(input.text.toString())
                    dialog.dismiss()
                    Snackbar.make(it, "Successfully saved repetition", Snackbar.LENGTH_LONG)
                        .setAction("CLOSE", {})
                        .show()
                }
            }
            builder.setNegativeButton(
                "Cancel"
            ) { dialog, _ ->
                run {
                    dialog.cancel()
                    viewModel.resetToDefaultState()
                    Snackbar.make(it, "Did not save the movement", Snackbar.LENGTH_LONG)
                        .setAction("CLOSE", {})
                        .show()
                }
            }

            builder.show()
        }

        viewModel.cameraViewState.observe(
            this
        ) {
            countdownTextView.visibility = it.countDownTextVisibility
            startButton.visibility = it.startButtonVisibility
            if (it.startTimer) {
                timer.start()
            }
        }
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)
        bundle.putString(STATE_SELECTED_MODEL, viewModel.selectedModel)
    }

    @Synchronized
    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        viewModel.selectedModel = parent.getItemAtPosition(pos).toString()
        Log.d(TAG, "Selected model: ${viewModel.selectedModel}")
        bindAnalysisUseCase()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Do nothing.
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        if (cameraProvider == null) {
            return
        }
        val newLensFacing =
            if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
                CameraSelector.LENS_FACING_BACK
            } else CameraSelector.LENS_FACING_FRONT
        val newCameraSelector = CameraSelector.Builder().requireLensFacing(newLensFacing).build()
        try {
            if (cameraProvider!!.hasCamera(newCameraSelector)) {
                Log.d(TAG, "Set facing to $newLensFacing")
                lensFacing = newLensFacing
                cameraSelector = newCameraSelector
                bindAllCameraUseCases()
                return
            }
        } catch (e: CameraInfoUnavailableException) {
            // Falls through
        }
        Toast.makeText(
            applicationContext,
            "This device does not have lens with facing: $newLensFacing",
            Toast.LENGTH_SHORT
        )
            .show()
    }

    public override fun onResume() {
        super.onResume()
        bindAllCameraUseCases()
    }

    override fun onPause() {
        super.onPause()
        viewModel.stop()
    }

    public override fun onDestroy() {
        super.onDestroy()
        viewModel.stop()
    }

    private fun bindAllCameraUseCases() {
        if (cameraProvider != null) {
            // As required by CameraX API, unbinds all use cases before trying to
            // re-bind any of them.
            cameraProvider!!.unbindAll()
            bindPreviewUseCase()
            bindAnalysisUseCase()
        }
    }

    private fun bindPreviewUseCase() {
        if (!PreferenceUtils.isCameraLiveViewportEnabled(this)) {
            return
        }
        if (cameraProvider == null) {
            return
        }
        if (previewUseCase != null) {
            cameraProvider!!.unbind(previewUseCase)
        }
        val builder = Preview.Builder()
        val targetResolution = PreferenceUtils.getCameraXTargetResolution(this, lensFacing)
        if (targetResolution != null) {
            builder.setTargetResolution(targetResolution)
        }
        previewUseCase = builder.build()
        previewUseCase!!.setSurfaceProvider(previewView!!.surfaceProvider)
        cameraProvider!!.bindToLifecycle(
            /* lifecycleOwner = */this,
            cameraSelector!!,
            previewUseCase
        )
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun bindAnalysisUseCase() {
        if (cameraProvider == null) {
            return
        }
        if (analysisUseCase != null) {
            cameraProvider!!.unbind(analysisUseCase)
        }
        viewModel.stop()
        viewModel.getImageProcessor()

        val builder = ImageAnalysis.Builder()
        val targetResolution = PreferenceUtils.getCameraXTargetResolution(this, lensFacing)
        if (targetResolution != null) {
            builder.setTargetResolution(targetResolution)
        }
        analysisUseCase = builder.build()
        needUpdateGraphicOverlayImageSourceInfo = true
        analysisUseCase!!.setAnalyzer(
            ContextCompat.getMainExecutor(this)
        ) { imageProxy: ImageProxy ->
            if (needUpdateGraphicOverlayImageSourceInfo) {
                val isImageFlipped = lensFacing == CameraSelector.LENS_FACING_FRONT
                val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                if (rotationDegrees == 0 || rotationDegrees == 180) {
                    graphicOverlay!!.setImageSourceInfo(
                        imageProxy.width,
                        imageProxy.height,
                        isImageFlipped
                    )
                } else {
                    graphicOverlay!!.setImageSourceInfo(
                        imageProxy.height,
                        imageProxy.width,
                        isImageFlipped
                    )
                }
                needUpdateGraphicOverlayImageSourceInfo = false
            }
            try {
                viewModel.processImageProxy(imageProxy, graphicOverlay!!)
            } catch (e: MlKitException) {
                Log.e(TAG, "Failed to process image. Error: " + e.localizedMessage)
                Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT)
                    .show()
            }
        }
        cameraProvider!!.bindToLifecycle(
            /* lifecycleOwner = */this,
            cameraSelector!!,
            analysisUseCase
        )
    }

    companion object {
        private const val TAG = "CameraActivity"
    }
}
