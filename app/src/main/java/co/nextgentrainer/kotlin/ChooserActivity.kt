package co.nextgentrainer.kotlin

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import android.text.Html
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import co.nextgentrainer.BuildConfig
import co.nextgentrainer.FirebaseLoginActivity
import co.nextgentrainer.R
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class ChooserActivity : AppCompatActivity(), OnItemClickListener, View.OnClickListener {
    private lateinit var auth: FirebaseAuth
    private val storageProfilePicRef = FirebaseStorage.getInstance().reference.child("ProfilePic")

    var imgUri: MutableLiveData<Uri?> = MutableLiveData<Uri?>()
    lateinit var uploadedImageUri: Uri

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            imgUri.value = result.uriContent
        } else {
            val exception = result.error
            exception?.message?.let { Log.e(TAG, it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                ThreadPolicy.Builder().detectAll().penaltyLog().build()
            )
            StrictMode.setVmPolicy(
                VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build()
            )
        }
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        auth = Firebase.auth

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.activity_chooser)

        findViewById<ImageButton>(R.id.trainingButton).setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }

        findViewById<ImageView>(R.id.trainingButtonImageView).setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }

        findViewById<TextView>(R.id.trainingTextView).setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }

        findViewById<TextView>(R.id.alreadyTextView).setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }

        findViewById<TextView>(R.id.fitlogTextVieww).setOnClickListener {
            startActivity(Intent(this, FitlogCustomActivity::class.java))
        }

        findViewById<ImageButton>(R.id.fitLogButton).setOnClickListener {
            startActivity(Intent(this, FitlogCustomActivity::class.java))
        }

        findViewById<ImageButton>(R.id.competeButton).setOnClickListener {
            startActivity(Intent(this, CompeteActivity::class.java))
        }

        findViewById<TextView>(R.id.competeTextView).setOnClickListener {
            startActivity(Intent(this, CompeteActivity::class.java))
        }

        findViewById<ImageButton>(R.id.bellButton).setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val wv = WebView(this)
            wv.settings.javaScriptEnabled = true
            wv.loadUrl(getString(R.string.feedback_form))
            wv.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    view.loadUrl(url)
                    return true
                }
            }

            builder.setTitle("Your feedback:")

            builder.setView(wv)

            builder.setNegativeButton(
                "Cancel"
            ) { dialog, _ ->
                run {
                    dialog.cancel()
                    Snackbar.make(it, "You're the BEST! Thanks for helping.", Snackbar.LENGTH_LONG)
                        .setAction("CLOSE", {})
                        .show()
                }
            }

            builder.show()
        }

        val profilePictureView = findViewById<ImageView>(R.id.profileImageView)

        profilePictureView.setOnClickListener {
            startCrop()
            uploadProfileImage()
        }

        storageProfilePicRef.child(auth.currentUser!!.uid + ".jpg").downloadUrl.addOnSuccessListener {
            Glide.with(this)
                .load(it)
                .placeholder(R.drawable.default_profile_picture)
                .into(profilePictureView)
        }

        val gearButton = findViewById<ImageButton>(R.id.gearImageButton)
        registerForContextMenu(gearButton)
        gearButton.setOnClickListener {
            gearButton.showContextMenu()
        }
    }

    private fun startCrop() {
        cropImage.launch(
            CropImageContractOptions(
                null,
                CropImageOptions()
            )
        )
    }

    fun uploadProfileImage() {
        val profilePictureView = findViewById<ImageView>(R.id.profileImageView)
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Set your profile image")
        progressDialog.setMessage("Please wait, while we are setting your data")
        progressDialog.show()

        imgUri.observe(this) { uri ->
            if (uri != null) {
                val fileRef = storageProfilePicRef.child(auth.currentUser!!.uid + ".jpg")

                val uploadTask = fileRef.putFile(uri)
                uploadTask.addOnSuccessListener {
                    fileRef.downloadUrl.addOnSuccessListener {
                        Glide.with(this)
                            .load(
                                it
                            )
                            .placeholder(R.drawable.default_profile_picture)
                            .into(profilePictureView)
                        uploadedImageUri = it
                    }
                    progressDialog.dismiss()
                }
            } else {
                progressDialog.dismiss()
                Snackbar.make(profilePictureView.rootView, "Failed to save the image", Snackbar.LENGTH_LONG)
                    .setAction("CLOSE", {})
                    .show()
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        val textView = findViewById<TextView>(R.id.helloTextView)
        val user = auth.currentUser
        val login = user?.displayName

        if (user != null) {
            val name = login.orEmpty()
            textView?.text = Html.fromHtml("Hello, <b>$name</b>", Html.FROM_HTML_MODE_COMPACT)
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        val clicked = CLASSES[position]
        startActivity(Intent(this, clicked))
    }

    override fun onClick(v: View) {
        startActivity(Intent(this, CameraActivity::class.java))
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.chooser_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sign_out -> {
                Firebase.auth.signOut()
                startActivity(Intent(this, FirebaseLoginActivity::class.java))
                finish()
            }
        }

        return super.onContextItemSelected(item)
    }

    companion object {
        private const val TAG = "ChooserActivity"
        private val CLASSES = arrayOf<Class<*>>(
            CameraActivity::class.java
        )
    }
}
