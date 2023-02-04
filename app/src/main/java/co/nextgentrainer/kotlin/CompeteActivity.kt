package co.nextgentrainer.kotlin

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import co.nextgentrainer.CameraXViewModel
import co.nextgentrainer.GraphicOverlay
import co.nextgentrainer.R
import co.nextgentrainer.kotlin.ui.compete.CompeteViewModel
import co.nextgentrainer.kotlin.utils.Constants
import co.nextgentrainer.preference.PreferenceUtils
import com.google.android.gms.common.annotation.KeepName
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.mlkit.common.MlKitException
import dagger.hilt.android.AndroidEntryPoint

@KeepName
@AndroidEntryPoint
class CompeteActivity :
    AppCompatActivity() {

    private var previewView: PreviewView? = null
    private var graphicOverlay: GraphicOverlay? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null
    private var needUpdateGraphicOverlayImageSourceInfo = false
    private var lensFacing = CameraSelector.LENS_FACING_FRONT
    private var cameraSelector: CameraSelector? = null
    private lateinit var challengeRuleTextView: TextView
    private lateinit var challengeTimer: CountDownTimer
    private lateinit var countdownTextView: TextView
    private lateinit var againstTextView: TextView
    private lateinit var timer: CountDownTimer
    private val viewModel: CompeteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compete)
        Log.d(TAG, "onCreate")

        countdownTextView = findViewById(R.id.challengeCounterTextView)

        againstTextView = findViewById(R.id.textViewAgainst)
        againstTextView.text = getString(R.string.waiting)


        challengeRuleTextView = findViewById(R.id.challengeTextView)
        challengeTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                challengeRuleTextView.text = (millisUntilFinished.div(1000).plus(1)).toString()
            }

            override fun onFinish() {
                viewModel.updateSessionHasEnded()
                challengeRuleTextView.gravity = Gravity.CENTER
                viewModel.turnOffProcessing()
            }
        }


        if (savedInstanceState != null) {
            viewModel.selectedModel = savedInstanceState.getString(
                Constants.STATE_SELECTED_MODEL,
                Constants.REP_COUNTER
            )
        }
        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        graphicOverlay = findViewById(R.id.challengeGraphicOverlay)

        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[CameraXViewModel::class.java]
            .processCameraProvider
            .observe(
                this
            ) { provider: ProcessCameraProvider? ->
                cameraProvider = provider
                bindAllCameraUseCases()
            }

        timer = object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countdownTextView.text = (millisUntilFinished.div(1000).plus(1)).toString()
            }

            override fun onFinish() {
                viewModel.turnOnProcessing()
                challengeTimer.start()
                challengeRuleTextView.gravity = Gravity.END
                challengeRuleTextView.textSize = 90f
            }
        }

        val startButton = findViewById<FloatingActionButton>(R.id.challenge_start)
        startButton.setOnClickListener {
            viewModel.startSession()
        }

        viewModel.competeViewState.observe(this) {
            startButton.visibility = it.startButtonVisibility
            countdownTextView.visibility = it.countdownTextViewVisibility
            countdownTextView.text = it.countDownTextVewText
            challengeRuleTextView.visibility = it.challengeRuleTextViewVisibility
            challengeRuleTextView.text = it.challengeRuleTextViewText
            challengeRuleTextView.textSize = it.challengeRuleTextViewTextSize

            if (it.startTimer) {
                timer.start()
                viewModel.timerStarted(it)
            }
        }
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
            cameraProvider!!.unbindAll()
            bindPreviewUseCase()
            bindAnalysisUseCase()
        }
    }

    private fun bindPreviewUseCase() {
        if (previewView == null) {
            return
        }
        if (!PreferenceUtils.isCameraLiveViewportEnabled(this)) {
            return
        }
        if (cameraProvider == null) {
            return
        }
        cameraProvider!!.unbind(previewUseCase)
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
        cameraProvider!!.bindToLifecycle( /* lifecycleOwner = */this, cameraSelector!!, analysisUseCase)
    }


    companion object {
        private const val TAG = "CompeteActivity"
    }
}
