package com.nextgentrainer.kotlin

import android.content.Intent
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
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.nextgentrainer.BuildConfig
import com.nextgentrainer.FirebaseLoginActivity
import com.nextgentrainer.R

class ChooserActivity : AppCompatActivity(), OnItemClickListener, View.OnClickListener {
    private lateinit var auth: FirebaseAuth
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

        val gearButton = findViewById<ImageButton>(R.id.gearImageButton)
        registerForContextMenu(gearButton)
        gearButton.setOnClickListener {
            gearButton.showContextMenu()
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
