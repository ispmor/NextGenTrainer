package com.nextgentrainer.java

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.annotation.KeepName
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.mlkit.common.MlKitException
import com.nextgentrainer.CameraXViewModel
import com.nextgentrainer.GraphicOverlay
import com.nextgentrainer.R
import com.nextgentrainer.java.data.CompetitionSession
import com.nextgentrainer.java.posedetector.ExerciseProcessor
import com.nextgentrainer.java.utils.CameraActivityHelper
import com.nextgentrainer.java.utils.Constants
import com.nextgentrainer.preference.PreferenceUtils
import java.util.Date

@KeepName
class CompeteActivity :
    AppCompatActivity() {

    private var previewView: PreviewView? = null
    private var graphicOverlay: GraphicOverlay? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null
    private var needUpdateGraphicOverlayImageSourceInfo = false
    private var selectedModel = Constants.SQUATS_TRAINER
    private var lensFacing = CameraSelector.LENS_FACING_FRONT
    private var cameraSelector: CameraSelector? = null
    private lateinit var imageProcessor: ExerciseProcessor
    private lateinit var database: DatabaseReference
    private var session: CompetitionSession? = null
    private var key: String? = null
    private var whoAmI: String? = null
    private lateinit var challengeRuleTextView: TextView
    private lateinit var challengeTimer: CountDownTimer
    private lateinit var countdownTextView: TextView
    private lateinit var againstTextView: TextView
    private lateinit var timer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compete)
        Log.d(TAG, "onCreate")

        countdownTextView = findViewById<TextView>(R.id.challengeCounterTextView)

        againstTextView = findViewById<TextView>(R.id.textViewAgainst)
        againstTextView.text = getString(R.string.waiting)

        challengeRuleTextView = findViewById<TextView>(R.id.challengeTextView)
        challengeTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                challengeRuleTextView.text = (millisUntilFinished.div(1000).plus(1)).toString()
            }

            override fun onFinish() {
                updateSessionHasEnded()
                // updateUIBaseOnSession()
                imageProcessor.isStarted = false
            }
        }

        database = Firebase.database(getString(R.string.database_url)).getReference("CompetitionSession")

        imageProcessor = CameraActivityHelper.selectModel(selectedModel, this)
        if (savedInstanceState != null) {
            selectedModel = savedInstanceState.getString(
                Constants.STATE_SELECTED_MODEL,
                Constants.REP_COUNTER
            )
        }
        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        graphicOverlay = findViewById(R.id.challengeGraphicOverlay)

        ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))
            .get(CameraXViewModel::class.java)
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
                countdownTextView.visibility = View.INVISIBLE
                imageProcessor.isStarted = true
                challengeTimer.start()
            }
        }

        val startButton = findViewById<FloatingActionButton>(R.id.challenge_start)
        startButton.setOnClickListener {
            if (session == null) {
                database.orderByChild("finished").equalTo(false).limitToFirst(1).get().addOnSuccessListener {
                    val value = it.getValue<HashMap<String, Any>>()

                    if (key.isNullOrEmpty() && value != null) {
                        key = value.keys.first()
                        val tmpSession = it.child(key!!).getValue<CompetitionSession>()
                        tmpSession!!.user2 = "test-2USER"
                        whoAmI = "test-2USER"

                        updateSession(tmpSession)
                        Log.d(TAG, "New key is: $key")
                        Log.d(TAG, "Value is: $session")
                    } else if (key.isNullOrEmpty() && value == null) {
                        whoAmI = "Test-USER1"
                        key = createNewSession("squats", "Test-USER1")
                    }
                    bindSessionToKey(key!!)
                }.addOnFailureListener {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", it)
                }
            }
            startButton.visibility = View.INVISIBLE
            countdownTextView.visibility = View.VISIBLE
        }
    }

    public override fun onResume() {
        super.onResume()
        bindAllCameraUseCases()
    }

    override fun onPause() {
        super.onPause()
        imageProcessor.stop()
    }

    public override fun onDestroy() {
        super.onDestroy()
        imageProcessor.stop()
    }

    private fun bindAllCameraUseCases() {
        if (cameraProvider != null) {
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
        imageProcessor.stop()

        imageProcessor = CameraActivityHelper.selectModel(selectedModel, this)

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
                imageProcessor.processImageProxy(imageProxy, graphicOverlay!!)
            } catch (e: MlKitException) {
                Log.e(TAG, "Failed to process image. Error: " + e.localizedMessage)
                Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT)
                    .show()
            }
        }
        cameraProvider!!.bindToLifecycle( /* lifecycleOwner = */this, cameraSelector!!, analysisUseCase)
    }

    fun createNewSession(exercise: String?, user1: String?): String {
        val keyTmp = database.push().key
        if (keyTmp == null) {
            Log.w(TAG, "Couldn't get push key for competitionsession")
            return ""
        }
        session = CompetitionSession(keyTmp, exercise, user1, startDateMillis = Date().time)
        database.child(keyTmp).setValue(session)

        bindSessionToKey(keyTmp)
        return keyTmp
    }

    fun bindSessionToKey(bindingKey: String) {
        database.child(bindingKey).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val sessionTmp = dataSnapshot.getValue<CompetitionSession>()
                if (sessionTmp != null) {
                    if (
                        !sessionTmp.user1.isNullOrEmpty() &&
                        !sessionTmp.user2.isNullOrEmpty() &&
                        sessionTmp.endDateMillis == null
                    ) {
                        countdownTextView.visibility = View.VISIBLE
                        timer.start()
                        // challengeTimer.start()
                        againstTextView.text = "GO!!!!!"
                    }

                    session = sessionTmp

                    if (sessionTmp.finished) {
                        updateFinished()
                    }
                    Log.d(TAG, "Value is: $session")
                }
                Log.d(TAG, "Empty TMP session")
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    fun updateSession(
        session: CompetitionSession
    ) {
        database.child(session.uid!!).setValue(session)
    }

    fun updateSessionHasEnded() {
        if (session?.endDateMillis == null) {
            session?.endDateMillis = Date().time
            updateReps()
            database.child(key!!).setValue(session)
        } else {
            session?.finished = true
            updateReps()
            database.child(key!!).setValue(session)
        }
    }

    fun updateReps() {
        if (whoAmI == session!!.user1) {
            session!!.reps1 = getReps()
        } else {
            session!!.reps2 = getReps()
        }
    }

    fun getReps(): Int {
        return imageProcessor.lastQualifiedRepetition!!.repetitionCounter!!.numRepeats
    }

//    fun updateUIBaseOnSession() {
//        var iWon: Boolean
//        iWon = if (whoAmI == session!!.user1) {
//            session!!.reps1!! > session!!.reps2!!
//        } else {
//            session!!.reps1!! < session!!.reps2!!
//        }
//
//        if (iWon) {
//            againstTextView.text = "YOU WON!"
//        } else {
//            againstTextView.text = "YOU LOST"
//        }
//    }

    fun updateFinished() {
        var iWon: Boolean
        iWon = if (whoAmI == session!!.user1) {
            session!!.reps1!! > session!!.reps2!!
        } else {
            session!!.reps1!! < session!!.reps2!!
        }

        if (iWon) {
            againstTextView.text = "YOU WON!"
        } else {
            againstTextView.text = "YOU LOST"
        }
    }

    companion object {
        private const val TAG = "CompeteActivity"
    }
}
