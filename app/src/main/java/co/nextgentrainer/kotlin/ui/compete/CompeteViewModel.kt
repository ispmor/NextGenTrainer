package co.nextgentrainer.kotlin.ui.compete

import android.app.Application
import android.util.Log
import android.view.View
import androidx.camera.core.ImageProxy
import androidx.lifecycle.*
import co.nextgentrainer.GraphicOverlay
import co.nextgentrainer.kotlin.data.model.CompeteSession
import co.nextgentrainer.kotlin.data.repository.*
import co.nextgentrainer.kotlin.utils.CameraActivityHelper.selectModel
import co.nextgentrainer.kotlin.utils.Constants
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

@HiltViewModel
class CompeteViewModel @Inject constructor(
    val repetitionRepository: RepetitionRepository,
    val workoutRepository: WorkoutRepository,
    val movementRepository: MovementRepository,
    val competeSessionRepository: CompeteSessionRepository,
    val application: Application
) : ViewModel(), DefaultLifecycleObserver {
    private val _competeViewState = MutableLiveData<CompeteState>()
    val competeViewState: LiveData<CompeteState> = _competeViewState
    var selectedModel = Constants.SQUATS_TRAINER
    private var session: CompeteSession? = null
    private var key: String? = null
    private var notStartedYet = true


    var imageProcessor = selectModel(
        selectedModel,
        context = application.applicationContext,
        movementRepository,
        repetitionRepository,
        workoutRepository
    )

    fun startSession(){
        if (session == null) {
            competeSessionRepository.getCompeteSessionReference().addOnSuccessListener {
                val value = it.getValue<HashMap<String, CompeteSession>>()

                if (key.isNullOrEmpty() && value != null) {
                    key = value.keys.toList()[0]
                    val tmpSession = value[key]!!
                    tmpSession.user2 = Firebase.auth.currentUser!!.displayName


                    competeSessionRepository.updateSession(tmpSession)
                    Log.d(TAG, "New key is: $key")
                    Log.d(TAG, "Value is: $session")
                } else if (key.isNullOrEmpty() && value == null) {
                    key = competeSessionRepository.createNewSession("squats")
                }
                bindSessionToKey(key!!)
            }.addOnFailureListener {
                Log.w(TAG, "Failed to read value.", it)
            }
        }
        Log.d("STATE:-------", CompeteState(
            startButtonVisibility = View.INVISIBLE,
            countdownTextViewVisibility = View.VISIBLE,
            challengeRuleTextViewVisibility = View.VISIBLE
        ).toString())
        _competeViewState.value = CompeteState(
            startButtonVisibility = View.INVISIBLE,
            countdownTextViewVisibility = View.VISIBLE,
            challengeRuleTextViewVisibility = View.VISIBLE
        )
    }

    fun stop() {
        imageProcessor.stop()
    }

    @androidx.camera.core.ExperimentalGetImage
    fun processImageProxy(imageProxy: ImageProxy, graphicOverlay: GraphicOverlay){
        imageProcessor.processImageProxy(imageProxy, graphicOverlay)
    }

    fun getImageProcessor() {
        imageProcessor = selectModel(
            selectedModel,
            application.applicationContext,
            movementRepository,
            repetitionRepository,
            workoutRepository
        )
    }

    fun turnOffProcessing() {
        imageProcessor.setIsProcessing(false)
    }

    fun turnOnProcessing() {
        imageProcessor.setIsProcessing(true)
        _competeViewState.value = CompeteState(
            countdownTextViewVisibility = View.INVISIBLE,
            challengeRuleTextViewVisibility = View.VISIBLE,
            startButtonVisibility = View.INVISIBLE,
        )
    }


    private fun bindSessionToKey(bindingKey: String) {
        competeSessionRepository.getSessionFromKey(bindingKey).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val sessionTmp = dataSnapshot.getValue<CompeteSession>()
                if (sessionTmp != null) {
                    if (
                        bothUsersExist(sessionTmp) &&
                        sessionTmp.endDateMillis == null &&
                        notStartedYet
                    ) {
                        notStartedYet = false
                        _competeViewState.value = CompeteState(
                            startButtonVisibility = View.INVISIBLE,
                            countdownTextViewVisibility = View.VISIBLE,
                            startTimer = true,
                            againstTextViewVisibility = View.INVISIBLE,

                        )
                    }
                    session = sessionTmp

                    if (sessionTmp.user1_finished == true && sessionTmp.user2_finished == true) {
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

    //TODO(PROBLEMEM JEST WYŚCIG! KONIECZNIE TRZEBA WYKMINIĆ JAK SYNCHRONIZOWAĆ STAN ZAKOŃCZONEJ SESJI)
    fun updateSession(tmpSession: CompeteSession) {
        if (tmpSession.user1_finished != null && session?.user1_finished != tmpSession.user1_finished ) {
            session?.user1_finished = tmpSession.user1_finished
        }

        if (tmpSession.user2_finished != null && tmpSession.user2_finished !=  session?.user2_finished) {
            session?.user2_finished = tmpSession.user2_finished
        }

        if (tmpSession.user1 != null && session?.user1 != tmpSession.user1) {
            session?.user1 = tmpSession.user1
        }

        if (tmpSession.user2 != null && session?.user2 != tmpSession.user2 ) {
            session?.user2 = tmpSession.user2
        }
    }

    private fun bothUsersExist(tmpSession: CompeteSession): Boolean {
        return !tmpSession.user1.isNullOrEmpty() && !tmpSession.user2.isNullOrEmpty()
    }

    fun updateSessionHasEnded() {
        updateReps()

        if (session?.endDateMillis == null) {
            session?.endDateMillis = Date().time
            competeSessionRepository.setEndDateMillis(key!!, Date().time)
        } else {
            session?.finished = true
            competeSessionRepository.setFinished(key!!, true)
        }

        competeSessionRepository.setUserFinished(key!!, whichUserAmI())
    }

    private fun updateReps() {
        if (Firebase.auth.currentUser!!.displayName == session!!.user1) {
            session!!.reps1 = getReps()
            competeSessionRepository.saveRepsForSessionAndUser(key!!, "reps1", getReps())
        } else {
            session!!.reps2 = getReps()
            competeSessionRepository.saveRepsForSessionAndUser(key!!, "reps2", getReps())
        }
    }

    private fun getReps(): Int {
        if (imageProcessor.lastQualifiedRepetition == null) {
            return 0
        }
        return imageProcessor.lastQualifiedRepetition!!.repetitionCounter!!.numRepeats
    }

    private fun whichUserAmI(): String {
        val whoAmI = Firebase.auth.currentUser!!.displayName!!
        return if (whoAmI == session!!.user1) {
            "user1"
        } else {
            "user2"
        }
    }
    private fun getOpponentReps(): Int {
        val whoAmI = Firebase.auth.currentUser!!.displayName!!
        return if (whoAmI != session!!.user1) {
            session!!.reps1
        } else {
            session!!.reps2
        }
    }

    private fun getOpponentName(): String {
        val whoAmI = Firebase.auth.currentUser!!.displayName!!
        return if (whoAmI != session!!.user1) {
            session!!.user1!!
        } else {
            session!!.user2!!
        }
    }

    fun updateFinished() {
        // TODO("Trzeba dodać do uistate i na tej podstawie w observerze to zmieniać")
        val challengeRuleTextViewTextSize = 34f
        val whoAmI = Firebase.auth.currentUser!!.displayName!!

        val myReps = if (whoAmI == session!!.user1) {
            session!!.reps1
        } else {
            session!!.reps2
        }

        val challengeRuleTextViewText = "You did: $myReps reps.\n${getOpponentName()}: ${getOpponentReps()} reps."
        var againstTextViewText = ""
        if (session!!.reps1 == session!!.reps2) {
            againstTextViewText = "TIE"

        } else {

            val iWon = if (whoAmI == session!!.user1) {
                session!!.reps1 > session!!.reps2
            } else {
                session!!.reps1 < session!!.reps2
            }

            if (iWon) {
                againstTextViewText = "WIN!"
            } else {
                againstTextViewText = "LOST :("
            }
        }
        _competeViewState.value = CompeteState(
            againstTextViewVisibility = View.VISIBLE,
            againstTextViewText = againstTextViewText,
            challengeRuleTextViewText = challengeRuleTextViewText,
            challengeRuleTextViewTextSize = challengeRuleTextViewTextSize,
            challengeRuleTextViewVisibility = View.VISIBLE
        )
    }

    fun timerStarted(it: CompeteState) {

        _competeViewState.value = CompeteState(
            startTimer = false,
            againstTextViewVisibility = it.againstTextViewVisibility,
            againstTextViewText = it.againstTextViewText,
            challengeRuleTextViewText = it.challengeRuleTextViewText,
            challengeRuleTextViewTextSize = it.challengeRuleTextViewTextSize,
            challengeRuleTextViewVisibility = it.challengeRuleTextViewVisibility,
            countdownTextViewVisibility = it.countdownTextViewVisibility,
            countDownTextVewText = it.countDownTextVewText,
            exerciseSet = it.exerciseSet,
            setFinished = it.setFinished,
            userMessage = it.userMessage,
            startButtonVisibility = it.startButtonVisibility,
            imageProcessorIsStarted = it.imageProcessorIsStarted
        )
    }

    companion object {
        private const val TAG = "CompeteViewModel"
    }

}
