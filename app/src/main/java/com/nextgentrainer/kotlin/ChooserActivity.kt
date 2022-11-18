package com.nextgentrainer.kotlin

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.nextgentrainer.BuildConfig
import com.nextgentrainer.R

class ChooserActivity : AppCompatActivity(), OnItemClickListener, View.OnClickListener {
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
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_chooser_training)
        val repCounter = findViewById<Button>(R.id.rep_counter_button)
        repCounter.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }
        findViewById<Button>(R.id.compete_button).setOnClickListener {
            startActivity(Intent(this, CompeteActivity::class.java))
        }

        findViewById<Button>(R.id.master_button).setOnClickListener {
            startActivity(Intent(this, ImproveTestActivity::class.java))
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        val clicked = CLASSES[position]
        startActivity(Intent(this, clicked))
    }

    override fun onClick(v: View) {
        startActivity(Intent(this, CameraActivity::class.java))
    }

    companion object {
        private const val TAG = "ChooserActivity"
        private val CLASSES = arrayOf<Class<*>>(
            CameraActivity::class.java
        )
    }
}
